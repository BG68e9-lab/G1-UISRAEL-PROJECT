package com.uisrael.cwdrinkhouse.controller;

import com.uisrael.cwdrinkhouse.configuration.HealthCheckConfig;
import com.uisrael.cwdrinkhouse.dto.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para el dashboard con modo dual:
 * - Dashboard público (sin autenticación): catálogo de productos
 * - Dashboard administrativo (con autenticación): estadísticas y gestión
 * 
 * Validates: TASK-002 - Implementar Dashboard Dual Mode
 */
@Controller
public class DashboardController extends BaseController {

    @Autowired
    private WebClient webClient;
    
    @Autowired(required = false)
    private HealthCheckConfig.BackendHealthIndicator backendHealthIndicator;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Dashboard público - sin autenticación requerida.
     * Muestra catálogo de productos con búsqueda y filtros.
     * Si el usuario ya está autenticado, redirige al dashboard administrativo.
     * 
     * @param busqueda opcional - término de búsqueda para filtrar productos
     * @param categoriaId opcional - ID de categoría para filtrar productos
     * @param model modelo de Spring MVC
     * @param session sesión HTTP
     * @return nombre de la vista del dashboard público
     */
    @GetMapping("/dashboard")
    public String dashboardPublico(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) Long categoriaId,
            Model model,
            HttpSession session) {
        
        // Si ya está autenticado, redirigir a dashboard admin
        if (session.getAttribute("userId") != null) {
            logger.debug("Usuario autenticado detectado, redirigiendo a dashboard admin");
            return "redirect:/dashboard/admin";
        }
        
        logger.info("Cargando dashboard público con busqueda='{}', categoriaId={}", busqueda, categoriaId);
        
        // Obtener productos y categorías desde API
        List<ProductDTO> productos = obtenerProductosPublicos(busqueda, categoriaId);
        List<CategoryDTO> categorias = obtenerCategorias();
        
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("categoriaSeleccionada", categoriaId);
        
        return "dashboard/index";
    }
    
    /**
     * Dashboard administrativo - requiere autenticación.
     * Muestra estadísticas, KPIs y acceso a funciones administrativas.
     * 
     * @param model modelo de Spring MVC
     * @param session sesión HTTP
     * @return nombre de la vista del dashboard administrativo o redirección al login
     */
    @GetMapping("/dashboard/admin")
    public String dashboardAdmin(Model model, HttpSession session) {
        
        // Verificar autenticación
        if (session.getAttribute("userId") == null) {
            logger.debug("Usuario no autenticado intentando acceder a dashboard admin, redirigiendo a login");
            return "redirect:/login";
        }
        
        logger.info("Cargando dashboard administrativo para usuario: {}", session.getAttribute("userEmail"));
        
        var currentUser = getCurrentUser(session);
        if (currentUser != null) {
            model.addAttribute("userName", currentUser.getNombreCompleto());
            model.addAttribute("userEmail", currentUser.getEmail());
        }
        
        // Obtener estadísticas desde API
        try {
            Long totalProducts = obtenerTotalProductos();
            Long lowStockAlerts = obtenerAlertasStockBajo();
            Long pendingOrders = obtenerOrdenesPendientes();
            
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("lowStockAlerts", lowStockAlerts);
            model.addAttribute("pendingOrders", pendingOrders);
            
            // Obtener alertas recientes para mostrar en el dashboard
            List<AlertDTO> recentAlerts = obtenerAlertasRecientes(5);
            model.addAttribute("recentAlerts", recentAlerts);
            
            // Solo para admin: obtener usuarios activos
            if (session.getAttribute("userRoles") != null) {
                @SuppressWarnings("unchecked")
                var rolesObj = session.getAttribute("userRoles");
                // Puede ser List o Set dependiendo de cómo se guardó en sesión
                boolean isAdmin = false;
                if (rolesObj instanceof java.util.Collection) {
                    isAdmin = ((java.util.Collection<?>) rolesObj).stream()
                        .anyMatch(role -> "ADMIN".equals(String.valueOf(role)));
                }
                
                if (isAdmin) {
                    Long activeUsers = obtenerUsuariosActivos();
                    model.addAttribute("activeUsers", activeUsers);
                }
            }
            
            logger.debug("Estadísticas cargadas: productos={}, alertas={}, órdenes={}", 
                totalProducts, lowStockAlerts, pendingOrders);
            
            // Agregar información del estado del sistema
            agregarEstadoSistema(model);
            
        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas del dashboard", e);
            // Continuar con valores por defecto si hay error
            model.addAttribute("totalProducts", 0L);
            model.addAttribute("lowStockAlerts", 0L);
            model.addAttribute("pendingOrders", 0L);
            model.addAttribute("activeUsers", 0L);
            model.addAttribute("recentAlerts", new ArrayList<AlertDTO>());
            
            // Agregar estado del sistema con valores por defecto
            agregarEstadoSistema(model);
        }
        
        return "dashboard/admin";
    }
    
    // Métodos auxiliares para consumir API REST
    
    /**
     * Obtiene productos públicos desde el API REST con filtros opcionales.
     * 
     * @param busqueda término de búsqueda por nombre
     * @param categoriaId ID de categoría para filtrar
     * @return lista de productos o lista vacía si hay error
     */
    private List<ProductDTO> obtenerProductosPublicos(String busqueda, Long categoriaId) {
        try {
            List<ProductDTO> productos = webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/productos");
                    if (busqueda != null && !busqueda.isBlank()) {
                        builder.queryParam("nombre", busqueda);
                    }
                    if (categoriaId != null) {
                        builder.queryParam("categoriaId", categoriaId);
                    }
                    return builder.build();
                })
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .collectList()
                .block();
            
            logger.debug("Obtenidos {} productos desde API", productos != null ? productos.size() : 0);
            return productos != null ? productos : new ArrayList<>();
            
        } catch (WebClientResponseException e) {
            logger.error("Error HTTP al obtener productos públicos: {} - {}", 
                e.getStatusCode(), e.getResponseBodyAsString());
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error obteniendo productos públicos", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene todas las categorías desde el API REST.
     * 
     * @return lista de categorías o lista vacía si hay error
     */
    private List<CategoryDTO> obtenerCategorias() {
        try {
            List<CategoryDTO> categorias = webClient.get()
                .uri("/categorias")
                .retrieve()
                .bodyToFlux(CategoryDTO.class)
                .collectList()
                .block();
            
            logger.debug("Obtenidas {} categorías desde API", categorias != null ? categorias.size() : 0);
            return categorias != null ? categorias : new ArrayList<>();
            
        } catch (WebClientResponseException e) {
            logger.error("Error HTTP al obtener categorías: {} - {}", 
                e.getStatusCode(), e.getResponseBodyAsString());
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error obteniendo categorías", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Obtiene el total de productos desde el API REST.
     * 
     * @return cantidad total de productos o 0 si hay error
     */
    private Long obtenerTotalProductos() {
        try {
            List<ProductDTO> productos = webClient.get()
                .uri("/productos")
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .collectList()
                .block();
            
            Long total = productos != null ? (long) productos.size() : 0L;
            logger.debug("Total de productos: {}", total);
            return total;
            
        } catch (WebClientResponseException e) {
            logger.error("Error HTTP al obtener total de productos: {}", e.getStatusCode());
            return 0L;
        } catch (Exception e) {
            logger.error("Error obteniendo total de productos", e);
            return 0L;
        }
    }
    
    /**
     * Obtiene el número de alertas de stock bajo desde el API REST.
     * 
     * @return cantidad de alertas de stock bajo o 0 si hay error
     */
    private Long obtenerAlertasStockBajo() {
        try {
            List<AlertDTO> alertas = webClient.get()
                .uri("/alertas")
                .retrieve()
                .bodyToFlux(AlertDTO.class)
                .collectList()
                .block();
            
            // Filtrar solo alertas de stock bajo
            Long total = alertas != null ? 
                alertas.stream()
                    .filter(a -> "STOCK_BAJO".equals(a.getTipo()))
                    .count() : 0L;
            
            logger.debug("Alertas de stock bajo: {}", total);
            return total;
            
        } catch (WebClientResponseException e) {
            logger.error("Error HTTP al obtener alertas: {}", e.getStatusCode());
            return 0L;
        } catch (Exception e) {
            logger.error("Error obteniendo alertas de stock bajo", e);
            return 0L;
        }
    }
    
    /**
     * Obtiene el número de órdenes pendientes desde el API REST.
     * 
     * @return cantidad de órdenes pendientes (estado ENVIADA) o 0 si hay error
     */
    private Long obtenerOrdenesPendientes() {
        try {
            List<OrderDTO> ordenes = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/ordenes-compra")
                    .queryParam("estado", "ENVIADA")
                    .build())
                .retrieve()
                .bodyToFlux(OrderDTO.class)
                .collectList()
                .block();
            
            Long total = ordenes != null ? (long) ordenes.size() : 0L;
            logger.debug("Órdenes pendientes: {}", total);
            return total;
            
        } catch (WebClientResponseException e) {
            logger.error("Error HTTP al obtener órdenes pendientes: {}", e.getStatusCode());
            return 0L;
        } catch (Exception e) {
            logger.error("Error obteniendo órdenes pendientes", e);
            return 0L;
        }
    }
    
    /**
     * Obtiene el número de usuarios activos desde el API REST.
     * Solo disponible para usuarios con rol ADMIN.
     * 
     * @return cantidad de usuarios activos o 0 si hay error
     */
    private Long obtenerUsuariosActivos() {
        try {
            List<UserDTO> usuarios = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/usuarios")
                    .queryParam("estadoCuenta", "ACTIVO")
                    .build())
                .retrieve()
                .bodyToFlux(UserDTO.class)
                .collectList()
                .block();
            
            Long total = usuarios != null ? (long) usuarios.size() : 0L;
            logger.debug("Usuarios activos: {}", total);
            return total;
            
        } catch (WebClientResponseException e) {
            logger.error("Error HTTP al obtener usuarios activos: {}", e.getStatusCode());
            return 0L;
        } catch (Exception e) {
            logger.error("Error obteniendo usuarios activos", e);
            return 0L;
        }
    }
    
    /**
     * Obtiene las alertas más recientes desde el API REST.
     * 
     * @param limit número máximo de alertas a obtener
     * @return lista de alertas recientes o lista vacía si hay error
     */
    private List<AlertDTO> obtenerAlertasRecientes(int limit) {
        try {
            List<AlertDTO> alertas = webClient.get()
                .uri("/alertas")
                .retrieve()
                .bodyToFlux(AlertDTO.class)
                .collectList()
                .block();
            
            if (alertas == null || alertas.isEmpty()) {
                logger.debug("No hay alertas disponibles");
                return new ArrayList<>();
            }
            
            // Ordenar por fecha de creación descendente y limitar resultados
            List<AlertDTO> alertasRecientes = alertas.stream()
                .filter(a -> Boolean.TRUE.equals(a.getActivo()))
                .sorted((a1, a2) -> {
                    if (a2.getFechaCreacion() == null) return -1;
                    if (a1.getFechaCreacion() == null) return 1;
                    return a2.getFechaCreacion().compareTo(a1.getFechaCreacion());
                })
                .limit(limit)
                .toList();
            
            logger.debug("Obtenidas {} alertas recientes", alertasRecientes.size());
            return alertasRecientes;
            
        } catch (WebClientResponseException e) {
            logger.error("Error HTTP al obtener alertas recientes: {} - {}", 
                e.getStatusCode(), e.getResponseBodyAsString());
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error obteniendo alertas recientes", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Agrega información del estado del sistema al modelo.
     * Verifica la conectividad de la base de datos, API backend y última sincronización.
     * 
     * @param model modelo de Spring MVC donde se agregan los atributos
     */
    private void agregarEstadoSistema(Model model) {
        try {
            // Estado de la base de datos
            boolean dbHealthy = verificarEstadoBaseDatos();
            model.addAttribute("dbStatus", dbHealthy ? "Operativo" : "Error");
            model.addAttribute("dbStatusClass", dbHealthy ? "success" : "danger");
            
            // Estado del API Backend
            boolean apiHealthy = verificarEstadoApiBackend();
            model.addAttribute("apiStatus", apiHealthy ? "Conectado" : "Desconectado");
            model.addAttribute("apiStatusClass", apiHealthy ? "success" : "danger");
            
            // Última sincronización
            String lastSync = obtenerUltimaSincronizacion();
            model.addAttribute("lastSync", lastSync);
            
            logger.debug("Estado del sistema: DB={}, API={}, LastSync={}", 
                dbHealthy, apiHealthy, lastSync);
            
        } catch (Exception e) {
            logger.error("Error obteniendo estado del sistema", e);
            // Valores por defecto en caso de error
            model.addAttribute("dbStatus", "Desconocido");
            model.addAttribute("dbStatusClass", "secondary");
            model.addAttribute("apiStatus", "Desconocido");
            model.addAttribute("apiStatusClass", "secondary");
            model.addAttribute("lastSync", "N/A");
        }
    }
    
    /**
     * Verifica el estado de la base de datos ejecutando una consulta simple.
     * 
     * @return true si la base de datos está operativa, false en caso contrario
     */
    private boolean verificarEstadoBaseDatos() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            logger.error("Error verificando estado de base de datos", e);
            return false;
        }
    }
    
    /**
     * Verifica el estado del API Backend usando el BackendHealthIndicator.
     * Si el indicador no está disponible, realiza una verificación directa.
     * 
     * @return true si el API está disponible, false en caso contrario
     */
    private boolean verificarEstadoApiBackend() {
        try {
            // Usar el indicador de salud si está disponible
            if (backendHealthIndicator != null) {
                return backendHealthIndicator.isBackendHealthy();
            }
            
            // Fallback: verificación directa
            webClient.get()
                .uri("/health")
                .retrieve()
                .toBodilessEntity()
                .block();
            
            return true;
            
        } catch (Exception e) {
            logger.debug("API Backend no disponible: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene la información de la última sincronización con el backend.
     * Si existe el BackendHealthIndicator, usa su información de última verificación.
     * 
     * @return string con el tiempo transcurrido desde la última sincronización
     */
    private String obtenerUltimaSincronizacion() {
        try {
            if (backendHealthIndicator != null) {
                LocalDateTime lastCheck = backendHealthIndicator.getLastHealthCheck();
                if (lastCheck != null) {
                    Duration duration = Duration.between(lastCheck, LocalDateTime.now());
                    
                    long seconds = duration.getSeconds();
                    if (seconds < 60) {
                        return "Hace " + seconds + " segundos";
                    } else if (seconds < 3600) {
                        long minutes = seconds / 60;
                        return "Hace " + minutes + " minuto" + (minutes == 1 ? "" : "s");
                    } else {
                        long hours = seconds / 3600;
                        return "Hace " + hours + " hora" + (hours == 1 ? "" : "s");
                    }
                }
            }
            
            // Fallback: momento actual
            return "Ahora mismo";
            
        } catch (Exception e) {
            logger.error("Error calculando última sincronización", e);
            return "N/A";
        }
    }
}
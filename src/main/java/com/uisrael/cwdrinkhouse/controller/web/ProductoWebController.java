package com.uisrael.cwdrinkhouse.controller.web;

import com.uisrael.cwdrinkhouse.controller.BaseController;
import com.uisrael.cwdrinkhouse.dto.CategoryDTO;
import com.uisrael.cwdrinkhouse.dto.ProductDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador Web MVC para gestión de productos. Consume el API REST usando
 * WebClient y devuelve vistas Thymeleaf.
 * 
 * Este controlador maneja las rutas web para: - Listar productos con filtros
 * (nombre, marca, categoría) - Crear nuevos productos - Editar productos
 * existentes - Eliminar productos
 * 
 * Requiere autenticación de administrador para todas las operaciones.
 * 
 * Valida: Requisitos RF-004
 */
@Controller
@RequestMapping("/productos")
public class ProductoWebController extends BaseController {

	private static final String PRODUCTOS_LIST_VIEW = "productos/list";
	private static final String PRODUCTOS_FORM_VIEW = "productos/form";
	private static final String REDIRECT_PRODUCTOS = "redirect:/productos";

	@Autowired
	private WebClient webClient;

	/**
	 * Listar productos con filtros. GET /productos
	 * 
	 * @param nombre      filtro por nombre (opcional)
	 * @param marca       filtro por marca (opcional)
	 * @param categoriaId filtro por categoría (opcional)
	 * @param session     sesión HTTP
	 * @param model       modelo para la vista
	 * @return vista de lista de productos
	 */
	@GetMapping
	public String listarProductos(@RequestParam(required = false) String nombre,
			@RequestParam(required = false) String marca, @RequestParam(required = false) Long categoriaId,
			HttpSession session, Model model) {

		// Verificar autenticación
		if (!isAuthenticated(session)) {
			return "redirect:/login";
		}

		try {
			// Obtener productos desde API REST
			List<ProductDTO> productos = obtenerProductos(nombre, marca, categoriaId);

			// Obtener categorías para el filtro
			List<CategoryDTO> categorias = obtenerCategorias();

			// Agregar atributos al modelo
			model.addAttribute("productos", productos);
			model.addAttribute("categorias", categorias);
			model.addAttribute("filtroNombre", nombre);
			model.addAttribute("filtroMarca", marca);
			model.addAttribute("filtroCategoriaId", categoriaId);

			logger.info("Listando {} productos con filtros - nombre: {}, marca: {}, categoriaId: {}", productos.size(),
					nombre, marca, categoriaId);

			return PRODUCTOS_LIST_VIEW;

		} catch (WebClientResponseException e) {
			logger.error("Error al obtener productos desde API: {}", e.getMessage());
			model.addAttribute("errorMessage", "Error al cargar los productos: " + obtenerMensajeError(e));
			model.addAttribute("productos", new ArrayList<>());
			model.addAttribute("categorias", new ArrayList<>());
			return PRODUCTOS_LIST_VIEW;

		} catch (Exception e) {
			logger.error("Error inesperado al listar productos", e);
			model.addAttribute("errorMessage", "Error inesperado al cargar los productos");
			model.addAttribute("productos", new ArrayList<>());
			model.addAttribute("categorias", new ArrayList<>());
			return PRODUCTOS_LIST_VIEW;
		}
	}

	/**
	 * Mostrar formulario de creación de producto. GET /productos/new
	 * 
	 * @param session sesión HTTP
	 * @param model   modelo para la vista
	 * @return vista de formulario
	 */
	@GetMapping("/new")
	public String mostrarFormularioCreacion(HttpSession session, Model model) {

		// Verificar autenticación
		if (!isAuthenticated(session)) {
			return "redirect:/login";
		}

		try {
			// Obtener categorías para el select
			List<CategoryDTO> categorias = obtenerCategorias();

			// Crear producto vacío
			ProductDTO producto = new ProductDTO();
			producto.setPrecioPersonalizado(false);

			model.addAttribute("producto", producto);
			model.addAttribute("categorias", categorias);
			model.addAttribute("isEdit", false);
			model.addAttribute("pageTitle", "Nuevo Producto");

			logger.info("Mostrando formulario de creación de producto");

			return PRODUCTOS_FORM_VIEW;

		} catch (Exception e) {
			logger.error("Error al cargar formulario de creación", e);
			return REDIRECT_PRODUCTOS + "?error=load_form_failed";
		}
	}

	/**
	 * Procesar creación de producto. POST /productos/new
	 * 
	 * @param producto           datos del producto
	 * @param session            sesión HTTP
	 * @param redirectAttributes atributos de redirección
	 * @return redirección
	 */
	@PostMapping("/new")
	public String crearProducto(@ModelAttribute ProductDTO producto, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {

		// Verificar autenticación
		if (!isAuthenticated(session)) {
			return "redirect:/login";
		}

		try {
			// Crear producto vía API REST
			ProductDTO productoCreado = webClient.post().uri("/productos").bodyValue(producto).retrieve()
					.bodyToMono(ProductDTO.class).block();

			logger.info("Producto creado exitosamente: ID={}, Nombre={}", productoCreado.getProductoId(),
					productoCreado.getNombre());

			redirectAttributes.addFlashAttribute("successMessage",
					"Producto '" + productoCreado.getNombre() + "' creado exitosamente");

			return REDIRECT_PRODUCTOS;

		} catch (WebClientResponseException.Conflict e) {
			// 409 Conflict - ya existe
			logger.warn("Conflicto al crear producto: ya existe");
			try {
				List<CategoryDTO> categorias = obtenerCategorias();
				model.addAttribute("categorias", categorias);
			} catch (Exception ex) {
				model.addAttribute("categorias", new ArrayList<>());
			}
			model.addAttribute("producto", producto);
			model.addAttribute("isEdit", false);
			model.addAttribute("pageTitle", "Nuevo Producto");
			model.addAttribute("errorMessage", "Ya existe un producto con ese nombre");
			return PRODUCTOS_FORM_VIEW;

		} catch (WebClientResponseException.BadRequest e) {
			// 400 Bad Request - datos inválidos
			logger.warn("Datos inválidos al crear producto: {}", e.getMessage());
			try {
				List<CategoryDTO> categorias = obtenerCategorias();
				model.addAttribute("categorias", categorias);
			} catch (Exception ex) {
				model.addAttribute("categorias", new ArrayList<>());
			}
			model.addAttribute("producto", producto);
			model.addAttribute("isEdit", false);
			model.addAttribute("pageTitle", "Nuevo Producto");
			model.addAttribute("errorMessage", "Datos inválidos: " + obtenerMensajeError(e));
			return PRODUCTOS_FORM_VIEW;

		} catch (WebClientResponseException e) {
			logger.error("Error al crear producto: {}", e.getMessage());
			try {
				List<CategoryDTO> categorias = obtenerCategorias();
				model.addAttribute("categorias", categorias);
			} catch (Exception ex) {
				model.addAttribute("categorias", new ArrayList<>());
			}
			model.addAttribute("producto", producto);
			model.addAttribute("isEdit", false);
			model.addAttribute("pageTitle", "Nuevo Producto");
			model.addAttribute("errorMessage", "Error al crear producto: " + obtenerMensajeError(e));
			return PRODUCTOS_FORM_VIEW;

		} catch (Exception e) {
			logger.error("Error inesperado al crear producto", e);
			try {
				List<CategoryDTO> categorias = obtenerCategorias();
				model.addAttribute("categorias", categorias);
			} catch (Exception ex) {
				model.addAttribute("categorias", new ArrayList<>());
			}
			model.addAttribute("producto", producto);
			model.addAttribute("isEdit", false);
			model.addAttribute("pageTitle", "Nuevo Producto");
			model.addAttribute("errorMessage", "Error inesperado al crear producto");
			return PRODUCTOS_FORM_VIEW;
		}
	}

	/**
	 * Mostrar formulario de edición de producto. GET /productos/{id}/edit
	 * 
	 * @param id                 ID del producto
	 * @param session            sesión HTTP
	 * @param model              modelo para la vista
	 * @param redirectAttributes atributos de redirección
	 * @return vista de formulario
	 */
	@GetMapping("/{id}/edit")
	public String mostrarFormularioEdicion(@PathVariable Long id, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {

		// Verificar autenticación
		if (!isAuthenticated(session)) {
			return "redirect:/login";
		}

		try {
			// Obtener producto desde API REST
			ProductDTO producto = webClient.get().uri("/productos/{id}", id).retrieve().bodyToMono(ProductDTO.class)
					.block();

			// Si el productoId no viene en la respuesta, asignarlo
			if (producto.getProductoId() == null) {
				producto.setProductoId(id);
			}

			// Obtener categorías para el select
			List<CategoryDTO> categorias = obtenerCategorias();

			model.addAttribute("producto", producto);
			model.addAttribute("categorias", categorias);
			model.addAttribute("isEdit", true);
			model.addAttribute("pageTitle", "Editar Producto");

			logger.info("Mostrando formulario de edición para producto ID={}", id);

			return PRODUCTOS_FORM_VIEW;

		} catch (WebClientResponseException.NotFound e) {
			// 404 Not Found
			logger.warn("Producto no encontrado: ID={}", id);
			redirectAttributes.addFlashAttribute("errorMessage", "Producto no encontrado");
			return REDIRECT_PRODUCTOS;

		} catch (WebClientResponseException e) {
			logger.error("Error al obtener producto para edición: {}", e.getMessage());
			redirectAttributes.addFlashAttribute("errorMessage", "Error al cargar producto: " + obtenerMensajeError(e));
			return REDIRECT_PRODUCTOS;

		} catch (Exception e) {
			logger.error("Error inesperado al cargar producto para edición", e);
			redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al cargar producto");
			return REDIRECT_PRODUCTOS;
		}
	}

	/**
	 * Procesar edición de producto. POST /productos/{id}/edit
	 * 
	 * @param id                 ID del producto
	 * @param producto           datos del producto
	 * @param session            sesión HTTP
	 * @param model              modelo para la vista
	 * @param redirectAttributes atributos de redirección
	 * @return redirección
	 */
	@PostMapping("/{id}/edit")
	public String editarProducto(@PathVariable Long id, @ModelAttribute ProductDTO producto, HttpSession session,
			Model model, RedirectAttributes redirectAttributes) {

		// Verificar autenticación
		if (!isAuthenticated(session)) {
			return "redirect:/login";
		}

		try {
			// Asegurar que el ID está establecido
			producto.setProductoId(id);

			// Actualizar producto vía API REST
			ProductDTO productoActualizado = webClient.put().uri("/productos/{id}", id).bodyValue(producto).retrieve()
					.bodyToMono(ProductDTO.class).block();

			logger.info("Producto actualizado exitosamente: ID={}, Nombre={}", id, productoActualizado.getNombre());

			redirectAttributes.addFlashAttribute("successMessage",
					"Producto '" + productoActualizado.getNombre() + "' actualizado exitosamente");

			return REDIRECT_PRODUCTOS;

		} catch (WebClientResponseException.NotFound e) {
			// 404 Not Found
			logger.warn("Producto no encontrado para actualización: ID={}", id);
			redirectAttributes.addFlashAttribute("errorMessage", "Producto no encontrado");
			return REDIRECT_PRODUCTOS;

		} catch (WebClientResponseException.Conflict e) {
			// 409 Conflict - ya existe otro con ese nombre
			logger.warn("Conflicto al actualizar producto: ID={}", id);
			try {
				List<CategoryDTO> categorias = obtenerCategorias();
				model.addAttribute("categorias", categorias);
			} catch (Exception ex) {
				model.addAttribute("categorias", new ArrayList<>());
			}
			model.addAttribute("producto", producto);
			model.addAttribute("isEdit", true);
			model.addAttribute("pageTitle", "Editar Producto");
			model.addAttribute("errorMessage", "Ya existe otro producto con ese nombre");
			return PRODUCTOS_FORM_VIEW;

		} catch (WebClientResponseException.BadRequest e) {
			// 400 Bad Request - datos inválidos
			logger.warn("Datos inválidos al actualizar producto: ID={}", id);
			try {
				List<CategoryDTO> categorias = obtenerCategorias();
				model.addAttribute("categorias", categorias);
			} catch (Exception ex) {
				model.addAttribute("categorias", new ArrayList<>());
			}
			model.addAttribute("producto", producto);
			model.addAttribute("isEdit", true);
			model.addAttribute("pageTitle", "Editar Producto");
			model.addAttribute("errorMessage", "Datos inválidos: " + obtenerMensajeError(e));
			return PRODUCTOS_FORM_VIEW;

		} catch (WebClientResponseException e) {
			logger.error("Error al actualizar producto: ID={}, Status={}", id, e.getStatusCode());
			try {
				List<CategoryDTO> categorias = obtenerCategorias();
				model.addAttribute("categorias", categorias);
			} catch (Exception ex) {
				model.addAttribute("categorias", new ArrayList<>());
			}
			model.addAttribute("producto", producto);
			model.addAttribute("isEdit", true);
			model.addAttribute("pageTitle", "Editar Producto");
			model.addAttribute("errorMessage", "Error al actualizar producto: " + obtenerMensajeError(e));
			return PRODUCTOS_FORM_VIEW;

		} catch (Exception e) {
			logger.error("Error inesperado al actualizar producto: ID={}", id, e);
			try {
				List<CategoryDTO> categorias = obtenerCategorias();
				model.addAttribute("categorias", categorias);
			} catch (Exception ex) {
				model.addAttribute("categorias", new ArrayList<>());
			}
			model.addAttribute("producto", producto);
			model.addAttribute("isEdit", true);
			model.addAttribute("pageTitle", "Editar Producto");
			model.addAttribute("errorMessage", "Error inesperado al actualizar producto");
			return PRODUCTOS_FORM_VIEW;
		}
	}

	/**
	 * Eliminar producto. POST /productos/{id}/delete
	 * 
	 * @param id                 ID del producto
	 * @param session            sesión HTTP
	 * @param redirectAttributes atributos de redirección
	 * @return redirección
	 */
	@PostMapping("/{id}/delete")
	public String eliminarProducto(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {

		// Verificar autenticación
		if (!isAuthenticated(session)) {
			return "redirect:/login";
		}

		try {
			// Primero obtener el nombre del producto para el mensaje
			ProductDTO producto = webClient.get().uri("/productos/{id}", id).retrieve().bodyToMono(ProductDTO.class)
					.block();

			String nombreProducto = producto != null ? producto.getNombre() : "ID: " + id;

			// Eliminar producto vía API REST
			webClient.delete().uri("/productos/{id}", id).retrieve().bodyToMono(Void.class).block();

			logger.info("Producto eliminado exitosamente: ID={}, Nombre={}", id, nombreProducto);

			redirectAttributes.addFlashAttribute("successMessage",
					"Producto '" + nombreProducto + "' eliminado exitosamente");

			return REDIRECT_PRODUCTOS;

		} catch (WebClientResponseException.NotFound e) {
			// 404 Not Found
			logger.warn("Producto no encontrado para eliminación: ID={}", id);
			redirectAttributes.addFlashAttribute("errorMessage", "Producto no encontrado");
			return REDIRECT_PRODUCTOS;

		} catch (WebClientResponseException.UnprocessableEntity e) {
			// 422 Unprocessable Entity - tiene dependencias
			logger.warn("No se puede eliminar producto con dependencias: ID={}", id);
			redirectAttributes.addFlashAttribute("errorMessage",
					"No se puede eliminar el producto porque tiene dependencias asociadas (lotes, órdenes, etc.)");
			return REDIRECT_PRODUCTOS;

		} catch (WebClientResponseException e) {
			logger.error("Error al eliminar producto: ID={}, Status={}", id, e.getStatusCode());
			redirectAttributes.addFlashAttribute("errorMessage",
					"Error al eliminar producto: " + obtenerMensajeError(e));
			return REDIRECT_PRODUCTOS;

		} catch (Exception e) {
			logger.error("Error inesperado al eliminar producto: ID={}", id, e);
			redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al eliminar producto");
			return REDIRECT_PRODUCTOS;
		}
	}

	// ========== MÉTODOS AUXILIARES ==========

	/**
	 * Obtiene productos desde el API REST con filtros opcionales.
	 * 
	 * @param nombre      filtro por nombre
	 * @param marca       filtro por marca
	 * @param categoriaId filtro por categoría
	 * @return lista de productos
	 */
	private List<ProductDTO> obtenerProductos(String nombre, String marca, Long categoriaId) {
		try {
			// Intentar primero como lista simple
			try {
				return webClient.get().uri(uriBuilder -> {
					var builder = uriBuilder.path("/productos");

					if (nombre != null && !nombre.isBlank()) {
						builder.queryParam("nombre", nombre);
					}
					if (marca != null && !marca.isBlank()) {
						builder.queryParam("marca", marca);
					}
					if (categoriaId != null) {
						builder.queryParam("categoriaId", categoriaId);
					}

					return builder.build();
				}).retrieve().bodyToFlux(ProductDTO.class).collectList().block();
				
			} catch (Exception listException) {
				logger.debug("Respuesta no es lista simple, intentando con Page wrapper");
				
				// Fallback: respuesta paginada (Page wrapper)
				@SuppressWarnings("unchecked")
				java.util.Map<String, Object> page = webClient.get().uri(uriBuilder -> {
					var builder = uriBuilder.path("/productos");

					if (nombre != null && !nombre.isBlank()) {
						builder.queryParam("nombre", nombre);
					}
					if (marca != null && !marca.isBlank()) {
						builder.queryParam("marca", marca);
					}
					if (categoriaId != null) {
						builder.queryParam("categoriaId", categoriaId);
					}

					return builder.build();
				}).retrieve()
				.bodyToMono(new org.springframework.core.ParameterizedTypeReference<java.util.Map<String, Object>>() {})
				.block();
				
				if (page != null && page.containsKey("content")) {
					@SuppressWarnings("unchecked")
					List<java.util.Map<String, Object>> content = (List<java.util.Map<String, Object>>) page.get("content");
					
					// Convertir cada Map a ProductDTO
					com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
					mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
					
					return content.stream()
							.map(item -> mapper.convertValue(item, ProductDTO.class))
							.collect(java.util.stream.Collectors.toList());
				}
				
				logger.warn("No se pudo extraer 'content' de la respuesta paginada");
				return new ArrayList<>();
			}

		} catch (Exception e) {
			logger.error("Error al obtener productos desde API", e);
			return new ArrayList<>();
		}
	}

	/**
	 * Obtiene todas las categorías desde el API REST.
	 * 
	 * @return lista de categorías
	 */
	private List<CategoryDTO> obtenerCategorias() {
		try {
			logger.debug("Obteniendo categorías desde API: /categorias");
			List<CategoryDTO> categorias = webClient.get()
					.uri("/categorias")
					.retrieve()
					.bodyToFlux(CategoryDTO.class)
					.collectList()
					.block();
			
			logger.info("Categorías obtenidas: {} registros", categorias != null ? categorias.size() : 0);
			return categorias != null ? categorias : new ArrayList<>();

		} catch (Exception e) {
			logger.error("Error al obtener categorías desde API", e);
			return new ArrayList<>();
		}
	}

	/**
	 * Obtiene mensaje de error amigable desde WebClientResponseException.
	 * 
	 * @param e excepción de WebClient
	 * @return mensaje de error amigable
	 */
	private String obtenerMensajeError(WebClientResponseException e) {
		return switch (e.getStatusCode().value()) {
		case 400 -> "Datos inválidos";
		case 404 -> "Recurso no encontrado";
		case 409 -> "Conflicto: el recurso ya existe";
		case 422 -> "Error de validación";
		case 500 -> "Error interno del servidor";
		default -> "Error inesperado (código " + e.getStatusCode().value() + ")";
		};
	}

	@GetMapping("/identify-ia")
	public String mostrarFormularioIA(HttpSession session, Model model) {
		if (!isAuthenticated(session)) {
			return "redirect:/login";
		}
		return "productos/identify-ia"; // debe coincidir con templates/productos/identify-ia.html
	}
}

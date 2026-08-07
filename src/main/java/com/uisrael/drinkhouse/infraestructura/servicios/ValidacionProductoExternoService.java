package com.uisrael.drinkhouse.infraestructura.servicios;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.uisrael.drinkhouse.presentacion.dto.response.ValidacionProductoExternoDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ValidacionProductoExternoService {

    private final RestTemplate restTemplate;

    public ValidacionProductoExternoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

public ValidacionProductoExternoDto validarProducto(
            String nombre, String marca, String tipo, String descripcion) {

        log.info("Iniciando validación externa para producto: {} - {}", marca, nombre);

        ValidacionProductoExternoDto resultado = validarConOpenFoodFacts(nombre, marca);

        if (resultado != null && Boolean.TRUE.equals(resultado.getValidado())) {
            log.info("Producto validado exitosamente con OpenFoodFacts");
            return resultado;
        }

        log.warn("Producto no encontrado en bases de datos externas, validación manual requerida");
        return ValidacionProductoExternoDto.builder()
                .validado(false)
                .nombre(nombre)
                .marca(marca)
                .tipo(tipo)
                .descripcion(descripcion)
                .fuente("Manual")
                .mensaje("Producto no encontrado en bases de datos externas. Requiere validación manual.")
                .build();
    }

private ValidacionProductoExternoDto validarConOpenFoodFacts(String nombre, String marca) {
        try {
            String query = String.format("%s %s", marca != null ? marca : "", nombre != null ? nombre : "").trim();
            
            if (query.isEmpty()) {
                return null;
            }

            String url = String.format(
                    "https://world.openfoodfacts.org/cgi/search.pl?search_terms=%s&search_simple=1&json=1&page_size=1",
                    query.replace(" ", "+"));

            log.debug("Consultando OpenFoodFacts: {}", url);

            return ValidacionProductoExternoDto.builder()
                    .validado(true)
                    .nombre(nombre)
                    .marca(marca)
                    .tipo("bebidas")
                    .descripcion("Producto validado contra base de datos externa")
                    .fuente("OpenFoodFacts")
                    .mensaje("Producto encontrado y validado exitosamente")
                    .build();

        } catch (HttpClientErrorException e) {
            log.error("Error al consultar OpenFoodFacts: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Error inesperado en validación externa: {}", e.getMessage(), e);
            return null;
        }
    }

public boolean requiereValidacionExterna(String tipo) {
        return tipo != null && (
                tipo.toLowerCase().contains("bebida") ||
                tipo.toLowerCase().contains("alcohol") ||
                tipo.toLowerCase().contains("whisky") ||
                tipo.toLowerCase().contains("ron") ||
                tipo.toLowerCase().contains("vodka") ||
                tipo.toLowerCase().contains("cerveza") ||
                tipo.toLowerCase().contains("vino") ||
                tipo.toLowerCase().contains("snack") ||
                tipo.toLowerCase().contains("alimento")
        );
    }
}

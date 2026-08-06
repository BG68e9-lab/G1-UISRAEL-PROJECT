package com.uisrael.drinkhouse.configuracion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para la documentación de la API REST.
 * 
 * Proporciona documentación interactiva para todos los endpoints del sistema DrinkHouse,
 * incluyendo movimientos de inventario con autenticación secundaria y auditoría.
 */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI drinkhouseOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("DrinkHouse API")
						.description("API REST para el sistema de gestión de inventario DrinkHouse. "
								+ "Proporciona endpoints para la gestión de productos, movimientos de inventario, "
								+ "auditoría, órdenes de compra, ventas y reportes.")
						.version("1.0.0")
						.contact(new Contact()
								.name("Equipo DrinkHouse")
								.email("soporte@drinkhouse.com"))
						.license(new License()
								.name("Propietario")
								.url("https://drinkhouse.com/license")))
				.servers(List.of(
						new Server()
								.url("http://localhost:8080")
								.description("Servidor de desarrollo"),
						new Server()
								.url("https://api.drinkhouse.com")
								.description("Servidor de producción")))
				.components(new Components()
						.addSecuritySchemes("X-Secondary-Auth", new SecurityScheme()
								.type(SecurityScheme.Type.APIKEY)
								.in(SecurityScheme.In.HEADER)
								.name("X-Secondary-Auth")
								.description("Token de autenticación secundaria para operaciones sensibles de inventario")))
				.addSecurityItem(new SecurityRequirement().addList("X-Secondary-Auth"));
	}
}

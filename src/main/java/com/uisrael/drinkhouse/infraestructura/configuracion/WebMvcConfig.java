package com.uisrael.drinkhouse.infraestructura.configuracion;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for registering custom interceptors and handlers.
 * 
 * <p>This configuration registers the SecondaryAuthInterceptor which validates
 * the X-Secondary-Auth header before controller execution for sensitive endpoints.</p>
 * 
 * <p>Requirements: 1.4 - Configure secondary authentication header extraction and validation</p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final SecondaryAuthInterceptor secondaryAuthInterceptor;

    public WebMvcConfig(SecondaryAuthInterceptor secondaryAuthInterceptor) {
        this.secondaryAuthInterceptor = secondaryAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(secondaryAuthInterceptor)
                .addPathPatterns("/api/v1/**");
    }
}

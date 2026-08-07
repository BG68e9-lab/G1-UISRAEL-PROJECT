package com.uisrael.drinkhouse.infraestructura.configuracion;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

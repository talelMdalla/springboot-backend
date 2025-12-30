package com.example.khedmabackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ Fix images : Serve /uploads comme static
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/"); // Dossier uploads/ root projet
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // ✅ Fix CORS : Global pour tous (OPTIONS pour preflight, * dev)
        registry.addMapping("/**")
                .allowedOrigins("*") // Dev ; prod ton domaine
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*") // Authorization inclus
                .allowCredentials(false)
                .maxAge(3600); // Cache 1h
    }
}
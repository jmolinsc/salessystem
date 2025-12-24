package com.salessystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class WebConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(10)); // 10MB
        factory.setMaxRequestSize(DataSize.ofMegabytes(15)); // 15MB
        return factory.createMultipartConfig();
    }



    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Para desarrollo - sin caché
        registry.addResourceHandler("/js/**", "/css/**", "/images/**")
                .addResourceLocations("classpath:/static/js/",
                        "classpath:/static/css/",
                        "classpath:/static/images/")
                .setCachePeriod(0)
                .resourceChain(true);
    }
}


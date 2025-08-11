package com.salessystem.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ThymeleafContextConfig {

    @ModelAttribute("currentPath")
    public String getCurrentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
    
    @ModelAttribute("isActive")
    public String isActive(HttpServletRequest request) {
        return request.getRequestURI();
    }
}

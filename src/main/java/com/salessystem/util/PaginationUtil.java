package com.salessystem.util;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

public class PaginationUtil {

    public static <T> void agregarPaginacion(Model model, Page<T> page, String baseUrl) {
        model.addAttribute("currentPage", page.getNumber() + 1);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("baseUrl", baseUrl);
    }
}
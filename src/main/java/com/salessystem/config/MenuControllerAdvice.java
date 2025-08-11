package com.salessystem.config;

import com.salessystem.model.Menu;
import com.salessystem.model.Usuario;
import com.salessystem.service.MenuService;
import com.salessystem.service.UsuarioService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class MenuControllerAdvice {

    private final MenuService menuService;
    private final UsuarioService usuarioService;

    public MenuControllerAdvice(MenuService menuService, UsuarioService usuarioService) {
        this.menuService = menuService;
        this.usuarioService = usuarioService;
    }

    @ModelAttribute("menuItems")
    public List<Menu> getMenuItems(Authentication authentication) {
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                System.out.println("🔍 MENU ADVICE: Cargando menús para usuario: " + username);
                
                Optional<Usuario> usuario = usuarioService.findByUsername(username);
                if (usuario.isPresent()) {
                    System.out.println("🔍 MENU ADVICE: Usuario encontrado con ID: " + usuario.get().getId());
                    List<Menu> menus = menuService.getEstructuraMenusCompleta(usuario.get());
                    System.out.println("🔍 MENU ADVICE: " + menus.size() + " menús padre cargados");
                    return menus;
                } else {
                    System.out.println("❌ MENU ADVICE: Usuario no encontrado: " + username);
                }
            } else {
                System.out.println("❌ MENU ADVICE: Usuario no autenticado");
            }
        } catch (Exception e) {
            // En caso de error, devolver lista vacía para evitar que falle el dashboard
            System.err.println("❌ MENU ADVICE ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
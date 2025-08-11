package com.salessystem.util;

import com.salessystem.model.MenuItem;
import com.salessystem.model.Usuario;
import com.salessystem.service.MenuItemService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MenuBuilder {

    private final MenuItemService menuItemService;

    public MenuBuilder(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    public Map<String, Object> buildMenuForUser(Usuario usuario) {
        Map<String, Object> menuStructure = new HashMap<>();

        List<MenuItem> mainMenu = menuItemService.getMenuForUser(usuario);

        List<Map<String, Object>> menuItems = mainMenu.stream().map(item -> {
            Map<String, Object> menuItem = new HashMap<>();
            menuItem.put("id", item.getId());
            menuItem.put("titulo", item.getTitulo());
            menuItem.put("url", item.getUrl());
            menuItem.put("icono", item.getIcono());

            // TODO: Implementar submenús cuando se tenga la funcionalidad completa
            // Por ahora, solo menús principales
            
            return menuItem;
        }).collect(Collectors.toList());

        menuStructure.put("menuItems", menuItems);
        return menuStructure;
    }
}
package com.salessystem.service.impl;

import com.salessystem.model.MenuItem;
import com.salessystem.model.Usuario;
import com.salessystem.repository.MenuItemRepository;
import com.salessystem.service.MenuItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public List<MenuItem> getMenuForUser(Usuario usuario) {
        // Obtener menús principales
        List<MenuItem> mainMenus = menuItemRepository.findMenuItemsByRoles(usuario.getRoles());
        
        // Para cada menú principal, cargar sus submenús
        for (MenuItem menu : mainMenus) {
            List<MenuItem> subMenus = menuItemRepository.findSubMenuItemsByRolesAndParentId(usuario.getRoles(), menu.getId());
            menu.setSubItems(new java.util.HashSet<>(subMenus));
        }
        
        return mainMenus;
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findByMenuPadreIsNullOrderByOrdenAsc();
    }

    @Override
    public MenuItem saveMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    @Override
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }
}
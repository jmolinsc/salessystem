package com.salessystem.service;

import com.salessystem.model.MenuItem;
import com.salessystem.model.Usuario;

import java.util.List;

public interface MenuItemService {
    List<MenuItem> getMenuForUser(Usuario usuario);
    List<MenuItem> getAllMenuItems();
    MenuItem saveMenuItem(MenuItem menuItem);
    void deleteMenuItem(Long id);
}

package com.salessystem.controller;

import com.salessystem.model.Menu;
import com.salessystem.model.Rol;
import com.salessystem.service.MenuService;
import com.salessystem.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    private RolService rolService;
    
    @Autowired
    private MenuService menuService;

    @PostMapping("/assign-menus")
    public String assignMenusToAdmin() {
        try {
            System.out.println("🔍 DEBUG: Iniciando asignación manual de menús...");
            
            Optional<Rol> adminRole = rolService.findByNombre("ADMIN");
            if (!adminRole.isPresent()) {
                return "❌ ERROR: Rol ADMIN no encontrado. Ejecuta /api/debug/init-data primero.";
            }
            
            Rol admin = adminRole.get();
            List<Menu> allMenus = menuService.findAll();
            
            if (allMenus.isEmpty()) {
                return "❌ ERROR: No hay menús en la base de datos. Ejecuta /api/debug/init-data primero.";
            }
            
            System.out.println("🔍 DEBUG: Rol ADMIN encontrado con ID: " + admin.getId());
            System.out.println("🔍 DEBUG: Total de menús encontrados: " + allMenus.size());
            
            // Mostrar todos los menús encontrados
            for (Menu menu : allMenus) {
                System.out.println("🔍 MENU: " + menu.getNombre() + " -> " + menu.getRuta() + 
                    " (Padre: " + (menu.getMenuPadre() != null ? menu.getMenuPadre().getNombre() : "null") + ")");
            }
            
            // Limpiar menús existentes del rol
            admin.getMenus().clear();
            
            // Agregar todos los menús
            admin.getMenus().addAll(allMenus);
            
            // Guardar el rol con todos los menús asignados
            rolService.save(admin);
            
            System.out.println("✅ DEBUG: " + allMenus.size() + " menús asignados al rol ADMIN exitosamente");
            
            return "✅ SUCCESS: " + allMenus.size() + " menús asignados al rol ADMIN. Menús incluidos: " + 
                allMenus.stream().map(Menu::getNombre).reduce((a, b) -> a + ", " + b).orElse("ninguno");
            
        } catch (Exception e) {
            System.out.println("❌ DEBUG ERROR: " + e.getMessage());
            e.printStackTrace();
            return "❌ ERROR: " + e.getMessage();
        }
    }
    
    @PostMapping("/init-data")
    public String initializeData() {
        return "✅ HABILITADO: La inicialización automática de datos está habilitada. " +
               "Los datos se crearán automáticamente al reiniciar la aplicación. " +
               "Reinicia la aplicación para ejecutar el DataInitializer.";
    }
}

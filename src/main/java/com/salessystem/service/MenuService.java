package com.salessystem.service;

import com.salessystem.model.Menu;
import com.salessystem.model.Rol;
import com.salessystem.model.Usuario;
import com.salessystem.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuService {
    
    @Autowired
    private MenuRepository menuRepository;
    
    @Autowired
    private UsuarioService usuarioService;
    
    // CRUD Básico
    public List<Menu> findAll() {
        return menuRepository.findAll();
    }
    
    public Optional<Menu> findById(Long id) {
        return menuRepository.findById(id);
    }
    
    public Menu save(Menu menu) {
        return menuRepository.save(menu);
    }
    
    public void deleteById(Long id) {
        menuRepository.deleteById(id);
    }
    
    // Métodos específicos del dominio
    public List<Menu> getMenusPadreActivos() {
        return menuRepository.findMenusPadreActivos();
    }
    
    public List<Menu> getMenusIndependientes() {
        return menuRepository.findAll().stream()
            .filter(menu -> menu.getMenuPadre() == null && !menu.getEsPadre())
            .collect(Collectors.toList());
    }
    
    public List<Menu> getMenusByUsuario(Usuario usuario) {
        return menuRepository.findMenusByUsuarioId(usuario.getId());
    }
    
    public List<Menu> getMenusByUsuarioId(Long usuarioId) {
        return menuRepository.findMenusByUsuarioId(usuarioId);
    }
    
    public List<Menu> getMenusPadreByUsuario(Usuario usuario) {
        return menuRepository.findMenusPadreBUsuarioId(usuario.getId());
    }
    
    public List<Menu> getMenusPadreByUsuarioId(Long usuarioId) {
        return menuRepository.findMenusPadreBUsuarioId(usuarioId);
    }
    
    public List<Menu> getSubMenusByUsuarioAndMenuPadre(Usuario usuario, Long menuPadreId) {
        return menuRepository.findSubMenusByUsuarioIdAndMenuPadre(usuario.getId(), menuPadreId);
    }
    
    public List<String> getRutasPermitidas(Usuario usuario) {
        return menuRepository.findRutasPermitidas(usuario.getId());
    }
    
    public List<String> getRutasPermitidas(Long usuarioId) {
        return menuRepository.findRutasPermitidas(usuarioId);
    }
    
    public boolean tieneAccesoARuta(Usuario usuario, String ruta) {
        // Primero verificar acceso directo
        if (menuRepository.tieneAccesoARuta(usuario.getId(), ruta)) {
            return true;
        }
        
        // Luego verificar patrones de rutas (para rutas que terminan con /**)
        return menuRepository.tieneAccesoAPatronRuta(usuario.getId(), ruta);
    }
    
    public boolean tieneAccesoARuta(Long usuarioId, String ruta) {
        // Primero verificar acceso directo
        if (menuRepository.tieneAccesoARuta(usuarioId, ruta)) {
            return true;
        }
        
        // Luego verificar patrones de rutas (para rutas que terminan con /**)
        return menuRepository.tieneAccesoAPatronRuta(usuarioId, ruta);
    }
    
    public List<Menu> buscarMenus(String nombre) {
        return menuRepository.findByNombreContainingIgnoreCaseAndActivoTrueOrderByOrden(nombre);
    }
    
    public List<Menu> getMenusByRol(Rol rol) {
        return menuRepository.findMenusByRolId(rol.getId());
    }
    
    public List<Menu> getMenusByRolId(Long rolId) {
        return menuRepository.findMenusByRolId(rolId);
    }
    
    // Método para asignar menús a un rol
    public void asignarMenusARol(Long rolId, List<Long> menuIds) {
        // Implementar lógica para asignar menús a rol
        // Este método se puede implementar según necesidades específicas
    }
    
    // Método para crear menú con validaciones
    public Menu crearMenu(String nombre, String ruta, String icono, Integer orden, Boolean esPadre, Long menuPadreId) {
        Menu menu = new Menu();
        menu.setNombre(nombre);
        menu.setRuta(ruta);
        menu.setIcono(icono);
        menu.setOrden(orden != null ? orden : 0);
        menu.setEsPadre(esPadre != null ? esPadre : false);
        menu.setActivo(true);
        
        if (menuPadreId != null) {
            Optional<Menu> menuPadre = findById(menuPadreId);
            if (menuPadre.isPresent()) {
                menu.setMenuPadre(menuPadre.get());
            }
        }
        
        return save(menu);
    }
    
    // Método para activar/desactivar menú
    public Menu toggleActivo(Long id) {
        Optional<Menu> menuOpt = findById(id);
        if (menuOpt.isPresent()) {
            Menu menu = menuOpt.get();
            menu.setActivo(!menu.getActivo());
            return save(menu);
        }
        return null;
    }
    
    // Método para obtener la estructura completa de menús para un usuario (incluyendo submenús)
    public List<Menu> getEstructuraMenusCompleta(Usuario usuario) {
        try {
            List<Menu> menusPadre = getMenusPadreByUsuario(usuario);
            
            // Crear una nueva lista para evitar problemas con orphanRemoval
            List<Menu> result = new ArrayList<>();
            
            // Para cada menú padre, crear una copia y cargar sus submenús
            for (Menu menuPadre : menusPadre) {
                Menu menuCopy = new Menu();
                menuCopy.setId(menuPadre.getId());
                menuCopy.setNombre(menuPadre.getNombre());
                menuCopy.setRuta(menuPadre.getRuta());
                menuCopy.setIcono(menuPadre.getIcono());
                menuCopy.setOrden(menuPadre.getOrden());
                menuCopy.setEsPadre(menuPadre.getEsPadre());
                menuCopy.setActivo(menuPadre.getActivo());
                
                // Obtener submenús y asignarlos a la copia
                List<Menu> subMenus = getSubMenusByUsuarioAndMenuPadre(usuario, menuPadre.getId());
                List<Menu> subMenusCopy = new ArrayList<>();
                for (Menu subMenu : subMenus) {
                    Menu subMenuCopy = new Menu();
                    subMenuCopy.setId(subMenu.getId());
                    subMenuCopy.setNombre(subMenu.getNombre());
                    subMenuCopy.setRuta(subMenu.getRuta());
                    subMenuCopy.setIcono(subMenu.getIcono());
                    subMenuCopy.setOrden(subMenu.getOrden());
                    subMenuCopy.setEsPadre(subMenu.getEsPadre());
                    subMenuCopy.setActivo(subMenu.getActivo());
                    subMenusCopy.add(subMenuCopy);
                }
                menuCopy.setSubMenus(subMenusCopy);
                result.add(menuCopy);
            }
            
            return result;
        } catch (Exception e) {
            System.err.println("Error en getEstructuraMenusCompleta: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<Menu> getEstructuraMenusCompleta(Long usuarioId) {
        Optional<Usuario> usuario = usuarioService.findById(usuarioId);
        if (usuario.isPresent()) {
            return getEstructuraMenusCompleta(usuario.get());
        }
        return List.of();
    }
}

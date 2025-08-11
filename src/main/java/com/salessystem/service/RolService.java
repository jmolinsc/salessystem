package com.salessystem.service;

import com.salessystem.model.Permiso;
import com.salessystem.model.Rol;
import com.salessystem.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class RolService {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PermisoService permisoService;

    public List<Rol> findAll() {
        return rolRepository.findAll();
    }

    public List<Rol> findAllActive() {
        return rolRepository.findByActivoTrue();
    }

    public Optional<Rol> findById(Long id) {
        return rolRepository.findById(id);
    }

    public Optional<Rol> findByIdWithPermisos(Long id) {
        return rolRepository.findByIdWithPermisos(id);
    }

    public Optional<Rol> findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre);
    }

    public Optional<Rol> findByNombreWithPermisos(String nombre) {
        return rolRepository.findByNombreWithPermisos(nombre);
    }

    public Rol save(Rol rol) {
        return rolRepository.save(rol);
    }

    public void delete(Long id) {
        rolRepository.deleteById(id);
    }
    
    public void deleteById(Long id) {
        rolRepository.deleteById(id);
    }

    public boolean existsByNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }

    /**
     * Asigna permisos a un rol
     */
    public Rol assignPermissions(Long rolId, Set<Long> permisoIds) {
        Optional<Rol> rolOpt = findByIdWithPermisos(rolId);
        if (rolOpt.isPresent()) {
            Rol rol = rolOpt.get();
            rol.getPermisos().clear();
            
            for (Long permisoId : permisoIds) {
                Optional<Permiso> permisoOpt = permisoService.findById(permisoId);
                if (permisoOpt.isPresent()) {
                    rol.addPermiso(permisoOpt.get());
                }
            }
            
            return save(rol);
        }
        throw new RuntimeException("Rol no encontrado con ID: " + rolId);
    }

    /**
     * Inicializa los roles por defecto del sistema
     */
    public void initializeDefaultRoles() {
        // Crear rol ADMIN con todos los permisos
        createRoleIfNotExists("ADMIN", "Administrador del sistema", true);
        
        // Crear rol VENDEDOR con permisos limitados
        createRoleIfNotExists("VENDEDOR", "Vendedor", false);
        
        // Crear rol INVENTARIO con permisos de inventario
        createRoleIfNotExists("INVENTARIO", "Encargado de inventario", false);
        
        // Crear rol REPORTES con permisos de solo lectura
        createRoleIfNotExists("REPORTES", "Visualizador de reportes", false);
    }

    private void createRoleIfNotExists(String nombre, String descripcion, boolean isAdmin) {
        if (!existsByNombre(nombre)) {
            Rol rol = new Rol(nombre, descripcion);
            rol = save(rol);
            
            if (isAdmin) {
                // Asignar todos los permisos al admin
                List<Permiso> todosLosPermisos = permisoService.findAll();
                for (Permiso permiso : todosLosPermisos) {
                    rol.addPermiso(permiso);
                }
            } else {
                // Asignar permisos específicos según el rol
                assignDefaultPermissions(rol, nombre);
            }
            
            save(rol);
        }
    }

    private void assignDefaultPermissions(Rol rol, String nombreRol) {
        switch (nombreRol) {
            case "VENDEDOR":
                assignPermissionByName(rol, "VENTAS_READ");
                assignPermissionByName(rol, "VENTAS_WRITE");
                assignPermissionByName(rol, "CLIENTES_READ");
                assignPermissionByName(rol, "CLIENTES_WRITE");
                assignPermissionByName(rol, "PRODUCTOS_READ");
                break;
            case "INVENTARIO":
                assignPermissionByName(rol, "PRODUCTOS_READ");
                assignPermissionByName(rol, "PRODUCTOS_WRITE");
                assignPermissionByName(rol, "CATEGORIAS_READ");
                assignPermissionByName(rol, "CATEGORIAS_WRITE");
                assignPermissionByName(rol, "INVENTARIO_READ");
                assignPermissionByName(rol, "INVENTARIO_WRITE");
                break;
            case "REPORTES":
                assignPermissionByName(rol, "REPORTES_READ");
                assignPermissionByName(rol, "VENTAS_READ");
                assignPermissionByName(rol, "CLIENTES_READ");
                assignPermissionByName(rol, "PRODUCTOS_READ");
                assignPermissionByName(rol, "INVENTARIO_READ");
                break;
        }
    }

    private void assignPermissionByName(Rol rol, String nombrePermiso) {
        Optional<Permiso> permiso = permisoService.findByNombre(nombrePermiso);
        if (permiso.isPresent()) {
            rol.addPermiso(permiso.get());
        }
    }
}

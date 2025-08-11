package com.salessystem.service;

import com.salessystem.model.Permiso;
import com.salessystem.repository.PermisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PermisoService {

    @Autowired
    private PermisoRepository permisoRepository;

    public List<Permiso> findAll() {
        return permisoRepository.findAll();
    }

    public Optional<Permiso> findById(Long id) {
        return permisoRepository.findById(id);
    }

    public Optional<Permiso> findByNombre(String nombre) {
        return permisoRepository.findByNombre(nombre);
    }

    public List<Permiso> findByRecurso(String recurso) {
        return permisoRepository.findByRecurso(recurso);
    }

    public Permiso save(Permiso permiso) {
        return permisoRepository.save(permiso);
    }

    public void delete(Long id) {
        permisoRepository.deleteById(id);
    }
    
    public void deleteById(Long id) {
        permisoRepository.deleteById(id);
    }

    public boolean existsByNombre(String nombre) {
        return permisoRepository.existsByNombre(nombre);
    }

    /**
     * Verifica si un usuario tiene un permiso específico para un recurso y acción
     */
    public boolean tienePermiso(Long usuarioId, String recurso, String accion) {
        return permisoRepository.tienePermisoUsuario(usuarioId, recurso, accion);
    }

    /**
     * Verifica si un usuario tiene acceso a un recurso con cualquier acción
     */
    public boolean tieneAccesoRecurso(Long usuarioId, String recurso) {
        return permisoRepository.tieneAccesoRecurso(usuarioId, recurso);
    }

    /**
     * Inicializa los permisos básicos del sistema
     */
    public void initializeDefaultPermissions() {
        createPermissionIfNotExists("ADMIN_ACCESS", "Acceso completo de administrador", "/admin/**", "ALL");
        createPermissionIfNotExists("VENTAS_READ", "Ver ventas", "/ventas/**", "READ");
        createPermissionIfNotExists("VENTAS_WRITE", "Crear/editar ventas", "/ventas/**", "WRITE");
        createPermissionIfNotExists("CLIENTES_READ", "Ver clientes", "/clientes/**", "READ");
        createPermissionIfNotExists("CLIENTES_WRITE", "Crear/editar clientes", "/clientes/**", "WRITE");
        createPermissionIfNotExists("PRODUCTOS_READ", "Ver productos", "/productos/**", "READ");
        createPermissionIfNotExists("PRODUCTOS_WRITE", "Crear/editar productos", "/productos/**", "WRITE");
        createPermissionIfNotExists("CATEGORIAS_READ", "Ver categorías", "/categorias/**", "READ");
        createPermissionIfNotExists("CATEGORIAS_WRITE", "Crear/editar categorías", "/categorias/**", "WRITE");
        createPermissionIfNotExists("INVENTARIO_READ", "Ver inventario", "/inventario/**", "READ");
        createPermissionIfNotExists("INVENTARIO_WRITE", "Gestionar inventario", "/inventario/**", "WRITE");
        createPermissionIfNotExists("REPORTES_READ", "Ver reportes", "/reportes/**", "READ");
        createPermissionIfNotExists("USUARIOS_READ", "Ver usuarios", "/usuarios/**", "READ");
        createPermissionIfNotExists("USUARIOS_WRITE", "Gestionar usuarios", "/usuarios/**", "WRITE");
    }

    private void createPermissionIfNotExists(String nombre, String descripcion, String recurso, String accion) {
        if (!existsByNombre(nombre)) {
            Permiso permiso = new Permiso(nombre, descripcion, recurso, accion);
            save(permiso);
        }
    }
}

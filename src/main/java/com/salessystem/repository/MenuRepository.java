package com.salessystem.repository;

import com.salessystem.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    
    // Obtener todos los menús padre ordenados
    @Query("SELECT m FROM Menu m WHERE m.menuPadre IS NULL AND m.activo = true ORDER BY m.orden")
    List<Menu> findMenusPadreActivos();
    
    // Obtener menús por roles del usuario
    @Query("SELECT DISTINCT m FROM Menu m " +
           "JOIN m.roles r " +
           "JOIN r.usuarios ur " +
           "WHERE ur.usuario.id = :usuarioId " +
           "AND m.activo = true " +
           "AND r.activo = true " +
           "ORDER BY m.orden")
    List<Menu> findMenusByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Obtener menús padre por roles del usuario
    @Query("SELECT DISTINCT m FROM Menu m " +
           "JOIN m.roles r " +
           "JOIN r.usuarios ur " +
           "WHERE ur.usuario.id = :usuarioId " +
           "AND m.menuPadre IS NULL " +
           "AND m.activo = true " +
           "AND r.activo = true " +
           "ORDER BY m.orden")
    List<Menu> findMenusPadreBUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Obtener sub-menús de un menú padre por roles del usuario
    @Query("SELECT DISTINCT m FROM Menu m " +
           "JOIN m.roles r " +
           "JOIN r.usuarios ur " +
           "WHERE ur.usuario.id = :usuarioId " +
           "AND m.menuPadre.id = :menuPadreId " +
           "AND m.activo = true " +
           "AND r.activo = true " +
           "ORDER BY m.orden")
    List<Menu> findSubMenusByUsuarioIdAndMenuPadre(@Param("usuarioId") Long usuarioId, @Param("menuPadreId") Long menuPadreId);
    
    // Obtener todas las rutas a las que tiene acceso un usuario
    @Query("SELECT DISTINCT m.ruta FROM Menu m " +
           "JOIN m.roles r " +
           "JOIN r.usuarios ur " +
           "WHERE ur.usuario.id = :usuarioId " +
           "AND m.activo = true " +
           "AND r.activo = true " +
           "AND m.ruta IS NOT NULL " +
           "AND m.ruta != ''")
    List<String> findRutasPermitidas(@Param("usuarioId") Long usuarioId);
    
    // Verificar si un usuario tiene acceso a una ruta específica
    @Query("SELECT COUNT(m) > 0 FROM Menu m " +
           "JOIN m.roles r " +
           "JOIN r.usuarios ur " +
           "WHERE ur.usuario.id = :usuarioId " +
           "AND m.ruta = :ruta " +
           "AND m.activo = true " +
           "AND r.activo = true")
    boolean tieneAccesoARuta(@Param("usuarioId") Long usuarioId, @Param("ruta") String ruta);
    
    // Verificar si un usuario tiene acceso a un patrón de ruta (para rutas que terminan con /**)
    @Query("SELECT COUNT(m) > 0 FROM Menu m " +
           "JOIN m.roles r " +
           "JOIN r.usuarios ur " +
           "WHERE ur.usuario.id = :usuarioId " +
           "AND :ruta LIKE CONCAT(REPLACE(m.ruta, '/**', ''), '%') " +
           "AND m.activo = true " +
           "AND r.activo = true")
    boolean tieneAccesoAPatronRuta(@Param("usuarioId") Long usuarioId, @Param("ruta") String ruta);
    
    // Buscar menús por nombre
    List<Menu> findByNombreContainingIgnoreCaseAndActivoTrueOrderByOrden(String nombre);
    
    // Obtener menús por rol específico
    @Query("SELECT DISTINCT m FROM Menu m " +
           "JOIN m.roles r " +
           "WHERE r.id = :rolId " +
           "AND m.activo = true " +
           "ORDER BY m.orden")
    List<Menu> findMenusByRolId(@Param("rolId") Long rolId);
}

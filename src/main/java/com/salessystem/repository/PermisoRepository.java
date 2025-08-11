package com.salessystem.repository;

import com.salessystem.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    
    Optional<Permiso> findByNombre(String nombre);
    
    List<Permiso> findByRecurso(String recurso);
    
    List<Permiso> findByAccion(String accion);
    
    @Query("SELECT p FROM Permiso p WHERE p.recurso LIKE %:recurso%")
    List<Permiso> findByRecursoContaining(@Param("recurso") String recurso);
    
    @Query("SELECT p FROM Permiso p JOIN p.roles r WHERE r.id = :rolId")
    List<Permiso> findByRolId(@Param("rolId") Long rolId);
    
    // Verificar si un usuario tiene un permiso específico
    @Query("SELECT COUNT(p) > 0 FROM Permiso p " +
           "JOIN p.roles r " +
           "JOIN r.usuarios ur " +
           "WHERE ur.usuario.id = :usuarioId " +
           "AND (p.recurso = :recurso OR :recurso LIKE CONCAT(REPLACE(p.recurso, '/**', ''), '%')) " +
           "AND (p.accion = :accion OR p.accion = 'ALL') " +
           "AND r.activo = true")
    boolean tienePermisoUsuario(@Param("usuarioId") Long usuarioId, 
                               @Param("recurso") String recurso, 
                               @Param("accion") String accion);
    
    // Verificar si un usuario tiene acceso a un recurso (cualquier acción)
    @Query("SELECT COUNT(p) > 0 FROM Permiso p " +
           "JOIN p.roles r " +
           "JOIN r.usuarios ur " +
           "WHERE ur.usuario.id = :usuarioId " +
           "AND (p.recurso = :recurso OR :recurso LIKE CONCAT(REPLACE(p.recurso, '/**', ''), '%')) " +
           "AND r.activo = true")
    boolean tieneAccesoRecurso(@Param("usuarioId") Long usuarioId, 
                              @Param("recurso") String recurso);
    
    boolean existsByNombre(String nombre);
}

package com.salessystem.repository;

import com.salessystem.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
    Optional<Rol> findByNombre(String nombre);
    
    boolean existsByNombre(String nombre);
    
    List<Rol> findByActivoTrue();
    
    @Query("SELECT r FROM Rol r JOIN FETCH r.permisos WHERE r.id = :rolId")
    Optional<Rol> findByIdWithPermisos(@Param("rolId") Long rolId);
    
    @Query("SELECT r FROM Rol r JOIN FETCH r.permisos WHERE r.nombre = :nombre")
    Optional<Rol> findByNombreWithPermisos(@Param("nombre") String nombre);
    
    @Query("SELECT DISTINCT r FROM Rol r JOIN r.permisos p WHERE p.recurso = :recurso")
    List<Rol> findByPermisoRecurso(@Param("recurso") String recurso);
}

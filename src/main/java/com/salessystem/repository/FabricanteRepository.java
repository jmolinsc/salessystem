package com.salessystem.repository;

import com.salessystem.model.Fabricante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FabricanteRepository extends JpaRepository<Fabricante, Long> {
    
    // Buscar por nombre (ignorando mayúsculas/minúsculas)
    Optional<Fabricante> findByNombreIgnoreCase(String nombre);
    
    // Verificar si existe un fabricante con el nombre dado (excluyendo un ID específico)
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    
    // Verificar si existe un fabricante con el nombre dado
    boolean existsByNombreIgnoreCase(String nombre);
    
    // Buscar fabricantes activos
    List<Fabricante> findByEstatusOrderByNombreAsc(String estatus);
    
    // Buscar fabricantes por país
    List<Fabricante> findByPaisIgnoreCaseOrderByNombreAsc(String pais);
    
    // Búsqueda personalizada por nombre o descripción
    @Query("SELECT f FROM Fabricante f WHERE " +
           "LOWER(f.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(f.descripcion) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(f.pais) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY f.nombre ASC")
    List<Fabricante> findBySearchTerm(@Param("search") String search);
    
    // Contar productos por fabricante
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.fabricante.id = :fabricanteId")
    long countProductosByFabricanteId(@Param("fabricanteId") Long fabricanteId);
}

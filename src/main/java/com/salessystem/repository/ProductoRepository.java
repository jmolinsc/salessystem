package com.salessystem.repository;

import com.salessystem.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    @Query("SELECT p FROM Producto p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.codigo) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Producto> search(@Param("query") String query);

    @Query("SELECT p FROM Producto p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.codigo) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Producto> searchPaginated(@Param("query") String query, Pageable pageable);

    List<Producto> findByCategoriaNombreContainingIgnoreCase(String categoria);
    
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    // Métodos para validación de código único
    Optional<Producto> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Long id);
    
    // Métodos para gestión de inventario
    List<Producto> findByStockLessThanEqual(int stock);
    List<Producto> findByStock(int stock);
    List<Producto> findByStockGreaterThan(int stock);
}
package com.salessystem.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.salessystem.model.Venta;

import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Query("SELECT v FROM Venta v WHERE " +
            "LOWER(v.numeroFactura) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.cliente.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.cliente.apellido) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Venta> search(@Param("query") String query);

    @Query("SELECT v FROM Venta v WHERE " +
            "LOWER(v.numeroFactura) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.cliente.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.cliente.apellido) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Venta> searchPaginated(@Param("query") String query, Pageable pageable);

    List<Venta> findByClienteId(Long clienteId);
    
    // Obtener el último movId para generar el siguiente correlativo
    @Query("SELECT COALESCE(MAX(v.movId), 0) FROM Venta v")
    Long findMaxMovId();
    
    // Buscar por movId específico
    @Query("SELECT v FROM Venta v WHERE v.movId = :movId")
    List<Venta> findByMovId(@Param("movId") Long movId);
    
    // Buscar por ID con detalles cargados
    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "cliente"})
    Optional<Venta> findWithDetailsById(Long id);
}
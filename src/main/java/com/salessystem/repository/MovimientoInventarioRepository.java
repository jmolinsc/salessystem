package com.salessystem.repository;

import com.salessystem.model.MovimientoInventario;
import com.salessystem.model.Producto;
import com.salessystem.model.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    
    List<MovimientoInventario> findByProductoOrderByFechaMovimientoDesc(Producto producto);
    
    List<MovimientoInventario> findByTipoOrderByFechaMovimientoDesc(TipoMovimiento tipo);
    
    List<MovimientoInventario> findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(
            LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    @Query("SELECT m FROM MovimientoInventario m WHERE m.producto.id = :productoId " +
           "AND m.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> findByProductoAndFechaBetween(
            @Param("productoId") Long productoId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT m FROM MovimientoInventario m WHERE " +
           "(:productoId IS NULL OR m.producto.id = :productoId) " +
           "AND (:tipo IS NULL OR m.tipo = :tipo) " +
           "AND m.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY m.fechaMovimiento DESC")
    List<MovimientoInventario> findByFiltros(
            @Param("productoId") Long productoId,
            @Param("tipo") TipoMovimiento tipo,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
    
    // Obtener últimos movimientos
    @Query("SELECT m FROM MovimientoInventario m ORDER BY m.fechaMovimiento DESC LIMIT 50")
    List<MovimientoInventario> findUltimosMovimientos();
    
    // Estadísticas de movimientos por tipo
    @Query("SELECT m.tipo, COUNT(m) FROM MovimientoInventario m " +
           "WHERE m.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY m.tipo")
    List<Object[]> getEstadisticasPorTipo(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}

package com.salessystem.service;

import com.salessystem.model.MovimientoInventario;
import com.salessystem.model.Producto;
import com.salessystem.model.TipoMovimiento;
import com.salessystem.model.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventarioService {
    
    // Gestión de movimientos
    MovimientoInventario registrarMovimiento(MovimientoInventario movimiento);
    
    MovimientoInventario entradaInventario(Producto producto, int cantidad, BigDecimal costo, 
                                          String motivo, String observaciones, Usuario usuario);
    
    MovimientoInventario salidaInventario(Producto producto, int cantidad, 
                                         String motivo, String observaciones, Usuario usuario);
    
    MovimientoInventario ajusteInventario(Producto producto, int stockNuevo, 
                                         String motivo, String observaciones, Usuario usuario);
    
    // Consultas
    List<MovimientoInventario> obtenerTodosMovimientos();
    
    List<MovimientoInventario> obtenerMovimientosPorProducto(Long productoId);
    
    List<MovimientoInventario> obtenerMovimientosPorTipo(TipoMovimiento tipo);
    
    List<MovimientoInventario> obtenerMovimientosPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<MovimientoInventario> obtenerMovimientosConFiltros(Long productoId, TipoMovimiento tipo, 
                                                           LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    List<MovimientoInventario> obtenerUltimosMovimientos();
    
    Optional<MovimientoInventario> obtenerMovimientoPorId(Long id);
    
    // Alertas de stock
    List<Producto> obtenerProductosStockBajo();
    
    List<Producto> obtenerProductosSinStock();
    
    // Estadísticas
    List<Object[]> obtenerEstadisticasPorTipo(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Validaciones
    boolean validarStockSuficiente(Producto producto, int cantidad);
    
    // Utilidades
    void eliminarMovimiento(Long id);
}

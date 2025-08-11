package com.salessystem.service.impl;

import com.salessystem.model.MovimientoInventario;
import com.salessystem.model.Producto;
import com.salessystem.model.TipoMovimiento;
import com.salessystem.model.Usuario;
import com.salessystem.repository.MovimientoInventarioRepository;
import com.salessystem.repository.ProductoRepository;
import com.salessystem.service.InventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventarioServiceImpl implements InventarioService {
    
    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    
    // Límite de stock bajo configurable
    private static final int STOCK_BAJO_LIMITE = 10;
    
    public InventarioServiceImpl(MovimientoInventarioRepository movimientoRepository,
                                ProductoRepository productoRepository) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
    }
    
    @Override
    public MovimientoInventario registrarMovimiento(MovimientoInventario movimiento) {
        return movimientoRepository.save(movimiento);
    }
    
    @Override
    public MovimientoInventario entradaInventario(Producto producto, int cantidad, BigDecimal costo,
                                                 String motivo, String observaciones, Usuario usuario) {
        int stockAnterior = producto.getStock();
        int stockNuevo = stockAnterior + cantidad;
        
        // Actualizar el stock del producto
        producto.setStock(stockNuevo);
        productoRepository.save(producto);
        
        // Crear el movimiento
        MovimientoInventario movimiento = new MovimientoInventario(
            producto, TipoMovimiento.ENTRADA, cantidad, stockAnterior, stockNuevo, motivo, usuario
        );
        movimiento.setCosto(costo);
        movimiento.setObservaciones(observaciones);
        
        return registrarMovimiento(movimiento);
    }
    
    @Override
    public MovimientoInventario salidaInventario(Producto producto, int cantidad,
                                                String motivo, String observaciones, Usuario usuario) {
        int stockAnterior = producto.getStock();
        
        if (stockAnterior < cantidad) {
            throw new IllegalArgumentException(
                "Stock insuficiente. Stock actual: " + stockAnterior + ", solicitado: " + cantidad
            );
        }
        
        int stockNuevo = stockAnterior - cantidad;
        
        // Actualizar el stock del producto
        producto.setStock(stockNuevo);
        productoRepository.save(producto);
        
        // Crear el movimiento
        MovimientoInventario movimiento = new MovimientoInventario(
            producto, TipoMovimiento.SALIDA, cantidad, stockAnterior, stockNuevo, motivo, usuario
        );
        movimiento.setObservaciones(observaciones);
        
        return registrarMovimiento(movimiento);
    }
    
    @Override
    public MovimientoInventario ajusteInventario(Producto producto, int stockNuevo,
                                                String motivo, String observaciones, Usuario usuario) {
        int stockAnterior = producto.getStock();
        int diferencia = stockNuevo - stockAnterior;
        
        TipoMovimiento tipo;
        if (diferencia > 0) {
            tipo = TipoMovimiento.AJUSTE_POSITIVO;
        } else if (diferencia < 0) {
            tipo = TipoMovimiento.AJUSTE_NEGATIVO;
        } else {
            // No hay cambio, no registrar movimiento
            return null;
        }
        
        // Actualizar el stock del producto
        producto.setStock(stockNuevo);
        productoRepository.save(producto);
        
        // Crear el movimiento
        MovimientoInventario movimiento = new MovimientoInventario(
            producto, tipo, Math.abs(diferencia), stockAnterior, stockNuevo, motivo, usuario
        );
        movimiento.setObservaciones(observaciones);
        
        return registrarMovimiento(movimiento);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerTodosMovimientos() {
        return movimientoRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerMovimientosPorProducto(Long productoId) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
        return movimientoRepository.findByProductoOrderByFechaMovimientoDesc(producto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerMovimientosPorTipo(TipoMovimiento tipo) {
        return movimientoRepository.findByTipoOrderByFechaMovimientoDesc(tipo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerMovimientosPorFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoRepository.findByFechaMovimientoBetweenOrderByFechaMovimientoDesc(fechaInicio, fechaFin);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerMovimientosConFiltros(Long productoId, TipoMovimiento tipo,
                                                                  LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoRepository.findByFiltros(productoId, tipo, fechaInicio, fechaFin);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MovimientoInventario> obtenerUltimosMovimientos() {
        return movimientoRepository.findUltimosMovimientos();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<MovimientoInventario> obtenerMovimientoPorId(Long id) {
        return movimientoRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosStockBajo() {
        return productoRepository.findByStockLessThanEqual(STOCK_BAJO_LIMITE);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosSinStock() {
        return productoRepository.findByStock(0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> obtenerEstadisticasPorTipo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movimientoRepository.getEstadisticasPorTipo(fechaInicio, fechaFin);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validarStockSuficiente(Producto producto, int cantidad) {
        return producto.getStock() >= cantidad;
    }
    
    @Override
    public void eliminarMovimiento(Long id) {
        movimientoRepository.deleteById(id);
    }
}

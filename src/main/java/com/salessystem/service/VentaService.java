package com.salessystem.service;

import com.salessystem.model.Venta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface VentaService extends BaseService<Venta> {
    Page<Venta> findAllPaginated(Pageable pageable);
    Page<Venta> searchPaginated(String query, Pageable pageable);
    List<Venta> findByClienteId(Long clienteId);
    List<Venta> obtenerTodasVentas();
    Venta guardarVenta(Venta venta);
    Optional<Venta> obtenerVentaPorId(Long id);
    
    // Métodos para procesamiento de comportamientos
    void procesarVentaSegunComportamiento(Venta venta, String comportamiento, String accion);
    void procesarComportamientoFactura(Venta venta, String accion);
    void procesarComportamientoDevolucion(Venta venta, String accion);
    void procesarComportamientoPedido(Venta venta, String accion);
    void procesarComportamientoEntrada(Venta venta, String accion);
    void procesarComportamientoSalida(Venta venta, String accion);
    void procesarComportamientoNeutro(Venta venta, String accion);
    
}
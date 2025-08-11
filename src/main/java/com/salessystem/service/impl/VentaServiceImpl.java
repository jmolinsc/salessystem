package com.salessystem.service.impl;

import com.salessystem.model.Venta;
import com.salessystem.model.EstatusVenta;
import com.salessystem.repository.VentaRepository;
import com.salessystem.service.VentaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;

    public VentaServiceImpl(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @Override
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    public Optional<Venta> findById(Long id) {
        return ventaRepository.findById(id);
    }

    @Override
    public Venta save(Venta venta) {
        // Generar movId automáticamente si es una nueva venta
        if (venta.getId() == null && venta.getMovId() == null) {
            Long nextMovId = generateNextMovId();
            venta.setMovId(nextMovId);
        }
        return ventaRepository.save(venta);
    }

    @Override
    public void deleteById(Long id) {
        ventaRepository.deleteById(id);
    }

    @Override
    public List<Venta> search(String query) {
        return ventaRepository.search(query);
    }

    @Override
    public Page<Venta> findAllPaginated(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

    @Override
    public Page<Venta> searchPaginated(String query, Pageable pageable) {
        return ventaRepository.searchPaginated(query, pageable);
    }

    @Override
    public List<Venta> findByClienteId(Long clienteId) {
        return ventaRepository.findByClienteId(clienteId);
    }

    @Override
    public List<Venta> obtenerTodasVentas() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta guardarVenta(Venta venta) {
        // Generar movId automáticamente si es una nueva venta
        if (venta.getId() == null && venta.getMovId() == null) {
            Long nextMovId = generateNextMovId();
            venta.setMovId(nextMovId);
        }
        
        // Asegurar que las relaciones bidireccionales estén establecidas
        if (venta.getDetalles() != null) {
            venta.getDetalles().forEach(detalle -> {
                detalle.setVenta(venta);
            });
        }
        return ventaRepository.save(venta);
    }

    @Override
    public Optional<Venta> obtenerVentaPorId(Long id) {
        // Primero intentar obtener con detalles cargados
        Optional<Venta> ventaConDetalles = ventaRepository.findWithDetailsById(id);
        if (ventaConDetalles.isPresent()) {
            return ventaConDetalles;
        }
        // Si no existe el método, usar el findById estándar
        return ventaRepository.findById(id);
    }
    
    // Método privado para generar el siguiente correlativo
    private Long generateNextMovId() {
        Long maxMovId = ventaRepository.findMaxMovId();
        return maxMovId + 1;
    }
    
    // ============================================
    // MÉTODOS DE PROCESAMIENTO DE COMPORTAMIENTOS
    // ============================================
    
    @Override
    public void procesarVentaSegunComportamiento(Venta venta, String comportamiento, String accion) {
        if (comportamiento == null || comportamiento.trim().isEmpty()) {
            comportamiento = "NEUTRO"; // Valor por defecto
        }
        
        if (accion == null || accion.trim().isEmpty()) {
            accion = "guardar"; // Valor por defecto
        }
        
        System.out.println("=== PROCESANDO COMPORTAMIENTO ===");
        System.out.println("Comportamiento: " + comportamiento);
        System.out.println("Acción: " + accion);
        
        switch (comportamiento.toUpperCase()) {
            case "FACTURA":
                procesarComportamientoFactura(venta, accion);
                break;
            case "DEVOLUCION":
                procesarComportamientoDevolucion(venta, accion);
                break;
            case "PEDIDO":
                procesarComportamientoPedido(venta, accion);
                break;
            case "ENTRADA":
                procesarComportamientoEntrada(venta, accion);
                break;
            case "SALIDA":
                procesarComportamientoSalida(venta, accion);
                break;
            case "NEUTRO":
                procesarComportamientoNeutro(venta, accion);
                break;
            default:
                System.out.println("Comportamiento no reconocido: " + comportamiento + ". Usando comportamiento NEUTRO.");
                procesarComportamientoNeutro(venta, accion);
                break;
        }
    }
    
    @Override
    public void procesarComportamientoFactura(Venta venta, String accion) {
        System.out.println("=== PROCESANDO COMPORTAMIENTO FACTURA ===");
        
        if (accion == null || accion.trim().isEmpty()) {
            accion = "guardar"; // Valor por defecto
        }
        
        switch (accion.toLowerCase()) {
            case "guardar":
                venta.setEstatus(EstatusVenta.SIN_AFECTAR);
                System.out.println("Factura guardada - Estatus: SIN_AFECTAR");
                // Lógica de negocio específica para facturas
                procesarLogicaFacturaGuardar(venta);
                break;
            case "afectar":
                venta.setEstatus(EstatusVenta.CONCLUIDO);
                System.out.println("Factura afectada - Estatus: CONCLUIDO");
                // Lógica de negocio específica para facturas afectadas
                procesarLogicaFacturaAfectar(venta);
                break;
            default:
                // Acción no reconocida, usar guardar por defecto
                venta.setEstatus(EstatusVenta.SIN_AFECTAR);
                System.out.println("Acción no reconocida (" + accion + "), usando guardar por defecto - Estatus: SIN_AFECTAR");
                procesarLogicaFacturaGuardar(venta);
                break;
        }
    }
    
    @Override
    public void procesarComportamientoDevolucion(Venta venta, String accion) {
        System.out.println("=== PROCESANDO COMPORTAMIENTO DEVOLUCION ===");
        switch (accion) {
            case "guardar":
                venta.setEstatus(EstatusVenta.SIN_AFECTAR);
                System.out.println("Devolución guardada - Estatus: SIN_AFECTAR");
                // Lógica de negocio específica para devoluciones
                procesarLogicaDevolucionGuardar(venta);
                break;
            case "afectar":
                venta.setEstatus(EstatusVenta.CONCLUIDO);
                System.out.println("Devolución afectada - Estatus: CONCLUIDO");
                // Lógica de negocio específica para devoluciones afectadas
                procesarLogicaDevolucionAfectar(venta);
                break;
        }
    }
    
    @Override
    public void procesarComportamientoPedido(Venta venta, String accion) {
        System.out.println("=== PROCESANDO COMPORTAMIENTO PEDIDO ===");
        switch (accion) {
            case "guardar":
                venta.setEstatus(EstatusVenta.PENDIENTE);
                System.out.println("Pedido guardado - Estatus: PENDIENTE");
                // Lógica de negocio específica para pedidos
                procesarLogicaPedidoGuardar(venta);
                break;
            case "afectar":
                venta.setEstatus(EstatusVenta.CONCLUIDO);
                System.out.println("Pedido afectado - Estatus: CONCLUIDO");
                // Lógica de negocio específica para pedidos afectados
                procesarLogicaPedidoAfectar(venta);
                break;
        }
    }
    
    @Override
    public void procesarComportamientoEntrada(Venta venta, String accion) {
        System.out.println("=== PROCESANDO COMPORTAMIENTO ENTRADA ===");
        switch (accion) {
            case "guardar":
                venta.setEstatus(EstatusVenta.SIN_AFECTAR);
                System.out.println("Entrada guardada - Estatus: SIN_AFECTAR");
                // Lógica de negocio específica para entradas
                procesarLogicaEntradaGuardar(venta);
                break;
            case "afectar":
                venta.setEstatus(EstatusVenta.CONCLUIDO);
                System.out.println("Entrada afectada - Estatus: CONCLUIDO");
                // Lógica de negocio específica para entradas afectadas
                procesarLogicaEntradaAfectar(venta);
                break;
        }
    }
    
    @Override
    public void procesarComportamientoSalida(Venta venta, String accion) {
        System.out.println("=== PROCESANDO COMPORTAMIENTO SALIDA ===");
        switch (accion) {
            case "guardar":
                venta.setEstatus(EstatusVenta.SIN_AFECTAR);
                System.out.println("Salida guardada - Estatus: SIN_AFECTAR");
                // Lógica de negocio específica para salidas
                procesarLogicaSalidaGuardar(venta);
                break;
            case "afectar":
                venta.setEstatus(EstatusVenta.CONCLUIDO);
                System.out.println("Salida afectada - Estatus: CONCLUIDO");
                // Lógica de negocio específica para salidas afectadas
                procesarLogicaSalidaAfectar(venta);
                break;
        }
    }
    
    @Override
    public void procesarComportamientoNeutro(Venta venta, String accion) {
        System.out.println("=== PROCESANDO COMPORTAMIENTO NEUTRO ===");
        switch (accion) {
            case "guardar":
                venta.setEstatus(EstatusVenta.SIN_AFECTAR);
                System.out.println("Documento neutro guardado - Estatus: SIN_AFECTAR");
                break;
            case "afectar":
                venta.setEstatus(EstatusVenta.CONCLUIDO);
                System.out.println("Documento neutro afectado - Estatus: CONCLUIDO");
                break;
        }
    }
    
    // ============================================
    // MÉTODOS PRIVADOS PARA LÓGICA DE NEGOCIO
    // ============================================
    
    private void procesarLogicaFacturaGuardar(Venta venta) {
        // TODO: Implementar lógica específica para guardar facturas
        // Ejemplo: Reservar productos, validar precios, generar número de serie
        System.out.println("Ejecutando lógica de negocio para guardar factura");
    }
    
    private void procesarLogicaFacturaAfectar(Venta venta) {
        // TODO: Implementar lógica específica para afectar facturas
        // Ejemplo: Decrementar inventario, generar cuentas por cobrar, enviar email
        System.out.println("Ejecutando lógica de negocio para afectar factura - decrementar inventario");
    }
    
    private void procesarLogicaDevolucionGuardar(Venta venta) {
        // TODO: Implementar lógica específica para guardar devoluciones
        // Ejemplo: Verificar factura original, validar productos
        System.out.println("Ejecutando lógica de negocio para guardar devolución");
    }
    
    private void procesarLogicaDevolucionAfectar(Venta venta) {
        // TODO: Implementar lógica específica para afectar devoluciones
        // Ejemplo: Incrementar inventario, generar nota de crédito
        System.out.println("Ejecutando lógica de negocio para afectar devolución - incrementar inventario");
    }
    
    private void procesarLogicaPedidoGuardar(Venta venta) {
        // TODO: Implementar lógica específica para guardar pedidos
        // Ejemplo: Verificar disponibilidad, calcular tiempo de entrega
        System.out.println("Ejecutando lógica de negocio para guardar pedido");
    }
    
    private void procesarLogicaPedidoAfectar(Venta venta) {
        // TODO: Implementar lógica específica para afectar pedidos
        // Ejemplo: Convertir a factura, reservar inventario definitivamente
        System.out.println("Ejecutando lógica de negocio para afectar pedido - convertir a factura");
    }
    
    private void procesarLogicaEntradaGuardar(Venta venta) {
        // TODO: Implementar lógica específica para guardar entradas de inventario
        System.out.println("Ejecutando lógica de negocio para guardar entrada de inventario");
    }
    
    private void procesarLogicaEntradaAfectar(Venta venta) {
        // TODO: Implementar lógica específica para afectar entradas de inventario
        // Ejemplo: Incrementar inventario
        System.out.println("Ejecutando lógica de negocio para afectar entrada - incrementar inventario");
    }
    
    private void procesarLogicaSalidaGuardar(Venta venta) {
        // TODO: Implementar lógica específica para guardar salidas de inventario
        System.out.println("Ejecutando lógica de negocio para guardar salida de inventario");
    }
    
    private void procesarLogicaSalidaAfectar(Venta venta) {
        // TODO: Implementar lógica específica para afectar salidas de inventario
        // Ejemplo: Decrementar inventario
        System.out.println("Ejecutando lógica de negocio para afectar salida - decrementar inventario");
    }
}
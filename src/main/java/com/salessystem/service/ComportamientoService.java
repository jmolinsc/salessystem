package com.salessystem.service;

import com.salessystem.model.Comportamiento;
import com.salessystem.repository.ComportamientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ComportamientoService {

    private final ComportamientoRepository comportamientoRepository;

    public ComportamientoService(ComportamientoRepository comportamientoRepository) {
        this.comportamientoRepository = comportamientoRepository;
    }

    // Operaciones CRUD básicas
    public List<Comportamiento> findAll() {
        return comportamientoRepository.findAll();
    }

    public Optional<Comportamiento> findById(Long id) {
        return comportamientoRepository.findById(id);
    }

    public Optional<Comportamiento> findByCodigo(String codigo) {
        return comportamientoRepository.findByCodigoIgnoreCase(codigo);
    }

    public Comportamiento save(Comportamiento comportamiento) {
        if (comportamiento.getId() != null) {
            comportamiento.setFechaModificacion(LocalDateTime.now());
        }
        return comportamientoRepository.save(comportamiento);
    }

    public void deleteById(Long id) {
        comportamientoRepository.deleteById(id);
    }

    // Validaciones
    public boolean existsByCodigo(String codigo) {
        return comportamientoRepository.existsByCodigoIgnoreCase(codigo);
    }

    public boolean existsByCodigoAndIdNot(String codigo, Long id) {
        return comportamientoRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id);
    }

    // Búsquedas especializadas
    public List<Comportamiento> findActivos() {
        return comportamientoRepository.findByEstatusOrderByNombreAsc("ACTIVO");
    }

    public List<Comportamiento> findByTipo(String tipo) {
        return comportamientoRepository.findByTipoAndEstatusOrderByNombreAsc(tipo, "ACTIVO");
    }

    public List<Comportamiento> findQueAfectanStock() {
        return comportamientoRepository.findByEstatusAndAfectaStockOrderByNombreAsc("ACTIVO", true);
    }

    public List<Comportamiento> findQueNoAfectanStock() {
        return comportamientoRepository.findByEstatusAndAfectaStockOrderByNombreAsc("ACTIVO", false);
    }

    public List<Comportamiento> findBySignoMovimiento(Integer signo) {
        return comportamientoRepository.findBySignoMovimientoAndEstatusOrderByNombreAsc(signo, "ACTIVO");
    }

    public List<Comportamiento> search(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findActivos();
        }
        return comportamientoRepository.findBySearchTerm(searchTerm.trim());
    }

    // Operaciones de estado
    public Comportamiento activar(Long id) {
        Optional<Comportamiento> comportamientoOpt = findById(id);
        if (comportamientoOpt.isPresent()) {
            Comportamiento comportamiento = comportamientoOpt.get();
            comportamiento.setEstatus("ACTIVO");
            comportamiento.setFechaModificacion(LocalDateTime.now());
            return save(comportamiento);
        }
        throw new RuntimeException("Comportamiento no encontrado con ID: " + id);
    }

    public Comportamiento desactivar(Long id) {
        Optional<Comportamiento> comportamientoOpt = findById(id);
        if (comportamientoOpt.isPresent()) {
            Comportamiento comportamiento = comportamientoOpt.get();
            comportamiento.setEstatus("INACTIVO");
            comportamiento.setFechaModificacion(LocalDateTime.now());
            return save(comportamiento);
        }
        throw new RuntimeException("Comportamiento no encontrado con ID: " + id);
    }

    // Estadísticas
    public Long countTiposDocumentoByComportamiento(Long comportamientoId) {
        Optional<Comportamiento> comportamiento = findById(comportamientoId);
        if (comportamiento.isPresent()) {
            return comportamientoRepository.countTiposDocumentoByComportamiento(comportamiento.get().getCodigo());
        }
        return 0L;
    }

    public List<Comportamiento> findAllWithTiposDocumento() {
        return comportamientoRepository.findAllWithTiposDocumento("ACTIVO");
    }

    // Inicializar comportamientos por defecto
    public void initializeDefaultComportamientos() {
        List<Comportamiento> existingComportamientos = findAll();
        if (existingComportamientos.isEmpty()) {
            System.out.println("🔄 Inicializando comportamientos por defecto...");
            
            // Comportamientos de Inventario
            createComportamientoIfNotExists("ENTRADA", "Entrada de Inventario", 
                "Movimiento que incrementa el stock de productos", "INVENTARIO", true, 1);
            
            createComportamientoIfNotExists("SALIDA", "Salida de Inventario", 
                "Movimiento que reduce el stock de productos", "INVENTARIO", true, -1);
            
            createComportamientoIfNotExists("NEUTRO", "Movimiento Neutro", 
                "Movimiento que no afecta el inventario", "GENERAL", false, 0);
            
            // Comportamientos de Ventas
            createComportamientoIfNotExists("FACTURA", "Facturación", 
                "Documento de venta que reduce inventario", "VENTAS", true, -1);
            
            createComportamientoIfNotExists("DEVOLUCION", "Devolución", 
                "Documento de devolución que incrementa inventario", "VENTAS", true, 1);
            
            createComportamientoIfNotExists("PEDIDO", "Pedido", 
                "Reserva de mercancía sin afectar stock físico", "VENTAS", false, 0);
            
            // Comportamientos de Ajuste
            createComportamientoIfNotExists("AJUSTE_POSITIVO", "Ajuste Positivo", 
                "Ajuste que incrementa el inventario", "INVENTARIO", true, 1);
            
            createComportamientoIfNotExists("AJUSTE_NEGATIVO", "Ajuste Negativo", 
                "Ajuste que reduce el inventario", "INVENTARIO", true, -1);
            
            // Comportamientos de Transferencia
            createComportamientoIfNotExists("TRANSFERENCIA", "Transferencia", 
                "Movimiento entre ubicaciones sin afectar stock total", "INVENTARIO", false, 0);
            
            System.out.println("✅ Comportamientos por defecto creados exitosamente");
        }
    }

    private void createComportamientoIfNotExists(String codigo, String nombre, String descripcion, 
                                               String tipo, Boolean afectaStock, Integer signoMovimiento) {
        Optional<Comportamiento> existing = findByCodigo(codigo);
        if (existing.isEmpty()) {
            Comportamiento comportamiento = new Comportamiento(codigo, nombre, descripcion, tipo, afectaStock, signoMovimiento);
            save(comportamiento);
            System.out.println("📝 Comportamiento creado: " + nombre);
        }
    }
}

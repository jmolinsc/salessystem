package com.salessystem.service;

import com.salessystem.model.Comportamiento;
import com.salessystem.model.TipoDocumento;
import com.salessystem.repository.TipoDocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TipoDocumentoService {

    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;

    @Autowired
    private ComportamientoService comportamientoService;

    // Obtener todos los tipos de documento
    @Transactional(readOnly = true)
    public List<TipoDocumento> findAll() {
        return tipoDocumentoRepository.findAll();
    }

    // Obtener tipos de documento activos
    @Transactional(readOnly = true)
    public List<TipoDocumento> findAllActivos() {
        return tipoDocumentoRepository.findByEstatusOrderByModuloAscDescripcionAsc("ACTIVO");
    }

    // Obtener tipos de documento por módulo (solo activos)
    @Transactional(readOnly = true)
    public List<TipoDocumento> findByModulo(String modulo) {
        return tipoDocumentoRepository.findByModuloAndEstatusOrderByDescripcionAsc(modulo, "ACTIVO");
    }

    // Obtener tipos de documento por módulo (todos los estatus)
    @Transactional(readOnly = true)
    public List<TipoDocumento> findAllByModulo(String modulo) {
        return tipoDocumentoRepository.findByModuloOrderByDescripcionAsc(modulo);
    }

    // Obtener tipo de documento por ID
    @Transactional(readOnly = true)
    public Optional<TipoDocumento> findById(Long id) {
        return tipoDocumentoRepository.findById(id);
    }

    // Obtener tipo de documento por mov
    @Transactional(readOnly = true)
    public Optional<TipoDocumento> findByMov(String mov) {
        return tipoDocumentoRepository.findByMovIgnoreCase(mov);
    }

    // Buscar tipos de documento por término de búsqueda
    @Transactional(readOnly = true)
    public List<TipoDocumento> search(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        return tipoDocumentoRepository.findBySearchTerm(searchTerm.trim());
    }

    // Guardar tipo de documento
    public TipoDocumento save(TipoDocumento tipoDocumento) {
        if (tipoDocumento.getId() == null) {
            // Nuevo tipo documento
            tipoDocumento.setFechaAlta(LocalDateTime.now());
            if (tipoDocumento.getEstatus() == null || tipoDocumento.getEstatus().isEmpty()) {
                tipoDocumento.setEstatus("ACTIVO");
            }
        }
        return tipoDocumentoRepository.save(tipoDocumento);
    }

    // Eliminar tipo de documento
    public void deleteById(Long id) {
        tipoDocumentoRepository.deleteById(id);
    }

    // Verificar si existe un mov
    @Transactional(readOnly = true)
    public boolean existsByMov(String mov) {
        return tipoDocumentoRepository.existsByMovIgnoreCase(mov);
    }

    // Verificar si existe un mov, excluyendo un ID específico
    @Transactional(readOnly = true)
    public boolean existsByMovAndIdNot(String mov, Long id) {
        return tipoDocumentoRepository.existsByMovIgnoreCaseAndIdNot(mov, id);
    }

    // Cambiar estatus del tipo de documento
    public TipoDocumento cambiarEstatus(Long id, String nuevoEstatus) {
        Optional<TipoDocumento> tipoDocumentoOpt = findById(id);
        if (tipoDocumentoOpt.isPresent()) {
            TipoDocumento tipoDocumento = tipoDocumentoOpt.get();
            tipoDocumento.setEstatus(nuevoEstatus);
            return save(tipoDocumento);
        }
        throw new RuntimeException("Tipo de documento no encontrado con ID: " + id);
    }

    // Inicializar tipos de documento por defecto
    public void initializeDefaultTiposDocumento() {
        // Verificar si ya existen tipos de documento
        List<TipoDocumento> existingTipos = findAll();
        if (existingTipos.isEmpty()) {
            // Crear tipos de documento para VTA (Ventas) con comportamientos específicos
            createTipoDocumentoIfNotExists("FACTURA", "Factura", "VTA", "FACTURA");
            createTipoDocumentoIfNotExists("CREDITO_FISCAL", "Crédito Fiscal", "VTA", "FACTURA");
            createTipoDocumentoIfNotExists("FACTURA_EXPORTACION", "Factura Exportación", "VTA", "FACTURA");
            createTipoDocumentoIfNotExists("NOTA_CREDITO", "Nota Crédito", "VTA", "DEVOLUCION");
            createTipoDocumentoIfNotExists("PEDIDO", "Pedido", "VTA", "PEDIDO");
            createTipoDocumentoIfNotExists("COTIZACION", "Cotización", "VTA", "COTIZACION");
            
            // Crear tipos de documento para INV (Inventario)
            createTipoDocumentoIfNotExists("ENTRADA_INV", "Entrada de Inventario", "INV", "ENTRADA");
            createTipoDocumentoIfNotExists("SALIDA_INV", "Salida de Inventario", "INV", "SALIDA");
            createTipoDocumentoIfNotExists("AJUSTE_POS", "Ajuste Positivo", "INV", "AJUSTE_POSITIVO");
            createTipoDocumentoIfNotExists("AJUSTE_NEG", "Ajuste Negativo", "INV", "AJUSTE_NEGATIVO");
            createTipoDocumentoIfNotExists("TRANSFERENCIA", "Transferencia", "INV", "TRANSFERENCIA");
            
            System.out.println("✅ Tipos de documento de ejemplo creados exitosamente");
        }
    }

    private void createTipoDocumentoIfNotExists(String mov, String descripcion, String modulo, Comportamiento comportamiento) {
        Optional<TipoDocumento> existing = findByMov(mov);
        if (existing.isEmpty()) {
            TipoDocumento tipoDocumento = new TipoDocumento(mov, descripcion, modulo, comportamiento);
            save(tipoDocumento);
        }
    }

    private void createTipoDocumentoIfNotExists(String mov, String descripcion, String modulo, String codigoComportamiento) {
        Optional<Comportamiento> comportamiento = comportamientoService.findByCodigo(codigoComportamiento);
        if (comportamiento.isPresent()) {
            createTipoDocumentoIfNotExists(mov, descripcion, modulo, comportamiento.get());
        } else {
            // Si no encuentra el comportamiento, crear sin comportamiento
            createTipoDocumentoIfNotExists(mov, descripcion, modulo, (Comportamiento) null);
            System.out.println("⚠️ Advertencia: Comportamiento '" + codigoComportamiento + "' no encontrado para tipo documento '" + mov + "'");
        }
    }
}

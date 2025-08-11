package com.salessystem.repository;

import com.salessystem.model.Comportamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComportamientoRepository extends JpaRepository<Comportamiento, Long> {
    
    // Buscar por código (único)
    Optional<Comportamiento> findByCodigoIgnoreCase(String codigo);
    
    // Verificar si existe un código
    boolean existsByCodigoIgnoreCase(String codigo);
    
    // Verificar si existe un código excluyendo un ID específico
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
    
    // Buscar por estatus
    List<Comportamiento> findByEstatusOrderByNombreAsc(String estatus);
    
    // Buscar activos
    List<Comportamiento> findByEstatusAndAfectaStockOrderByNombreAsc(String estatus, Boolean afectaStock);
    
    // Buscar por tipo
    List<Comportamiento> findByTipoAndEstatusOrderByNombreAsc(String tipo, String estatus);
    
    // Buscar por tipo y que afecte stock
    List<Comportamiento> findByTipoAndAfectaStockAndEstatusOrderByNombreAsc(String tipo, Boolean afectaStock, String estatus);
    
    // Buscar por signo de movimiento
    List<Comportamiento> findBySignoMovimientoAndEstatusOrderByNombreAsc(Integer signoMovimiento, String estatus);
    
    // Búsqueda personalizada
    @Query("SELECT c FROM Comportamiento c WHERE " +
           "LOWER(c.codigo) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.tipo) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY c.nombre ASC")
    List<Comportamiento> findBySearchTerm(@Param("search") String search);
    
    // Contar tipos de documento por comportamiento (usando el campo String comportamiento)
    @Query("SELECT COUNT(td) FROM TipoDocumento td WHERE UPPER(td.comportamiento) = UPPER(:comportamientoCodigo)")
    Long countTiposDocumentoByComportamiento(@Param("comportamientoCodigo") String comportamientoCodigo);
    
    // Buscar comportamientos activos (sin relación con tipos de documento)
    @Query("SELECT c FROM Comportamiento c " +
           "WHERE c.estatus = :estatus " +
           "ORDER BY c.nombre ASC")
    List<Comportamiento> findAllWithTiposDocumento(@Param("estatus") String estatus);
}

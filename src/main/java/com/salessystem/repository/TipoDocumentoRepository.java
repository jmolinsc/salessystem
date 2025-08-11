package com.salessystem.repository;

import com.salessystem.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, Long> {
    
    // Buscar por módulo y estatus activo
    List<TipoDocumento> findByModuloAndEstatusOrderByDescripcionAsc(String modulo, String estatus);
    
    // Buscar por módulo (todos los estatus)
    List<TipoDocumento> findByModuloOrderByDescripcionAsc(String modulo);
    
    // Buscar por mov (nombre único)
    Optional<TipoDocumento> findByMovIgnoreCase(String mov);
    
    // Verificar si existe un mov (excluyendo un ID específico)
    boolean existsByMovIgnoreCaseAndIdNot(String mov, Long id);
    
    // Verificar si existe un mov
    boolean existsByMovIgnoreCase(String mov);
    
    // Buscar activos
    List<TipoDocumento> findByEstatusOrderByModuloAscDescripcionAsc(String estatus);
    
    // Búsqueda personalizada
    @Query("SELECT td FROM TipoDocumento td WHERE " +
           "LOWER(td.mov) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(td.descripcion) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(td.modulo) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY td.modulo ASC, td.descripcion ASC")
    List<TipoDocumento> findBySearchTerm(@Param("search") String search);
}

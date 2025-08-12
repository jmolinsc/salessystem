package com.salessystem.repository;

import com.salessystem.model.ProdFamilia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdFamiliaRepository extends JpaRepository<ProdFamilia, Long> {
    
    List<ProdFamilia> findByEstatusOrderByNombreAsc(String estatus);
    
    Optional<ProdFamilia> findByCodigo(String codigo);
    
    boolean existsByCodigo(String codigo);
    
    boolean existsByCodigoAndIdNot(String codigo, Long id);
    
    @Query("SELECT p FROM ProdFamilia p WHERE p.estatus = 'ACTIVO' ORDER BY p.nombre ASC")
    List<ProdFamilia> findAllActivas();
    
    @Query("SELECT COUNT(p) FROM ProdFamilia p WHERE p.estatus = 'ACTIVO'")
    long countActivas();

    List<ProdFamilia> findByNombre(String nombre);
}

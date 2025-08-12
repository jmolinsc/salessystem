package com.salessystem.service;

import com.salessystem.model.ProdFamilia;
import com.salessystem.repository.ProdFamiliaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdFamiliaService {
    
    private final ProdFamiliaRepository prodFamiliaRepository;
    
    public ProdFamiliaService(ProdFamiliaRepository prodFamiliaRepository) {
        this.prodFamiliaRepository = prodFamiliaRepository;
    }
    
    public List<ProdFamilia> findAll() {
        return prodFamiliaRepository.findAll();
    }
    
    public List<ProdFamilia> findAllActivas() {
        return prodFamiliaRepository.findAllActivas();
    }
    
    public Optional<ProdFamilia> findById(Long id) {
        return prodFamiliaRepository.findById(id);
    }
    
    public Optional<ProdFamilia> findByCodigo(String codigo) {
        return prodFamiliaRepository.findByCodigo(codigo);
    }
    
    public ProdFamilia save(ProdFamilia prodFamilia) {
        return prodFamiliaRepository.save(prodFamilia);
    }
    
    public void deleteById(Long id) {
        prodFamiliaRepository.deleteById(id);
    }
    
    public boolean existsByCodigo(String codigo) {
        return prodFamiliaRepository.existsByCodigo(codigo);
    }
    
    public boolean existsByCodigoAndIdNot(String codigo, Long id) {
        return prodFamiliaRepository.existsByCodigoAndIdNot(codigo, id);
    }
    
    public long countActivas() {
        return prodFamiliaRepository.countActivas();
    }
    
    public ProdFamilia desactivar(Long id) {
        Optional<ProdFamilia> optionalProdFamilia = findById(id);
        if (optionalProdFamilia.isPresent()) {
            ProdFamilia prodFamilia = optionalProdFamilia.get();
            prodFamilia.setEstatus("INACTIVO");
            return save(prodFamilia);
        }
        throw new IllegalArgumentException("Familia de producto no encontrada: " + id);
    }
    
    public ProdFamilia activar(Long id) {
        Optional<ProdFamilia> optionalProdFamilia = findById(id);
        if (optionalProdFamilia.isPresent()) {
            ProdFamilia prodFamilia = optionalProdFamilia.get();
            prodFamilia.setEstatus("ACTIVO");
            return save(prodFamilia);
        }
        throw new IllegalArgumentException("Familia de producto no encontrada: " + id);
    }

    public List<ProdFamilia> findByNombre(String nombre) {
        return prodFamiliaRepository.findByNombre(nombre);
    }
}

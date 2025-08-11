package com.salessystem.service;

import com.salessystem.model.Fabricante;
import com.salessystem.repository.FabricanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FabricanteService {

    @Autowired
    private FabricanteRepository fabricanteRepository;

    // Obtener todos los fabricantes
    @Transactional(readOnly = true)
    public List<Fabricante> findAll() {
        return fabricanteRepository.findAll();
    }

    // Obtener fabricantes activos
    @Transactional(readOnly = true)
    public List<Fabricante> findAllActivos() {
        return fabricanteRepository.findByEstatusOrderByNombreAsc("ACTIVO");
    }

    // Obtener fabricante por ID
    @Transactional(readOnly = true)
    public Optional<Fabricante> findById(Long id) {
        return fabricanteRepository.findById(id);
    }

    // Obtener fabricante por nombre
    @Transactional(readOnly = true)
    public Optional<Fabricante> findByNombre(String nombre) {
        return fabricanteRepository.findByNombreIgnoreCase(nombre);
    }

    // Buscar fabricantes por término de búsqueda
    @Transactional(readOnly = true)
    public List<Fabricante> search(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll();
        }
        return fabricanteRepository.findBySearchTerm(searchTerm.trim());
    }

    // Guardar fabricante
    public Fabricante save(Fabricante fabricante) {
        if (fabricante.getId() == null) {
            // Nuevo fabricante
            fabricante.setFechaAlta(LocalDateTime.now());
            if (fabricante.getEstatus() == null || fabricante.getEstatus().isEmpty()) {
                fabricante.setEstatus("ACTIVO");
            }
        }
        return fabricanteRepository.save(fabricante);
    }

    // Eliminar fabricante
    public void deleteById(Long id) {
        fabricanteRepository.deleteById(id);
    }

    // Verificar si existe un fabricante con el nombre dado
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return fabricanteRepository.existsByNombreIgnoreCase(nombre);
    }

    // Verificar si existe un fabricante con el nombre dado, excluyendo un ID específico
    @Transactional(readOnly = true)
    public boolean existsByNombreAndIdNot(String nombre, Long id) {
        return fabricanteRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id);
    }

    // Contar productos por fabricante
    @Transactional(readOnly = true)
    public long countProductosByFabricante(Long fabricanteId) {
        return fabricanteRepository.countProductosByFabricanteId(fabricanteId);
    }

    // Verificar si un fabricante puede ser eliminado
    @Transactional(readOnly = true)
    public boolean canDelete(Long fabricanteId) {
        return countProductosByFabricante(fabricanteId) == 0;
    }

    // Cambiar estatus del fabricante
    public Fabricante cambiarEstatus(Long id, String nuevoEstatus) {
        Optional<Fabricante> fabricanteOpt = findById(id);
        if (fabricanteOpt.isPresent()) {
            Fabricante fabricante = fabricanteOpt.get();
            fabricante.setEstatus(nuevoEstatus);
            return save(fabricante);
        }
        throw new RuntimeException("Fabricante no encontrado con ID: " + id);
    }
}

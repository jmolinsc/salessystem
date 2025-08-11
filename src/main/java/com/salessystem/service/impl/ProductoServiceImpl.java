package com.salessystem.service.impl;

import com.salessystem.model.Producto;
import com.salessystem.repository.ProductoRepository;
import com.salessystem.service.ProductoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> search(String query) {
        return productoRepository.search(query);
    }

    @Override
    public List<Producto> findByCategoria(String categoria) {
        return productoRepository.findByCategoriaNombreContainingIgnoreCase(categoria);
    }

    @Override
    public Page<Producto> findAllPaginated(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    @Override
    public Page<Producto> searchPaginated(String query, Pageable pageable) {
        return productoRepository.searchPaginated(query, pageable);
    }

    @Override
    public Optional<Producto> findByCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return productoRepository.existsByCodigo(codigo);
    }

    @Override
    public boolean existsByCodigoAndIdNot(String codigo, Long id) {
        return productoRepository.existsByCodigoAndIdNot(codigo, id);
    }
}
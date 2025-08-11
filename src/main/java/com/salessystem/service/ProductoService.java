package com.salessystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.salessystem.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService extends BaseService<Producto> {
    List<Producto> findByCategoria(String categoria);
    Page<Producto> findAllPaginated(Pageable pageable);
    Page<Producto> searchPaginated(String query, Pageable pageable);
    Optional<Producto> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Long id);
}
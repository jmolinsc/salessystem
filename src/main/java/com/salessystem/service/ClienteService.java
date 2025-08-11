package com.salessystem.service;

import com.salessystem.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> findAll();
    Optional<Cliente> findById(Long id);
    Cliente save(Cliente cliente);
    void deleteById(Long id);
    List<Cliente> search(String query);
    Page<Cliente> findAllPaginated(Pageable pageable);
    Page<Cliente> searchPaginated(String query, Pageable pageable);
    boolean existsByEmail(String email);
    List<Cliente> findByFechaRegistroBetween(LocalDateTime startDate, LocalDateTime endDate);
}
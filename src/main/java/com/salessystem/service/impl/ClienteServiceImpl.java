package com.salessystem.service.impl;

import com.salessystem.model.Cliente;
import com.salessystem.repository.ClienteRepository;
import com.salessystem.service.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    @Override
    public void deleteById(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public List<Cliente> search(String query) {
        return clienteRepository.search(query);
    }

    @Override
    public Page<Cliente> findAllPaginated(Pageable pageable) {
        return clienteRepository.findAll(pageable);
    }

    @Override
    public Page<Cliente> searchPaginated(String query, Pageable pageable) {
        return clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
                query, query, pageable);
    }

    @Override
    public boolean existsByEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    @Override
    public List<Cliente> findByFechaRegistroBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return clienteRepository.findByFechaRegistroBetween(startDate, endDate);
    }
}

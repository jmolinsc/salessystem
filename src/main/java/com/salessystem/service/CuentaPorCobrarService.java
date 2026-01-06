package com.salessystem.service;

import com.salessystem.model.CuentaPorCobrar;

import java.util.List;
import java.util.Optional;

public interface CuentaPorCobrarService {
    CuentaPorCobrar crearCuentaPorCobrar(CuentaPorCobrar cp);
    Optional<CuentaPorCobrar> findByVentaId(Long ventaId);
    List<CuentaPorCobrar> findByClienteId(Long clienteId);
    CuentaPorCobrar guardar(CuentaPorCobrar cp);
    List<CuentaPorCobrar> findAll();
    Optional<CuentaPorCobrar> findById(Long id);
    void deleteById(Long id);
}

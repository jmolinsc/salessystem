package com.salessystem.service.impl;

import com.salessystem.model.CuentaPorCobrar;
import com.salessystem.repository.CuentaPorCobrarRepository;
import com.salessystem.service.CuentaPorCobrarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Date;

@Service
public class CuentaPorCobrarServiceImpl implements CuentaPorCobrarService {

    private final CuentaPorCobrarRepository cuentaRepo;

    public CuentaPorCobrarServiceImpl(CuentaPorCobrarRepository cuentaRepo) {
        this.cuentaRepo = cuentaRepo;
    }

    @Override
    @Transactional
    public CuentaPorCobrar crearCuentaPorCobrar(CuentaPorCobrar cp) {
        if (cp == null) throw new IllegalArgumentException("CuentaPorCobrar es null");
        if (cp.getFechaEmision() == null) cp.setFechaEmision(new Date());
        if (cp.getEstado() == null) cp.setEstado("PENDIENTE");
        return cuentaRepo.save(cp);
    }

    @Override
    public Optional<CuentaPorCobrar> findByVentaId(Long ventaId) {
        return cuentaRepo.findByVentaId(ventaId);
    }

    @Override
    public List<CuentaPorCobrar> findByClienteId(Long clienteId) {
        return cuentaRepo.findByClienteId(clienteId);
    }

    @Override
    @Transactional
    public CuentaPorCobrar guardar(CuentaPorCobrar cp) {
        if (cp == null) throw new IllegalArgumentException("CuentaPorCobrar es null");
        return cuentaRepo.save(cp);
    }

    @Override
    public List<CuentaPorCobrar> findAll() {
        return cuentaRepo.findAll();
    }

    @Override
    public Optional<CuentaPorCobrar> findById(Long id) {
        return cuentaRepo.findById(id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        cuentaRepo.deleteById(id);
    }
}

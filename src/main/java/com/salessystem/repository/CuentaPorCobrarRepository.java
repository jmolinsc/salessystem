package com.salessystem.repository;

import com.salessystem.model.CuentaPorCobrar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaPorCobrarRepository extends JpaRepository<CuentaPorCobrar, Long> {
    List<CuentaPorCobrar> findByClienteId(Long clienteId);
    Optional<CuentaPorCobrar> findByVentaId(Long ventaId);
    List<CuentaPorCobrar> findByEstado(String estado);
}

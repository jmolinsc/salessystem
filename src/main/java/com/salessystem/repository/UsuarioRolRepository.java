package com.salessystem.repository;

import com.salessystem.model.UsuarioRol;
import com.salessystem.model.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {
    void deleteByUsuarioIdAndRolId(Long usuarioId, Long rolId);
    boolean existsByUsuarioIdAndRolId(Long usuarioId, Long rolId);
}

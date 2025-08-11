package com.salessystem.service;

import com.salessystem.model.Rol;
import com.salessystem.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface UsuarioService extends BaseService<Usuario> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<Usuario> findAllPaginated(Pageable pageable);
    Page<Usuario> searchPaginated(String query, Pageable pageable);
    void addRolToUsuario(Long usuarioId, Long rolId);
    void removeRolFromUsuario(Long usuarioId, Long rolId);
    Set<Rol> getRolesByUsuarioId(Long usuarioId);
}
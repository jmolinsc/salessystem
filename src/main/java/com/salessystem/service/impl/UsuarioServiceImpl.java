package com.salessystem.service.impl;

import com.salessystem.model.Rol;
import com.salessystem.model.Usuario;
import com.salessystem.model.UsuarioRol;
import com.salessystem.model.UsuarioRolId;
import com.salessystem.repository.RolRepository;
import com.salessystem.repository.UsuarioRepository;
import com.salessystem.repository.UsuarioRolRepository;
import com.salessystem.service.UsuarioService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    // TODO: Implementar cuando se tengan las entidades Rol y UsuarioRol completas
    @SuppressWarnings("unused")
    private final RolRepository rolRepository;
    @SuppressWarnings("unused")
    private final UsuarioRolRepository usuarioRolRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, 
                             RolRepository rolRepository,
                             UsuarioRolRepository usuarioRolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public List<Usuario> search(String query) {
        return usuarioRepository.search(query);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    public Page<Usuario> findAllPaginated(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Override
    public Page<Usuario> searchPaginated(String query, Pageable pageable) {
        return usuarioRepository.searchPaginated(query, pageable);
    }

    @Override
    public void addRolToUsuario(Long usuarioId, Long rolId) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        Optional<Rol> rol = rolRepository.findById(rolId);
        
        if (usuario.isPresent() && rol.isPresent()) {
            if (!usuarioRolRepository.existsByUsuarioIdAndRolId(usuarioId, rolId)) {
                UsuarioRol usuarioRol = new UsuarioRol();
                usuarioRol.setId(new UsuarioRolId(usuarioId, rolId));
                usuarioRol.setUsuario(usuario.get());
                usuarioRol.setRol(rol.get());
                usuarioRolRepository.save(usuarioRol);
            }
        }
    }

    @Override
    public void removeRolFromUsuario(Long usuarioId, Long rolId) {
        usuarioRolRepository.deleteByUsuarioIdAndRolId(usuarioId, rolId);
    }

    @Override
    public Set<Rol> getRolesByUsuarioId(Long usuarioId) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isPresent()) {
            return usuario.get().getRoles();
        }
        return Set.of();
    }
}
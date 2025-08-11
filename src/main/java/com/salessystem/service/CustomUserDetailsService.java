package com.salessystem.service;

import com.salessystem.model.Permiso;
import com.salessystem.model.Rol;
import com.salessystem.model.Usuario;
import com.salessystem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return new org.springframework.security.core.userdetails.User(
                usuario.getUsername(),
                usuario.getPassword(),
                true,
                true,
                true,
                true,
                getAuthorities(usuario)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        Set<Rol> roles = usuario.getRoles();
        
        for (Rol rol : roles) {
            // Agregar el rol como autoridad (prefijo ROLE_ es automático en Spring Security)
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
            
            // Agregar cada permiso como autoridad
            Set<Permiso> permisos = rol.getPermisos();
            for (Permiso permiso : permisos) {
                authorities.add(new SimpleGrantedAuthority(permiso.getNombre()));
            }
        }
        
        return authorities;
    }
    
    /**
     * Método auxiliar para obtener los permisos de un usuario
     */
    public List<String> getUserPermissions(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        List<String> permissions = new ArrayList<>();
        
        for (Rol rol : usuario.getRoles()) {
            for (Permiso permiso : rol.getPermisos()) {
                if (!permissions.contains(permiso.getNombre())) {
                    permissions.add(permiso.getNombre());
                }
            }
        }
        
        return permissions;
    }
}

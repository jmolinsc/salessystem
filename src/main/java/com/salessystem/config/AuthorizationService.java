package com.salessystem.config;

import com.salessystem.model.Permiso;
import com.salessystem.model.Rol;
import com.salessystem.model.Usuario;
import com.salessystem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component("authService")
public class AuthorizationService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Verifica si el usuario actual tiene acceso a un recurso específico
     */
    public boolean hasAccessToResource(String resource) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        Set<Rol> roles = usuario.getRoles();

        for (Rol rol : roles) {
            Set<Permiso> permisos = rol.getPermisos();
            for (Permiso permiso : permisos) {
                if (resourceMatches(permiso.getRecurso(), resource)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Verifica si el usuario tiene un permiso específico
     */
    public boolean hasPermission(String permissionName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        Set<Rol> roles = usuario.getRoles();

        for (Rol rol : roles) {
            Set<Permiso> permisos = rol.getPermisos();
            for (Permiso permiso : permisos) {
                if (permiso.getNombre().equals(permissionName)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Verifica si el usuario tiene rol de ADMIN
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        Set<Rol> roles = usuario.getRoles();

        return roles.stream().anyMatch(rol -> "ADMIN".equals(rol.getNombre()));
    }

    /**
     * Compara si un patrón de recurso coincide con el recurso solicitado
     */
    private boolean resourceMatches(String pattern, String resource) {
        if (pattern.equals(resource)) {
            return true;
        }

        // Soporte para wildcards básicos
        if (pattern.endsWith("/**")) {
            String basePath = pattern.substring(0, pattern.length() - 3);
            return resource.startsWith(basePath);
        }

        if (pattern.endsWith("/*")) {
            String basePath = pattern.substring(0, pattern.length() - 2);
            return resource.startsWith(basePath) && !resource.substring(basePath.length()).contains("/");
        }

        return false;
    }
}

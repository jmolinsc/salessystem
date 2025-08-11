package com.salessystem.config;

import com.salessystem.model.Usuario;
import com.salessystem.service.MenuService;
import com.salessystem.service.PermisoService;
import com.salessystem.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class DynamicSecurityConfigurationManager implements AuthorizationManager<RequestAuthorizationContext> {
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private PermisoService permisoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        Authentication auth = authentication.get();
        
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        
        String requestUri = context.getRequest().getRequestURI();
        String username = auth.getName();
        
        System.out.println("🔍 SECURITY CHECK: Usuario '" + username + "' intentando acceder a: " + requestUri);
        
        try {
            // Verificar si el usuario tiene acceso basado en menús
            boolean tieneAccesoMenu = verificarAccesoMenu(username, requestUri);
            
            // Verificar si el usuario tiene permisos específicos
            boolean tienePermiso = verificarPermiso(username, requestUri);
            
            // El usuario tiene acceso si cumple alguna de las dos condiciones
            boolean acceso = tieneAccesoMenu || tienePermiso;
            
            System.out.println("🔍 RESULTADO: AccesoMenu=" + tieneAccesoMenu + ", Permiso=" + tienePermiso + ", ACCESO FINAL=" + acceso);
            
            return new AuthorizationDecision(acceso);
            
        } catch (Exception e) {
            // En caso de error, denegar acceso
            System.err.println("Error verificando autorización para " + username + " en " + requestUri + ": " + e.getMessage());
            return new AuthorizationDecision(false);
        }
    }
    
    private boolean verificarAccesoMenu(String username, String requestUri) {
        try {
            Long usuarioId = getUserIdByUsername(username);
            System.out.println("🔍 MENU CHECK: Usuario ID=" + usuarioId + " para ruta: " + requestUri);
            
            // Obtener las rutas permitidas para el usuario basadas en sus menús
            List<String> rutasPermitidas = menuService.getRutasPermitidas(usuarioId);
            System.out.println("🔍 RUTAS PERMITIDAS: " + rutasPermitidas);
            
            // Verificar acceso directo
            if (rutasPermitidas.contains(requestUri)) {
                System.out.println("✅ ACCESO DIRECTO CONCEDIDO para: " + requestUri);
                return true;
            }
            
            // Verificar patrones de rutas
            for (String rutaPermitida : rutasPermitidas) {
                if (rutaPermitida.endsWith("/**")) {
                    String patron = rutaPermitida.replace("/**", "");
                    if (requestUri.startsWith(patron)) {
                        System.out.println("✅ ACCESO POR PATRÓN CONCEDIDO: " + rutaPermitida + " para: " + requestUri);
                        return true;
                    }
                }
            }
            
            System.out.println("❌ ACCESO MENU DENEGADO para: " + requestUri);
            return false;
        } catch (Exception e) {
            System.out.println("❌ ERROR en verificarAccesoMenu: " + e.getMessage());
            return false;
        }
    }
    
    private boolean verificarPermiso(String username, String requestUri) {
        try {
            Long usuarioId = getUserIdByUsername(username);
            
            // Verificar si tiene permisos específicos para la ruta
            return permisoService.tienePermiso(usuarioId, requestUri, "READ") ||
                   permisoService.tienePermiso(usuarioId, requestUri, "ALL");
        } catch (Exception e) {
            return false;
        }
    }
    
    private Long getUserIdByUsername(String username) {
        try {
            Optional<Usuario> usuario = usuarioService.findByUsername(username);
            return usuario.map(Usuario::getId).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}

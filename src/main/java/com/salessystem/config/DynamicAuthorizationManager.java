package com.salessystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class DynamicAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Autowired
    private AuthorizationService authorizationService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext context) {
        String requestUri = context.getRequest().getRequestURI();
        
        // Verificar si el usuario tiene acceso al recurso
        boolean hasAccess = authorizationService.hasAccessToResource(requestUri);
        
        return new AuthorizationDecision(hasAccess);
    }
}

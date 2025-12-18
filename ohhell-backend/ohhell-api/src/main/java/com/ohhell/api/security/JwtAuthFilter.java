package com.ohhell.api.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext request) throws IOException {

        // Permitir preflight CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return;
        }

        String path = request.getUriInfo().getPath();

        // Rutas públicas
        if (path.startsWith("auth") || path.startsWith("health")) {
            return;
        }

        String authHeader = request.getHeaderString("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abort(request);
            return;
        }

        try {
            String token = authHeader.substring("Bearer ".length());
            UUID userId = JwtUtil.getUserIdFromToken(token);

            SecurityContext originalContext = request.getSecurityContext();

            request.setSecurityContext(new SecurityContext() {

                @Override
                public Principal getUserPrincipal() {
                    return new UserPrincipal(userId);
                }

                @Override
                public boolean isUserInRole(String role) {
                    return true;
                }

                @Override
                public boolean isSecure() {
                    return originalContext.isSecure();
                }

                @Override
                public String getAuthenticationScheme() {
                    return "Bearer";
                }
            });

        } catch (Exception e) {
            abort(request);
        }
    }

    private void abort(ContainerRequestContext request) {
        request.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("Token inválido o ausente")
                        .build()
        );
    }
}

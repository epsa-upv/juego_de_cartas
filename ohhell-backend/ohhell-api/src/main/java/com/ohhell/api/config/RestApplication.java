package com.ohhell.api.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationScoped
@ApplicationPath("/api")
public class RestApplication extends Application {
    // NO sobrescribas nada
}

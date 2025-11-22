package com.ohhell.ohhellapi;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("/api/v1")
public class HelloApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
            com.ohhell.ohhellapi.resources.HelloResource.class,
            com.ohhell.ohhellapi.resources.TestDatabaseResource.class,
            com.ohhell.ohhellapi.resources.PlayerResource.class,
            com.ohhell.ohhellapi.resources.GameResource.class,
            com.ohhell.ohhellapi.resources.RoundResource.class,
            com.ohhell.ohhellapi.resources.BidResource.class,
            com.ohhell.ohhellapi.resources.TrickResource.class
        );
    }
}

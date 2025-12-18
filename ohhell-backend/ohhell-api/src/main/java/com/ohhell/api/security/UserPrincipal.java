package com.ohhell.api.security;

import java.security.Principal;
import java.util.UUID;

public class UserPrincipal implements Principal {

    private final UUID userId;

    public UserPrincipal(UUID userId) {
        this.userId = userId;
    }

    @Override
    public String getName() {
        return userId.toString();
    }

    public UUID getUserId() {
        return userId;
    }
}

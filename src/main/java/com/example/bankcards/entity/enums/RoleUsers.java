package com.example.bankcards.entity.enums;

import org.springframework.security.core.GrantedAuthority;

public enum RoleUsers implements GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}

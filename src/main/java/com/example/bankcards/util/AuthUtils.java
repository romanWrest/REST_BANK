package com.example.bankcards.util;

import org.springframework.security.core.Authentication;

public class AuthUtils {
    public static boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

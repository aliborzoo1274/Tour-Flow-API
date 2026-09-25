package com.tour.tour.security;

import com.tour.tour.model.Traveler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static void verifyOwnership(Traveler traveler, String errorMessage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication is required.");
        }

        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !("USER_" + traveler.getNid()).equals(authentication.getName())) {
            throw new AccessDeniedException(errorMessage);
        }
    }
}

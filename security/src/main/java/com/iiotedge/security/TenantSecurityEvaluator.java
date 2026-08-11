package com.iiotedge.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class TenantSecurityEvaluator {

    public boolean isSuperAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().endsWith("SUPER_ADMIN"));
    }

    private String getJwtTenantId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof String) {
            return (String) auth.getDetails();
        }
        return null;
    }

    public boolean isTenantAdmin(String targetTenantId) {
        if (isSuperAdmin()) return true;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().contains("ADMIN"));
        String jwtTenantId = getJwtTenantId();

        return isAdmin && targetTenantId != null && targetTenantId.equals(jwtTenantId);
    }

    public boolean isTenantMember(String targetTenantId) {
        if (isSuperAdmin()) return true;

        String jwtTenantId = getJwtTenantId();
        return targetTenantId != null && targetTenantId.equals(jwtTenantId);
    }
}
package com.iiotedge.security.evaluator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("tenantSecurity")
@Slf4j
public class TenantSecurity {

    private static final String PERM_READ = "PERMISSION_READ";
    private static final String PERM_WRITE = "PERMISSION_WRITE";

    public boolean hasReadAccess(String tenantId) {
        return evaluateAccess(tenantId, PERM_READ);
    }

    public boolean hasWriteAccess(String tenantId) {
        return evaluateAccess(tenantId, PERM_WRITE);
    }

    private boolean evaluateAccess(String targetTenantId, String requiredPermission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            log.warn("Security Check Failed: No authenticated user found.");
            return false;
        }

        // 1. Verify Tenant Isolation
        String userTenantId = extractTenantIdFromAuth(auth);
        if (userTenantId == null || !userTenantId.equals(targetTenantId)) {
            log.warn("Security Check Failed: Tenant mismatch. UserTenant={}, TargetTenant={}", userTenantId, targetTenantId);
            return false;
        }

        // 2. Verify Generic Permission
        boolean hasPermission = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(requiredPermission));

        if (!hasPermission) {
            log.warn("Security Check Failed: User lacks {} permission.", requiredPermission);
        }

        return hasPermission;
    }

    /**
     * Extracts the tenant ID based on typical JJWT manual filter implementations.
     */
    private String extractTenantIdFromAuth(Authentication auth) {
        Object principal = auth.getPrincipal();
        Object details = auth.getDetails();

        // Scenario A: You mapped the tenantId into a custom UserDetails object
        // if (principal instanceof IotUserDetails) {
        //     return ((IotUserDetails) principal).getTenantId();
        // }

        // Scenario B: You stored the JWT claims map in the Authentication "details" object
        if (details instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> claims = (Map<String, Object>) details;
            return (String) claims.get("tenantId");
        }

        log.error("Could not extract tenantId. Principal type: {}", principal.getClass().getName());
        return null;
    }
}
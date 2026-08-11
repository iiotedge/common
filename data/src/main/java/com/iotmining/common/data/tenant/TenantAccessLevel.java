package com.iotmining.common.data.tenant;

public enum TenantAccessLevel {
    /**
     * God Mode.
     * Can manage all tenants, billing, and system configs.
     * Maps to: PLATFORM
     */
    SUPER_ADMIN,

    /**
     * Full Control of one Organization.
     * Can create users, sub-tenants, and manage billing for their org.
     * Maps to: ORGANIZATION
     */
    TENANT_ADMIN,

    /**
     * Operational Control.
     * Can manage devices and dashboards but cannot change billing or delete the org.
     * Maps to: SUB_TENANT
     */
    OPERATIONAL,

    /**
     * View Only.
     * Standard users who can only consume data.
     */
    READ_ONLY
}

package com.iotmining.common.data.tenant;

public enum TenantType {
    /**
     * The SaaS Provider (You).
     * There is usually only ONE Platform tenant (the Super Admin).
     */
    PLATFORM,

    /**
     * A Standard Customer / Company.
     * This is the root level for your clients (e.g., "Acme Corp").
     */
    ORGANIZATION,

    /**
     * A Logical Division.
     * Used for hierarchy: Regions, Factory Sites, or Departments under an Organization.
     * e.g., "Acme Corp -> West Coast Plant".
     */
    SUB_TENANT
}
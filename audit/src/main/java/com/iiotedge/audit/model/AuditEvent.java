package com.iiotedge.audit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {
    private String eventId;       // UUID
    private Instant timestamp;    // ISO-8601
    private String application;   // Service Name (e.g., "iiotedge-dms")
    private String tenantId;      // For Multi-tenancy

    // ACTOR (Who)
    private String actorType;     // USER / SYSTEM / DEVICE
    private String actorId;       // User ID or System ID
    private String ipAddress;     // Source IP

    // ACTION (What)
    private String action;
    private String resourceType;
    private String resourceId;

    // RESULT (Status)
    private String status;        // SUCCESS / FAILURE
    private String failureReason; // Exception message if failed

    private Map<String, Object> metadata; // Extra context
}
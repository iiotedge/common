package com.iiotedge.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method to be audited.
 * Triggers an event to Kafka upon success or failure.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {

    /**
     * The action being performed (e.g., "DEVICE_UPDATE", "USER_LOGIN").
     */
    String action();

    /**
     * The type of resource being modified (e.g., "DEVICE", "VEHICLE", "SIM").
     */
    String resourceType();

    /**
     * SpEL expression to dynamically get the Resource ID from parameters.
     * Example: "#deviceId" or "#vehicle.id"
     */
    String resourceId();

    /**
     * Optional description or metadata keys.
     */
    String description() default "";
}
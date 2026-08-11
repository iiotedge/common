package com.iiotedge.audit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "iiotedge.audit")
public class AuditProperties {
    private boolean enabled = true;
    private String kafkaTopic = "iiotedge.audit.events";
    private String applicationName = "unknown-service";
}
package com.iiotedge.audit.config;

import com.iiotedge.audit.aspect.AuditAspect;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
// 1. Enable AuditProperties AND KafkaProperties so we can inject them below
@EnableConfigurationProperties({AuditProperties.class, KafkaProperties.class})
@ConditionalOnProperty(prefix = "iiotedge.audit", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AuditAutoConfiguration {

    /**
     * 1. Create the ProducerFactory capable of handling Objects (JSON).
     * We manually build the config map using KafkaProperties + our overrides.
     */
    @Bean
    @ConditionalOnMissingBean(name = "auditProducerFactory")
    public ProducerFactory<String, Object> auditProducerFactory(KafkaProperties kafkaProperties) {
        // Get standard properties from application.yml (bootstrap-servers, etc.)
        Map<String, Object> configProps = new HashMap<>(kafkaProperties.buildProducerProperties(null));

        // Force Key = String, Value = JSON (Critical for Object logging)
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * 2. Create the KafkaTemplate<String, Object>.
     * This resolves the "No beans of type..." error for the Aspect.
     */
    @Bean
    @ConditionalOnMissingBean(name = "auditKafkaTemplate")
    public KafkaTemplate<String, Object> auditKafkaTemplate(ProducerFactory<String, Object> auditProducerFactory) {
        return new KafkaTemplate<>(auditProducerFactory);
    }

    /**
     * 3. Inject the Template into the Aspect.
     */
    @Bean
    public AuditAspect auditAspect(KafkaTemplate<String, Object> auditKafkaTemplate, AuditProperties properties) {
        return new AuditAspect(auditKafkaTemplate, properties);
    }
}
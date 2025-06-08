//package com.iotmining.common.base.config;
//
//import com.iotmining.common.base.context.TenantContext;
//import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class TenantAwareCqlSessionConfigurer {
//
//    @Bean
//    public CqlSessionBuilderCustomizer cqlSessionBuilderCustomizer() {
//        return builder -> {
//            String tenantId = TenantContext.getTenantId();
//
//            if (tenantId != null) {
//                String keyspace = tenantId.toLowerCase().replaceAll("[^a-z0-9]", "") + "_ks";
//                builder.withKeyspace(keyspace);
//            }
//            // Otherwise uses default keyspace from application.yml
//        };
//    }
//}

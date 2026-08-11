package com.iiotedge.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enables @PreAuthorize
@ConditionalOnProperty(name = "iiotedge.security.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityAutoConfiguration {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public StatelessJwtFilter statelessJwtFilter() {
        return new StatelessJwtFilter(jwtSecret, applicationName);
    }

    @Bean("tenantSecurity")
    public TenantSecurityEvaluator tenantSecurityEvaluator() {
        return new TenantSecurityEvaluator();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, StatelessJwtFilter jwtFilter,
            ObjectProvider<CorsConfigurationSource> corsConfigurationSource) throws Exception {

        // A consuming service opts into Spring-managed CORS by defining its own
        // CorsConfigurationSource bean (typically gated behind @Profile("dev")).
        // Without one - the default in prod, where Nginx/the gateway already adds
        // CORS headers for this service's routes - CORS stays disabled here to
        // avoid duplicate Access-Control-Allow-Origin headers.
        CorsConfigurationSource corsSource = corsConfigurationSource.getIfAvailable();
        if (corsSource != null) {
            http.cors(cors -> cors.configurationSource(corsSource));
        } else {
            http.cors(AbstractHttpConfigurer::disable);
        }

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Preflight requests never carry credentials/body - always safe to allow,
                        // and must never be blocked by anyRequest().authenticated() below.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
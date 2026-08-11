package com.iiotedge.audit.aspect;

import com.iiotedge.audit.annotation.Auditable;
import com.iiotedge.audit.config.AuditProperties;
import com.iiotedge.audit.model.AuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AuditProperties auditProperties;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    // 1. Handle Success
    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Auditable auditable, Object result) {
        publishEvent(joinPoint, auditable, "SUCCESS", null);
    }

    // 2. Handle Failure (Exceptions)
    @AfterThrowing(pointcut = "@annotation(auditable)", throwing = "ex")
    public void logFailure(JoinPoint joinPoint, Auditable auditable, Throwable ex) {
        publishEvent(joinPoint, auditable, "FAILURE", ex.getMessage());
    }

    private void publishEvent(JoinPoint joinPoint, Auditable auditable, String status, String errorReason) {
        if (!auditProperties.isEnabled()) return;

        try {
            // A. Resolve Resource ID (SpEL)
            String resolvedResourceId = resolveSpel(joinPoint, auditable.resourceId());

            // B. Capture Context (Security & Request)
            String currentUser = getCurrentUser();
            String currentTenant = getCurrentTenant(); // Implement based on your TenantContext
            String currentIp = getCurrentIp();

            // C. Build Event
            AuditEvent event = AuditEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .timestamp(Instant.now())
                    .application(auditProperties.getApplicationName())
                    .tenantId(currentTenant)
                    .actorType(currentUser.equals("SYSTEM") ? "SYSTEM" : "USER")
                    .actorId(currentUser)
                    .ipAddress(currentIp)
                    .action(auditable.action())
                    .resourceType(auditable.resourceType())
                    .resourceId(resolvedResourceId)
                    .status(status)
                    .failureReason(errorReason)
                    .build();

            // D. Send to Kafka (Async)
            kafkaTemplate.send(auditProperties.getKafkaTopic(), event.getTenantId(), event);
            log.debug("Audit event sent: {}", event);

        } catch (Exception e) {
            log.error("Failed to publish audit event. This should not break business logic.", e);
        }
    }

    // --- Helper Methods ---

    private String resolveSpel(JoinPoint joinPoint, String expression) {
        if (expression == null || expression.isEmpty()) return "N/A";

        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        StandardEvaluationContext context = new StandardEvaluationContext();

        // 1. Add "a0", "a1", "p0", "p1" (Robust Fallback)
        for (int i = 0; i < args.length; i++) {
            context.setVariable("a" + i, args[i]);
            context.setVariable("p" + i, args[i]);
        }

        // 2. Add real names if available (Nice to have)
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        try {
            Object value = parser.parseExpression(expression).getValue(context);
            return value != null ? value.toString() : "NULL";
        } catch (Exception e) {
            // Log less noisy warning
            log.warn("Audit SpEL Error: Could not resolve '{}'. Available vars: a0..a{}", expression, args.length-1);
            return "UNKNOWN";
        }
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return auth.getName(); // Or auth.getPrincipal() if using JWT object
        }
        return "SYSTEM";
    }

    private String getCurrentIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                return attrs.getRequest().getRemoteAddr();
            }
        } catch (Exception ignored) {
        }
        return "UNKNOWN";
    }

    private String getCurrentTenant() {
        // Replace with your actual TenantContext logic
        // return TenantContext.getTenantId();
        return "default";
    }
}
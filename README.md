# common

Shared libraries for IoTMining/IIoTEdge microservices - data model enums,
base notification DTOs, and two Spring Boot "starter" auto-configuration
modules used by multiple services.

## Modules

| Module | Coordinates | What it is |
|---|---|---|
| `data` | `com.iotmining.common:data` | Shared enums (`TenantType`, `TenantAccessLevel`, `DeviceCategory`, `DeviceType`) - the canonical values services like auth-service and tenant-management-service both depend on matching exactly |
| `base` | `com.iotmining.common:base` | Shared notification DTOs (`BaseRequest`, `NotificationRequest`/`Response`, payload types) |
| `interfaces` | `com.iotmining.common:interfaces` | Notification sender interfaces (`NotificationSender`, `SmsProvider`) |
| `audit` | `com.iiotedge:iiotedge-audit-starter` | Spring Boot auto-configuration for audit logging (`@Auditable` + AOP aspect) - already consumed by auth-service |
| `security` | `com.iiotedge:iiotedge-security-starter` | Spring Boot auto-configuration for stateless JWT validation + tenant-aware RBAC (`StatelessJwtFilter`, `TenantSecurityEvaluator`) - already consumed by tenant-management-service |
| `proto` | `com.iotmining.common:proto` | Protobuf/gRPC definitions and generated code |
| `utils` | `com.iotmining.common:utils` | Small standalone utilities (e.g. `JsonMapConverter`) |
| `message` | `com.iotmining.common:message` | Empty placeholder - no source yet, see `TODO.md` |
| `transport` | `com.iotmining.common:transport` | Empty placeholder - no source yet, see `TODO.md` |

## Versions

| Spring Boot generation | `iiotedge-parent` | `iiotedge-audit-starter` / `iiotedge-security-starter` | `data`, `base`, `interfaces`, `proto`, `utils`, `message`, `transport` |
|---|---|---|---|
| Spring Boot 4.0.9 / Spring Cloud 2025.1.4 | `2026.11.18` | `2.0.0` | `0.0.2-SNAPSHOT` |
| Spring Boot 3.4.4 / Spring Cloud 2024.0.2 | `2025.08.18` | `1.0.0` | `0.0.1-SNAPSHOT` |

The Boot 4 builds deliberately use new coordinates. The two starters are
Spring Boot auto-configuration and do not work across Boot generations, and a
Boot 4 build installed or published under the old coordinates would silently
replace the Boot 3 jar for every service still on `iiotedge-parent` 2025.08.18.
A service moves to the new versions in the same change that moves it to
`iiotedge-parent` 2026.11.18.

Boot 4 notes for consumers:
- `iiotedge-audit-starter` 2.0.0 depends on `spring-boot-starter-kafka` (Boot 4
  moved the Kafka auto-configuration out of `spring-boot-autoconfigure`), so a
  consuming service keeps Boot's Kafka auto-configuration as it had on Boot 3.
  Audit events are still serialized with Jackson 2 (`JsonSerializer`, now
  deprecated in Spring Kafka 4), so the `iiotedge.audit.events` payload format
  is unchanged.
- Both starters use `spring-boot-starter-webmvc` and `spring-boot-starter-aspectj`
  (the Boot 4 names of `-web` and `-aop`).

## Parent

Every module parents off `com.iiotedge.build:iiotedge-parent` (published to
GitHub Packages from [`iiotedge/platform-build`](https://github.com/iiotedge/platform-build)),
resolved by GAV over the network rather than a local relative path - the
same pattern auth-service now uses. See platform-build's own README for
the full rationale and the two real adoption costs (Spotless formatting,
strict dependency convergence) worth knowing before touching these POMs.

There used to be a `common`-local aggregator `pom.xml` at this repo's root;
it's been removed. None of the 9 modules actually parented off it (they all
parented directly off the `iotmining` monorepo's root pom instead), so it
wasn't providing real reactor grouping - just an inconsistent `<modules>`
list (several modules commented out, several real modules never added).
Each module now stands alone; there's no single command that builds all 9
in one reactor. Build them individually, in dependency order where it
matters: `data` before `base` before `interfaces`.

## Building

```bash
mvn -f data/pom.xml install
mvn -f base/pom.xml install
mvn -f interfaces/pom.xml install
mvn -f audit/pom.xml install
mvn -f security/pom.xml install
mvn -f proto/pom.xml install
mvn -f utils/pom.xml install
```

Needs GitHub Packages credentials to resolve `iiotedge-parent` - see
platform-build's README "Dev machine setup".

## Known compatibility fix worth knowing about

`security`'s JJWT dependency was bumped from 0.11.5 to 0.12.6 (matching
what `iiotedge-bom` pins and what auth-service already used - two major
versions of the same library resolving in one dependency tree is exactly
the kind of thing `dependencyConvergence` exists to catch, and did, the
first time this module was pulled through the shared BOM). JJWT 0.12
removed `Jwts.parserBuilder()`/`parseClaimsJws()`/`getBody()` entirely
(not just deprecated) - `StatelessJwtFilter` was updated to the current
`Jwts.parser()`/`parseSignedClaims()`/`getPayload()` fluent API. If you're
consuming `iiotedge-security-starter` and see a `NoSuchMethodError` on the
old API, you're on a stale cached JAR - reinstall.

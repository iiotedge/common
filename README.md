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

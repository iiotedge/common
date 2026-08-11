# TODO

Known gaps, prioritized. See `CHECKLIST.md` for the full done/open list.

## Resolved (kept here briefly for context, not action items)

- ~~All 9 modules resolved their parent via a local relative path to the
  `iotmining` monorepo root~~ - migrated to `iiotedge-parent`, resolved by
  GAV over the network, matching auth-service's own migration.
- ~~`base`'s `<version>` was missing entirely~~ - would have silently
  inherited `iiotedge-parent`'s own version (2025.08.18) instead of staying
  at `0.0.1-SNAPSHOT`, breaking every consuming service's explicit
  version-pinned dependency on it. Added back explicitly.
- ~~`proto`'s `<groupId>`/`<version>` were missing entirely~~ - same class
  of bug, same fix.
- ~~`security` module was on Java 17 and JJWT 0.11.5~~, both inconsistent
  with the rest of the platform (21 / 0.12.6) - fixed. JJWT 0.12 removed
  `Jwts.parserBuilder()` entirely (not just deprecated) - `StatelessJwtFilter`
  updated to the current fluent API, verified by an actual failing
  compile, not assumed compatible.
- ~~`message`'s parent pointed at a `bck/pom.xml` with its entire
  `<project>` commented out~~ - was never buildable. Fixed.
- ~~`target/` build directories were tracked in git history~~ (including a
  full Javadoc HTML site under `base/target/site/apidocs/`) - untracked,
  `.gitignore` now actually covers `target/`/`.DS_Store` (previously
  covered neither).
- ~~A fully dead, 100%-commented-out duplicate file~~
  (`NotificationResponse1.java`, unreferenced anywhere) - removed.
- ~~Local git remote had a live PAT embedded in the URL and pointed at the
  pre-rename org~~ - repointed to a clean `iiotedge/common` URL. The leaked
  token itself still needs rotating on GitHub's side - that part couldn't
  be done from here.

## Empty modules

- [ ] **`message` and `transport` have no source at all** (`src/` doesn't
      exist in either). Both build successfully (empty jars) now that
      their parent/build config is fixed, but there's nothing in them.
      Worth a real decision: populate them, or remove them if they're not
      actually planned.

## Cleanup

- [ ] Three pre-existing files still have real, uncleaned dead code -
      `base/.../config/TenantAwareCqlSessionConfigurer.java`,
      `base/.../notifications/NotificationDtoImpl.java`, and especially
      `base/.../ddl/cassandra/DDLGenerator.java` (82 commented lines) -
      not touched in this pass since they predate the changes actually
      being reviewed here, but worth the same cleanup treatment.
- [ ] No module has a JaCoCo/SpotBugs quality gate configured, unlike
      auth-service/tenant-management-service which both enforce
      >=85%/>=65% coverage and a clean SpotBugs+FindSecBugs pass locally.
      `iiotedge-parent`'s `pluginManagement` has both available - each
      module just needs to activate them (a bare `<plugin>` entry, no
      version needed) the same way auth-service/TMS do.
- [ ] No tests exist in any of the 9 modules.

## Publishing

- [ ] **None of these modules are published anywhere** - still only
      resolvable via local `mvn install`, which is the actual reason
      auth-service's CI workflow needs to check out this repo and build
      `data`/`base`/`audit` from source instead of just being a normal
      Maven dependency. Publishing these to GitHub Packages (matching how
      `iiotedge-parent`/`iiotedge-bom` now work) is the real fix - see
      platform-build's own release workflow as the template. Until that
      happens, auth-service (and any other consumer) can't build from a
      clean clone without also checking out this repo.

## Consistency

- [ ] `data`, `base`, `interfaces`, `proto`, `utils`, `message`, `transport`
      are still versioned as plain `0.0.1-SNAPSHOT` (matching how
      auth-service's pom.xml currently references them) - worth deciding
      whether these should eventually get real, independent version
      numbers once they're actually published, the same way
      `iiotedge-audit-starter`/`iiotedge-security-starter` already do
      (real `1.0.0`, not a perpetual SNAPSHOT).

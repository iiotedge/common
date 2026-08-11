# Production Readiness Checklist

What's actually verified vs. still open. See `TODO.md` for the reasoning
behind each unchecked item.

## Build & dependency correctness

- [x] All 9 modules resolve their parent from GitHub Packages
      (`iiotedge-parent`), not a local relative path
- [x] `base`'s and `proto`'s missing `<version>`/`<groupId>` (would have
      silently inherited the parent's own coordinates) fixed
- [x] `security`'s Java version (17->21) and JJWT version (0.11.5->0.12.6)
      brought in line with the rest of the platform
- [x] The JJWT 0.12 API break this caused (`parserBuilder()` removed, not
      just deprecated) fixed in `StatelessJwtFilter`, confirmed by an
      actual failing compile before the fix, not assumed
- [x] `message`'s previously-broken parent reference (`bck/pom.xml`, no
      live `<project>`) fixed
- [x] Every module verified with a real `mvn clean install`, in dependency
      order (`data` -> `base` -> `interfaces`)
- [x] Both real downstream consumers re-verified after these changes:
      auth-service (185/185 tests, SpotBugs clean, JaCoCo gate met) and
      tenant-management-service (34/34 excluding the one DB-dependent test
      that needs a live Postgres this environment doesn't have - same
      pre-existing, documented limitation as before, not a new regression)

## Repo hygiene

- [x] `target/` build directories untracked from git (were committed,
      including a full Javadoc HTML site) and `.gitignore` fixed to
      actually cover `target/`/`.DS_Store` going forward
- [x] Local git remote repointed to a clean `iiotedge/common` URL (no
      embedded credential, correct org)
- [x] One fully-dead, unreferenced duplicate file removed
      (`NotificationResponse1.java`)
- [x] `LICENSE` added (MIT, matching auth-service/tenant-management-service)
- [x] `README.md`/`TODO.md`/`CHECKLIST.md` added, matching the platform's
      documentation convention

## Not done

- [ ] No quality gates (JaCoCo/SpotBugs) activated in any of the 9 modules
- [ ] No tests in any module
- [ ] `message`/`transport` are still empty placeholders - not populated,
      not removed, just fixed so they at least build
- [ ] Not published anywhere - still local-`mvn install`-only, which is
      the actual reason auth-service's CI can't build from a clean clone
      without also checking out this repo
- [ ] Three pre-existing files with real dead code left uncleaned
      (see `TODO.md`)

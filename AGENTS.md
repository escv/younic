# AGENTS.md

## What this is

younic — a lightweight file-based CMS on an OSGi/Felix runtime. This is a **bnd (bndtools) Gradle workspace**, not a plain Gradle Java project.

## Build system (read before running anything)

- The workspace is wired in `settings.gradle` (`apply plugin: 'biz.aQute.bnd.workspace'`). Bundles are bnd modules: each directory has a `bnd.bnd` + `src/` + `test/`, driven by bnd, **not** by a per-module `build.gradle`. Only `net.younic.core.dispatcher/build.gradle` adds a task.
- `gradle.properties` sets `bnd_version=7.0.0`; the wrapper (`gradlew`) is Gradle **8.10.2**.
- **Java 21.** Every `bnd.bnd` sets `javac.source/target: 21`. Build with a JDK 21 (`JAVA_HOME` may need to point at a 21 install; the daemon picks it up from `JAVA_HOME`). The container image is `openjdk:21-jre-slim`.
- Root `build.gradle` quirk: `compileJava.dependsOn clean` — `compileJava` always wipes the previous build first.

### Commands

- Full verify (same as CI / `.travis.yml`): `./gradlew clean test jar dist packageZip --continue`
- Run standalone OSGi app: `./gradlew startServerStandalone` (task defined only in `net.younic.core.dispatcher/build.gradle`)
- Build + run as Docker container: `./gradlew clean compileJava dist docker dockerRun`

### Tests

- JUnit 4, declared per-bundle via `-testpath: ${junit}` (resolved to `org.apache.servicemix.bundles.junit;4.12` in `cnf/build.bnd`). Mocking uses **jmock** (`org.jmock:jmock`, `jmock-junit4`), not Mockito.
- Test sources live in each bundle's `test/` dir with resources under `test/resources/`.

## Module map

| Bundle | Role |
| --- | --- |
| `net.younic.core.api` | Public interfaces (`Resource`, `IResourceProvider`, `IResourceRenderer`, `IResourceContentProvider`, `IResourcePersistence`, …) |
| `net.younic.core.fs` | Filesystem provider/content/persistence; reads `net.younic.cms.root` |
| `net.younic.content` | Resource converters (txt, html, csv, xml, docx, markdown) + aggregation. API in `net.younic.content`, impl in `net.younic.content.internal` |
| `net.younic.core.dispatcher` | Central `Activator` — main runtime entrypoint, docroot + bundle wiring |
| `net.younic.tpl.thymeleaf` | Thymeleaf rendering |
| `net.younic.cache` | Resource caching |
| `net.younic.sync` / `net.younic.sync.git` | Content sync (API / git implementation) |
| `net.younic.admin` | Admin/fileadmin backend |
| `younic-fileadmin` | React admin UI (Create React App). **Standalone npm app** — no Gradle build. Use `npm start`/`test`/`build` inside the dir. |

## Running / configuration

- OSGi runs are defined in `.bndrun` files. Canonical: `net.younic.core.dispatcher/launch.bndrun` (lists all `-runbundles`). Personal dev copies are named `launch.{name}.bndrun`.
- Required property: **`net.younic.cms.root`** — many bundles throw `BundleException` at activation if it is missing. Others: `net.younic.devmode` (true/false), `net.younic.admin.fileadmin-root` (optional; missing → fileadmin disabled), `org.osgi.service.http.port`.
- Container/standalone path uses env vars instead: `YOUNIC_CMS_ROOT_GIT` (git URL cloned on startup in `cnf/run/start.sh`), `YOUNIC_RUN_ADMIN=true` to enable the admin bundle.

## Gotchas

- `cnf/` is the bnd workspace config. `cnf/build.bnd` declares Maven Central + Local/Templates/Release repos. `cnf/local/` contains manually-vendored OSGi jars (thymeleaf-bundle, jsoup, txtmark, attoparser, jaxb, activation) that bnd.bnd files pin by version — edit versions carefully.
- `Bundle-Version` is `0.0.0.${tstamp}` (timestamp-based), so bundle versions are not stable across builds.
- Content tests use package `net.youni.content.internal` (typo — missing `c`) while source is `net.younic.content.internal`. Don't "fix" the test package names.
- Build output goes to each bundle's `generated/` dir (gitignored), not the default Gradle `build/`.
- The container runtime in `cnf/run/` is kept in sync with `net.younic.core.dispatcher/launch.bndrun` (verify with a manifest audit of `cnf/run/bundle/*.jar` against the `-runbundles` pins). `cnf/run/start.sh` passes `-Djdk.util.zip.disableZip64ExtraFieldValidation=true` — required because legacy third-party jars (Aries JAX-RS embedded libs) fail JDK 21's strict zip validation. Don't remove the flag without upgrading those artifacts. The vendored activation jar (`cnf/local/activation-1.1-osgi.jar`, copied into `cnf/run/bundle/`) has the placeholder BSN `target` — odd but intentional.
- Admin-only REST jars (aries JAX-RS stack, `jaxb-api`, `activation`, `javax.xml.soap-api`, servicemix specs) live in `cnf/run/bundle-adm/`, **not** in `cnf/run/bundle/` — `net.younic.admin` is only loaded with `YOUNIC_RUN_ADMIN=true`. `cnf/run/bundle/` keeps only the core runtime (incl. `org.osgi.util.function/promise`, needed by SCR).
- `container/Dockerfile` is multi-stage: it unzips `younic.zip` in a builder stage and `jlink`s a trimmed JRE (final image ≈ 100MB, plain alpine + git). The `--add-modules` list was derived with `jdeps --print-module-deps` over `bundle/*.jar`, `bundle-adm/*.jar` and their embedded libs; when adding third-party bundles, re-run jdeps and extend the list. Shell scripts under `cnf/run/` are POSIX `#!/bin/sh` (alpine has no bash).
- `packageZip` excludes build junk (`felix-cache`, `cms-root`, `bundle/{generated,reports,test-reports,tmp,distributions}`, `younic.pid`, `info/`) — keep it that way; the zip is the Docker build context payload.

Drop a locally built `okaether-core.aar` (or `.jar`) here to test against
it. `app/build.gradle.kts` picks up any `.aar`/`.jar` in this folder
automatically via `fileTree`.

If you do this, also delete the stand-in package at
`app/src/main/java/com/aethersentinel/core/` — those files declare the exact
same public API (classes, functions, and enums) documented for the real SDK
purely so this project compiles and demos correctly before the real
dependency is wired in. Having both the real `.aar` and the stand-in package
present at the same time will cause duplicate-class build errors.

For production, prefer the Maven Central coordinate instead of a local
`.aar` — see the commented-out line in `app/build.gradle.kts`.

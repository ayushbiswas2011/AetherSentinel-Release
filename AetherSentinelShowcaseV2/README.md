# OkAether Showcase

A production-ready, ultra-modern Jetpack Compose showcase app built to
market the **OkAether** Android SDK — a drop-in library that stops
unnecessary API retry storms and slashes server bills by up to 40%.

**Official release repository:**
https://github.com/ayushbiswas2011/OkAether-Release

**Maven Central:**
`io.github.ayushbiswas2011:OkAether:1.0.0`

## Quick start

1. Open this folder in Android Studio (Koala/Ladybug or newer).
2. This project ships ready-to-run out of the box using a **local stand-in**
   of the OkAether public API (`app/src/main/java/com/aethersentinel/core/`)
   that mirrors the real SDK's exact classes, functions, and enums.
3. To ship against the real SDK, either:
   - **Maven Central (recommended):** uncomment the dependency line in
     `app/build.gradle.kts`:
     `implementation("io.github.ayushbiswas2011:OkAether:1.0.0")`
   - **1-Click Mirror:** apply via
     `apply from: 'https://raw.githubusercontent.com/ayushbiswas2011/OkAether-Release/main/init.gradle'`
   - **Local `.aar`:** drop `okaether-core.aar` into `app/libs/`.
   Either way, then **delete** the stand-in package at
   `app/src/main/java/com/aethersentinel/core/` — having both present
   causes duplicate-class errors.
4. Run on any device/emulator running API 31+ (Android 12+).

## Viral marketing features

- **Cloud Cost Saved Counter** on the dashboard — animated live dollar figure
  showing how much OkAether is saving by suppressing retry storms.
- **Shareable Scorecard** — one-tap to format live telemetry into a pre-filled
  Twitter/LinkedIn share text.
- **Pulsing GitHub FAB** — deep-links directly to OkAether-Release.
- **Top-bar Share Sheet** — native Android share intent with a pre-crafted
  viral message.
- **Live Terminal Console** — haptic-enabled, chronological monospace log
  persisting across all three tabs.

## File architecture

```
app/src/main/java/com/aethersentinel/
├── core/                          # OkAether API stand-in (delete when using real SDK)
│   ├── OkAether.kt                # Entrypoint singleton
│   ├── AetherConfig.kt
│   ├── AetherSentinelInterceptor.kt
│   ├── LicenseModels.kt           # LicenseTier, Feature, LicenseStatus, LicenseGatekeeper
│   ├── PhyHarvesterModels.kt      # RawTelemetrySnapshot, TelemetrySample, PhyHarvester
│   └── LedgerModels.kt            # LedgerState, LedgerEntry, LedgerDatabase
└── showcase/
    ├── OkAetherApp.kt             # Application class — bootstraps OkAether
    ├── MainActivity.kt            # Root Activity + OkAetherShowcaseRoot() composable
    ├── AppConstants.kt            # URLs, share message, code snippets
    ├── data/
    │   └── AetherRepository.kt    # Single adapter to OkAether SDK
    ├── viewmodel/
    │   ├── TerminalLogViewModel.kt
    │   ├── AppEventsViewModel.kt
    │   ├── PlaygroundViewModel.kt  # Tab 1 — cost counter, security check, telemetry, interceptor
    │   ├── SandboxViewModel.kt     # Tab 2 — copy snippets, test implementation
    │   ├── LedgerViewModel.kt      # Tab 3 — queue depth, reconciliation loop
    │   └── ViewModelFactory.kt
    └── ui/
        ├── theme/                  # Color.kt, Type.kt, Theme.kt (OkAetherShowcaseTheme)
        ├── navigation/AppNavigation.kt
        ├── components/
        │   ├── GlassCard.kt
        │   ├── TerminalLogView.kt  # Haptic-enabled live console
        │   ├── RadarScanView.kt
        │   ├── PulseIndicator.kt
        │   ├── CodeBlock.kt
        │   └── NeonGauge.kt
        └── screens/
            ├── PlaygroundScreen.kt        # Tab 1 (Cloud Cost Saved + gauges + radar)
            ├── DeveloperSandboxScreen.kt  # Tab 2 (zero-friction code panels)
            └── LedgerStatusScreen.kt      # Tab 3 (heartbeat + queue depth)
```

## Architecture notes

- **Zero naming collision**: `OkAetherApp` is the `Application` subclass;
  `OkAetherShowcaseRoot()` is the root Composable — they are distinct.
- **MVVM + StateFlow**: each screen ViewModel exposes one `StateFlow<UiState>`;
  screens collect it with `collectAsStateWithLifecycle()`.
- **Single SDK adapter**: every call into `OkAether.*` goes through
  `data/AetherRepository.kt` — swap the stand-in for the real SDK in one file.
- **Shared terminal**: `TerminalLogViewModel` is Activity-scoped so all three
  tabs write into the same console with haptic feedback on every new line.

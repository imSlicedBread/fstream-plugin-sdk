# Changelog

## 0.4.0 — 2026-10-27

- General API 4 entry with optional actions, automation, configuration, and video-input capabilities.
- Credential-free immutable studio state and asynchronous scene-selection commands.
- Scene Tools example: manual scene action, opt-in live-transition automation, configurable target.
- Bounded serial callbacks, timeout quarantine, pinned actions, and idle hot reload in the host.
- Existing API 1–3 packages remain compatible. New artifact: `plugin-sdk-0.4.0.aar`.
- Release downloads include experimental GoPro Connect and DJI Osmo Action 5 Pro adapters.
  Both require Android 13+; physical-camera pairing and live video remain unverified.
- The legacy 0.3.0 artifact stays available. Use only one SDK artifact in a plugin.

## 0.3.0 — 2026-10-27

First standalone public SDK release under MIT.

- Java SDK interfaces for API 3, preserving API 1/2 compatibility.
- Optional camera discovery, local configuration forms, and host RTMP video input.
- Signed test-pattern 0.1.0 example and standalone Gradle build.
- Developer and publishing guides, GitHub Actions build, and SHA-256 release checksums.

Publisher certificate SHA-256 for the official test-pattern release:

```text
b43b1fc8f7e8101e35e3a5b86ffd75ef07ee026fdef9ef2ddd7ceaed4dfe3a19
```

Test-pattern plugin ID: `com.fstream.plugins.testpattern.release`.
Local debug builds use a different certificate. Change the plugin ID when making
your own development builds. This release does not ship camera vendor SDKs or
claim tested camera compatibility.

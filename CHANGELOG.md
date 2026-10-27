# Changelog

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

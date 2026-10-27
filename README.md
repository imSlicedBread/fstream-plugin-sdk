# fStream Plugin SDK

Build tools, automation, and video inputs for the **direct-download edition of fStream**.
SDK **0.4.0 / API 4** provides a general plugin entry point with optional capabilities.
Existing API 1–3 camera and video plugins remain supported.

## Download plugins

Open **Settings → Plugins → Download** in fStream. Choose a plugin, review its publisher,
and install it. Open the installed plugin's row to configure it, run or pin actions,
enable automation, or manage camera sources. Healthy plugins hot reload when idle;
updates to a live camera wait until its session stops. A hung plugin can still require
an app restart because extensions execute in-process.

The [latest release](https://github.com/imSlicedBread/fstream-plugin-sdk/releases/latest)
contains Scene Tools, Test Pattern, GoPro Connect, and DJI Osmo Action 5 Pro plugins.
Both camera adapters are experimental pending physical-camera testing, and require
Android 13+. GoPro targets HERO13 Black; DJI targets Action 5 Pro using the host RTMP
receiver and optional Bluetooth setup. Vendor libraries retain their own licenses.

Official release publisher SHA-256:
`b43b1fc8f7e8101e35e3a5b86ffd75ef07ee026fdef9ef2ddd7ceaed4dfe3a19`

Release camera IDs end in `.release`, distinguishing them from older locally debug-signed
packages. Existing debug inputs remain unchanged; add a source from the release package
when switching publishers. Removing a plugin does not erase its publisher binding.

## Build the developer SDK and examples

Install JDK 21 and Android SDK Platform 36. Set `ANDROID_HOME` or an untracked
`local.properties` containing `sdk.dir`. The standalone project needs no Rust or host checkout.

```powershell
.\gradlew.bat :plugin-sdk:assembleRelease :plugin-sdk:sourceReleaseJar :plugin-test-pattern:assembleDebug :plugin-scene-tools:assembleDebug
```

Use `./gradlew` on macOS/Linux. SDK output: `plugin-sdk/build/outputs/aar/plugin-sdk-release.aar`.
Examples are under each module's `build/outputs/apk/debug/` directory.

Download `plugin-sdk-0.4.0.aar` from Releases and depend on it with:

```kotlin
dependencies { compileOnly(files("libs/plugin-sdk-0.4.0.aar")) }
```

`./gradlew :plugin-sdk:publishToMavenLocal` publishes `com.fstream:plugin-sdk:0.4.0` locally.
It is not on Maven Central. The legacy `video-input-sdk-0.3.0.aar` remains available in
[release 0.3.0](https://github.com/imSlicedBread/fstream-plugin-sdk/releases/tag/v0.3.0).
**Use one SDK artifact only. Do not bundle SDK classes or depend on both artifacts.**

## Capabilities

- **Actions:** named manual tools, optionally pinned to streaming controls.
- **Automation:** separately opt-in callbacks for stream, recording, and active-scene changes.
- **Configuration:** host-rendered local forms, including choice lists.
- **Video input:** existing video producers, camera discovery, and RTMP input.

API 4 requires Android 11+. The host exposes scene IDs/names and credential-free status,
plus asynchronous selection of an existing scene. It does not expose broadcast start/stop,
recording start/stop, account credentials, or scene editing commands. Overlays, effects,
and audio extension points are reserved for future releases.

Start with [Scene Tools](plugin-scene-tools) for a non-video plugin, or
[Test Pattern](plugin-test-pattern) for a video producer. Change the plugin ID before
building with a different signing key. Debug builds are for development only.

- [API 4 capabilities and lifecycle](docs/general-plugins.md)
- [Migration from the video SDK](docs/migration-0.4.md)
- [Video-input guide](docs/developer-guide.md)
- [Signing and publishing](docs/publishing.md)

Plugins are trusted in-process code, not a security sandbox. GitHub hosting and a valid
signature do not establish code safety. The restricted/store edition does not load plugins.
SDK and example source are [MIT](LICENSE); see [third-party notices](THIRD_PARTY_NOTICES.md).

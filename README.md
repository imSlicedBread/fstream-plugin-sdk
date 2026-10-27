# fStream plugin SDK

Build video-input plugins for the **direct-download edition of fStream**.
SDK **0.3.0**, API **3**, includes Java interfaces, a developer guide, and a
working test-pattern plugin. Camera vendors can implement discovery,
configuration, and live video without depending on fStream implementation classes.

## Downloads

Get the SDK AAR and signed test-pattern APK from
[Releases](https://github.com/imSlicedBread/fstream-plugin-sdk/releases).
The SDK AAR is for developers; the plugin APK is imported by fStream users.

In a fStream version with GitHub downloads, open **Settings → Plugins → Download
from GitHub**, use `imSlicedBread/fstream-plugin-sdk`, select the test-pattern APK,
and review **Trust and install**. Then choose **Add Moving color bars** and activate
the saved input. Older direct builds can download the APK with a browser and use
**Settings → Plugins → Import plugin**. Do not install the plugin as a separate app.

Plugins run inside fStream with access to its permissions and data. Review the
publisher fingerprint before trusting one. GitHub hosting is not an endorsement.
The restricted/store edition does not load plugins.

## Build the SDK and example

Install JDK 17 or newer (JDK 21 is used in CI), Android SDK Platform 36, and the
Android build tools required by Android Gradle Plugin 9.4.1. Set `ANDROID_HOME`
or create an untracked `local.properties` with your `sdk.dir`.

```powershell
.\gradlew.bat :plugin-sdk:assembleRelease :plugin-test-pattern:assembleDebug
```

On macOS/Linux use `./gradlew` instead. The Gradle wrapper is included; this
project does not need the fStream app checkout, Rust, or vendor SDK packages.

Outputs:

- `plugin-sdk/build/outputs/aar/plugin-sdk-release.aar`
- `plugin-test-pattern/build/outputs/apk/debug/plugin-test-pattern-debug.apk`

For a standalone plugin project, copy the downloaded SDK to `libs/` and use:

```kotlin
dependencies {
    compileOnly(files("libs/video-input-sdk-0.3.0.aar"))
}
```

Do not include SDK classes inside your plugin APK. The host supplies them.
Alternatively, `./gradlew :plugin-sdk:publishToMavenLocal` publishes
`com.fstream:video-input-sdk:0.3.0` locally; this coordinate is **not published to
Maven Central**.

## Create a plugin

1. Start from `plugin-test-pattern`. Change the application ID, manifest plugin ID,
   display name, and entry class to your own namespace.
2. Implement `VideoInputPlugin`. Return a `VideoInputSession` that renders SDR
   frames to the supplied Android `Surface`. Call `onFirstFrame()` after submitting
   a frame, and stop all producer work in `close()` without releasing the Surface.
3. Add `assets/fstream-plugin.json`; use API 1 for a simple producer, API 2 for
   camera discovery/private dependencies, or API 3 for local settings and RTMP.
4. Sign your release APK with your own protected key. Keep the same certificate
   and increase the manifest `versionCode` for every update.
5. Attach the signed APK to a published GitHub release. Keep it under 128 MiB.
   Users can enter your repository or a specific release page in fStream.

The [developer guide](docs/developer-guide.md) describes the package schema,
permissions, discovery, configuration, RTMP receiver, lifecycle, and compatibility.
The [publishing guide](docs/publishing.md) covers signing and GitHub releases.

The public signed sample uses ID `com.fstream.plugins.testpattern.release`.
Locally debug-signed builds and official release-signed builds have different
publishers. Use your own plugin ID for development; a previously trusted ID cannot
switch certificates, even after removal.

## Scope and compatibility

- API 1: Android 8+ (API 26), basic video producers.
- APIs 2–3: Android 11+ (API 30); vendor SDKs may require newer Android versions.
- Video only; plugin audio is not transported. fStream manages its own audio.
- One active plugin producer/discovery/configuration lease at a time.
- API 3's RTMP receiver accepts H.264 video and reports connection status.
- This repository includes no GoPro, DJI, or Insta360 SDK binaries or camera
  adapters. Real-camera compatibility must be tested on the target hardware.

SDK and example source: [MIT](LICENSE). The Gradle wrapper is separately licensed
by Gradle under Apache 2.0; see [third-party notices](THIRD_PARTY_NOTICES.md).

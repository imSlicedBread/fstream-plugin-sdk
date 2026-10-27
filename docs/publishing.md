# Signing and publishing

The public SDK is an AAR. Plugins are APK files with an embedded
`assets/fstream-plugin.json` and a valid APK Signature Scheme v2 or v3 signature.
APK signing and the manifest plugin ID establish publisher identity. A SHA-256
download checksum detects changed files but does not replace signature validation.

## Build a signed plugin

Create and back up your own signing key using Android Studio or `keytool`.
Never commit a private key, keystore, password, vendor SDK binary, or camera
pairing data. The example reads signing settings from environment variables:

```powershell
$env:FSTREAM_PLUGIN_KEYSTORE='C:\secure\plugin-publisher.p12'
$env:FSTREAM_PLUGIN_KEY_ALIAS='plugin-publisher'
# Supply FSTREAM_PLUGIN_STORE_PASSWORD and FSTREAM_PLUGIN_KEY_PASSWORD securely.
.\gradlew.bat :plugin-test-pattern:assembleRelease
```

Without these settings, the release build remains unsigned and cannot be imported.
Debug builds use a local debug certificate and are for local development only.
Use a separate plugin ID for debug experiments so it cannot become bound to your
production publisher identity on a device.

Verify a release with Android build tools:

```powershell
apksigner verify --verbose --print-certs plugin-test-pattern-release.apk
Get-FileHash plugin-test-pattern-release.apk -Algorithm SHA256
```

Keep the signing-certificate SHA-256 fingerprint in your release notes. Users
compare it with fStream's trust dialog. Increment `versionCode` inside the plugin
JSON when changing the APK, and keep the same signing certificate for updates.
Changing only Android's versionCode is insufficient. Updates apply automatically when the affected plugin is idle; hung plugins require recovery.

## GitHub releases

Create a public repository and publish a release with the signed plugin APK as a
release asset. Source archives, AARs, and arbitrary Android APKs are not plugins.
fStream lists APK assets under 128 MiB and verifies their signatures, embedded
metadata, API compatibility, publisher continuity, and version before import.
It also checks the release asset size and GitHub SHA-256 digest when available.

Users can enter `owner/repository` for the latest stable release, or paste
`https://github.com/owner/repository/releases/tag/v1.0.0` for a specific release.
Private repositories and automatic updates are not supported by this first
download flow. Every installation requires publisher review.

SDK releases in this repository attach `video-input-sdk-<version>.aar`, the
SDK source JAR, the signed sample APK, and `SHA256SUMS.txt`. The CI build only
compiles and saves development artifacts; publishing and release signing remain
explicit maintainer operations.

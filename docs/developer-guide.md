# Developer guide

## Author a plugin

Use the reference `plugin-test-pattern` application module as a template.
Depend on the SDK with `compileOnly`; do not bundle its classes in the APK. The
SDK uses Java interfaces and Android platform types so authors can use Java or
Kotlin without a shared Compose or coroutine ABI.

```kotlin
dependencies {
    compileOnly(files("libs/plugin-sdk-release.aar"))
}
```

Alternatively, `:plugin-sdk:publishToMavenLocal` publishes
`com.fstream:video-input-sdk:0.3.0` and its source JAR for local development. No
public Maven repository publication has been performed.

Implement `VideoInputPlugin` with a public no-argument constructor. Its
`createSession(host, sourceId, configurationJson)` returns a `VideoInputSession`.
The host calls `start(surface, width, height, fps, events)` once. Produce SDR video
at the requested dimensions and cadence, then call `onFirstFrame()` only after
submitting the first frame. Call `onError()` for a terminal failure. The host times
out startup after 12 seconds by default; API 2 can declare a bounded timeout of
12–90 seconds for camera connection. There is no plugin audio transport.

`close()` must be idempotent, stop and join producer work, and prevent further
callbacks or surface access before returning. The surface belongs to fStream;
never release it. A surface change creates a new session. Avoid blocking host
threads for camera discovery, I/O, or frame production. Do not change process-wide
network bindings. Only one plugin producer can run at a time in this version.

`PluginHost` provides application context and a per-plugin directory under
`noBackupFilesDir`. Store device-local credentials there. Persist only non-secret
configuration in a source's JSON object, because scene configuration can be synced.
The test pattern uses `{}`. API 2's optional `DiscoverableVideoInputPlugin`
returns a `VideoInputDiscovery`. Its callbacks report `DiscoveredVideoInput`
records containing a device name, ID, and non-secret configuration JSON. fStream
shows the selection dialog and saves that configuration. Discovery must stop and
suppress callbacks when closed. It shares the single-active-plugin limit with
streaming. Callbacks can arrive on any thread; networking must be asynchronous.

API 3 optionally implements `ConfigurableVideoInputPlugin.openConfiguration`.
It returns a `PluginConfiguration` lease with `getSchemaJson`, `save(valuesJson)`,
and idempotent `close`. The host renders fields and invokes these methods on IO.
Schema JSON is capped at 32 KiB and 16 fields; every field has a unique `key`,
`label`, string `value`, optional `secret`, and optional `readOnly`. Save receives
an object mapping editable keys to string values. Validate values before writing
them atomically to the private directory. The host retains entered secrets only
in memory; it never writes them to saved-instance or scene state. Configuration
shares the exclusive lease with scanning and streaming and does not need camera
runtime permissions.

`PluginHost.createRtmpReceiver(port, path)` returns a `VideoInputSession` that
reuses fStream's AVC decoder. Start it before requesting camera video; close it
when your session closes. Port must be 1024–65535; path is `app/key`, using
letters, numbers, underscores or hyphens, with a key of 16–128 characters.
The receiver binds all local interfaces, accepts one publisher at a time, gates
media by exact app/key, and discards camera audio. It emits `onFirstFrame` only
after decoded video is submitted to the Surface, including after reconnects.
It never releases the host Surface. Do not log the key or include it in scene JSON.

The optional `VideoInputEvents.onStatus` default method reports connection
progress or a lost connection; a subsequent `onFirstFrame` restores Live. This
does not reset the initial 12–90 second startup deadline. Do not send routine
connection progress after the video is live. `onError` is terminal. New methods
have defaults so existing API 1 and 2 packages continue to load.

## Package contract

The distribution file is an APK with a valid APK Signature Scheme v2 or v3
signature and exactly one signing certificate. Include this UTF-8 asset:

```json
{
  "formatVersion": 1,
  "apiVersion": 1,
  "id": "org.example.pattern",
  "name": "Example pattern",
  "versionCode": 1,
  "versionName": "1.0",
  "minAndroidApi": 26,
  "entryClass": "org.example.pattern.PatternPlugin",
  "sources": [{"id": "bars", "name": "Color bars"}]
}
```

Save it as `src/main/assets/fstream-plugin.json`. Package size is limited to 128
MiB, metadata to 32 KiB, and source count to 32. IDs must be stable. Increment
`versionCode` for every changed package; updates must use the original certificate.
Certificate rotation and downgrades are deliberately unsupported in API 1.
The publisher binding remains after removal to prevent takeover of an existing ID.

The host supplies the public SDK. API 1 retains parent-first class loading. Plugin
dependencies must not assume access to fStream implementation classes. Keep the
entry class and interface implementations when enabling shrinking/obfuscation.
Use platform code or relocate private dependencies where needed for API 1.
API 1 does not load Android resource tables or native `.so` packages.

API 2 requires `minAndroidApi >= 30`. It shares only framework classes and
`com.fstream.plugin.sdk.*`; all other dependencies must be packaged in the APK.
This prevents incompatible Kotlin, Media3, or Koin versions from reaching the
host. The context exposes APK resources using Android's resource-loader API.
Native libraries are extracted from the verified APK into an immutable,
hash-specific directory using the device's preferred supported ABI. Extraction
accepts only `lib/<known-abi>/lib<name>.so`, with count and size limits.
Native dependencies must satisfy Android's ABI and page-size requirements.

Set `discovery: true`, `startupTimeoutSeconds`, and a `permissions` array for
camera plugins. The supported runtime permissions are Bluetooth scan/connect,
Nearby Wi-Fi devices, and coarse/fine location. They are declared only in the
direct host. The host requests them before discovery and checks them again before
session creation. Declare only the runtime permissions your camera needs.

API 3 source objects may override `discovery` and `permissions`; omitted fields
inherit the package values. `configurable: true` exposes the local settings form.
For example, DJI's Bluetooth source requests scan/connect, while its Mimo source
has `discovery: false` and `permissions: []`. API 1/2 cannot declare these fields.

No API version registers plugin Activities, Services, Providers, manifest
initializers, or custom Compose UI. Vendor libraries that depend on those
components need additional integration. Imported code remains fully trusted;
dependency isolation is not a security sandbox.

## Trust, lifecycle, and recovery

Imported code executes inside fStream with its UID, permissions, and data access.
The API and class loader are not a sandbox. A valid signature authenticates a key;
it does not establish that a publisher is trustworthy or that the code is safe.
Publisher trust is explicit at import, before any constructor or plugin code runs.

Packages are copied to private storage, marked read-only before writing, verified
before import confirmation, verified again when committed, and verified on load.
Updates cannot replace an active producer. Loaded plugins reload when idle after updates; removals preserve immutable loaded files until process exit.
Missing, disabled, invalid, or incompatible plugins leave scene references intact.

An interrupted active plugin session leaves a persistent marker. On the next
process start that plugin is disabled, preventing automatic crash loops. This can
also happen after Android kills the process; users can re-enable the plugin.
In-process faults can still crash or hang fStream. Stopping a plugin is cooperative.

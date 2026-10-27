# Migrating to SDK 0.4.0

Existing API 1, 2, and 3 APKs continue to load unchanged. Their Java interfaces,
manifest version, saved source IDs, publisher bindings, and private settings are
preserved. A camera-only plugin does not need to migrate to gain idle hot reload.

For a general plugin:

1. Use `compileOnly(files("libs/plugin-sdk-0.4.0.aar"))` instead of the legacy AAR.
   Never depend on both artifacts, and never bundle SDK classes in the APK.
2. Implement `FStreamPlugin` and the optional capability interfaces you need.
3. Set `apiVersion` to 4, `minAndroidApi` to at least 30, and declare `capabilities`.
4. Declare stable action IDs for manual tools. Omit `sources` unless using video input.
5. Add host-rendered configuration if needed. Automation always requires separate
   user opt-in and must treat INITIAL as a snapshot.
6. Increment the manifest versionCode and sign with the same key when updating
   an existing plugin ID. Test alongside the existing camera plugins.

The new Maven-local coordinate is `com.fstream:plugin-sdk:0.4.0`.
The original `com.fstream:video-input-sdk:0.3.0` build and release downloads remain
available from tag v0.3.0. Neither artifact is currently on Maven Central.

Source-selection and camera interfaces are unchanged. API 4 video entries also
implement `VideoInputPlugin`; declare `video-input` and the usual source metadata.
Do not call host implementation classes, register Android components, or assume
the host shares your third-party dependencies.

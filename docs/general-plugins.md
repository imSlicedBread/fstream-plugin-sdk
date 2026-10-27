# API 4: general plugins

Implement `FStreamPlugin`, with any combination of `PluginActions`,
`PluginAutomation`, `ConfigurablePlugin`, and the existing `VideoInputPlugin`.
The entry needs a public no-argument constructor. API 4 requires Android 11+.
Declare exactly the capabilities you implement:

```json
{
  "formatVersion": 1,
  "apiVersion": 4,
  "id": "org.example.tools",
  "name": "My Tools",
  "versionCode": 1,
  "versionName": "1.0",
  "minAndroidApi": 30,
  "entryClass": "org.example.Tools",
  "capabilities": ["actions", "automation", "configuration"],
  "actions": [{"id": "switch", "name": "Switch scene"}]
}
```

Only `video-input` packages declare a nonempty `sources` list. Action IDs must
be unique within the package and remain stable for pinned shortcuts. Unknown
capabilities are rejected rather than silently ignored. A package may combine
video input and tools; existing camera exclusivity still applies to video leases.

## Host access

`onStart(PluginContext)` receives private on-device storage and `StudioControl`.
`getState()` returns an immutable scene list, active scene ID, stream status, and
recording status. No stream keys, account tokens, or destinations are included.
Call `selectScene(id, result)` asynchronously; never wait for its result from a
plugin callback. The result arrives on the same serial worker. Success means
the normal scene command was accepted; it does not guarantee remote OBS delivery.
Selecting the current scene is a no-op, and deleted scene IDs fail cleanly.

The API does not expose start/stop streaming, start/stop recording, scene editing,
or host implementation classes. This API boundary is not a sandbox: imported
Android code runs in fStream's process and shares its permissions and data access.

## Actions and automation

`runAction(actionId)` returns a short display message. Actions can be invoked
from plugin details or pinned to Tools in the streaming controls.

Automation is separately enabled by the user, never implicitly at installation.
`onEvent` receives `INITIAL`, `STREAM_STATUS`, `RECORDING_STATUS`, or `ACTIVE_SCENE`.
INITIAL is a snapshot, not a transition. Compare stream status with `Live` if you
need to react to going live. Recording uses `Recording`. Repeated state does not
generate repeat events. When several fields change, stream status is delivered
before recording status, followed by active scene. Plugin-originated scene changes
update the snapshot but do not emit ACTIVE_SCENE events, preventing feedback loops.

Enabled automation runs while the studio host exists, including background
streaming. It has no boot receiver, scheduler, or independent always-on service.
`onAutomationStopped()` stops subscriptions; `close()` releases all plugin work.
Threads started by a plugin must be stopped by that plugin.

## Threading, reload, and recovery

API 4 constructors and host-invoked callbacks run on a per-plugin serial worker,
off the UI thread. At most eight callbacks may wait. Every executing callback
must return within five seconds. A timeout quarantines that worker, rejects its
pending work, and requires an app restart; the host cannot safely kill an arbitrary
in-process thread. Legacy API 1–3 camera startup budgets remain unchanged.

Tools can run beside a camera plugin. Updates stop a general plugin's callbacks,
close its runtime, load a new class loader, and retain its private configuration.
Active video or configuration leases defer the update until closed. Normal
updates, disabling, and re-enabling do not require restarting the app. Native
libraries and DEX backing files may remain mapped until process exit.

An unclean process termination disables previously active plugins at next startup.
Users can re-enable them after inspecting the error. This also protects against
plugins repeatedly crashing the host during startup.

## Configuration

Return the existing `PluginConfiguration` interface from `openConfiguration()`.
The host serializes schema reads, saves, and close calls. Forms have up to 16
fields and 32 KiB of JSON. An optional `options` array adds a choice list:

```json
{"fields":[{"key":"scene","label":"Target scene","value":"scene-id",
"options":[{"value":"scene-id","label":"Main camera"}]}]}
```

Each option has a string value and label; at most 128 options are accepted.
Validate saved IDs against current state. Store credentials only in the private
plugin directory, never in scene configuration or logs.

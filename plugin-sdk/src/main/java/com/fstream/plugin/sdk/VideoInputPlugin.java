package com.fstream.plugin.sdk;

/** Public ABI v3 (v1 and v2 remain supported). Implementations need a public no-argument constructor.
 * Do not package this SDK in the plugin: use compileOnly. Calls arrive on a host
 * thread; do not block for network operations. Plugins own their worker threads.
 */
public interface VideoInputPlugin {
    int API_VERSION = 3;
    VideoInputSession createSession(PluginHost host, String sourceId, String configurationJson);
}

package com.fstream.plugin.sdk;

/** Optional ABI v2 extension. Discovery and streaming are mutually exclusive.
 * The host requests manifest permissions before constructing discovery or sessions.
 */
public interface DiscoverableVideoInputPlugin extends VideoInputPlugin {
    VideoInputDiscovery createDiscovery(PluginHost host, String sourceId);
}

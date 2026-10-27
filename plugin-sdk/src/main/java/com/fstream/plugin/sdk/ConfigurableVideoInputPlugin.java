package com.fstream.plugin.sdk;

/** SDK v3: optional device-local settings rendered by the host. */
public interface ConfigurableVideoInputPlugin extends VideoInputPlugin {
    PluginConfiguration openConfiguration(PluginHost host, String sourceId);
}

package com.fstream.plugin.sdk;
/** Uses the existing host-rendered configuration schema; no custom UI is loaded. */
public interface ConfigurablePlugin extends FStreamPlugin {
    PluginConfiguration openConfiguration();
}

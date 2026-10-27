package com.fstream.plugin.sdk;
/** Action IDs must be declared in the package manifest. Return a short user-facing result. */
public interface PluginActions extends FStreamPlugin {
    String runAction(String actionId);
}

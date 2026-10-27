package com.fstream.plugin.sdk;
/** Called only after separate user opt-in. INITIAL is a snapshot, never a transition. */
public interface PluginAutomation extends FStreamPlugin {
    void onEvent(StudioEvent event);
    default void onAutomationStopped() {}
}

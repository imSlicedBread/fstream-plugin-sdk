package com.fstream.plugin.sdk;
public interface StudioControl {
    StudioState getState();
    /** Asynchronous; result is delivered on the plugin's serialized worker.
     * Never block waiting for the result. A success means the normal scene command was
     * accepted, not that external OBS or network operations have completed. */
    void selectScene(String sceneId, Result result);
    interface Result { void onResult(boolean accepted, String message); }
}

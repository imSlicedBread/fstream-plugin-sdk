package com.fstream.plugin.sdk;

/** Callbacks may come from a producer thread. The host ignores callbacks after session close. */
public interface VideoInputEvents {
    /** SDK v3: connection progress; never include passwords or stream keys. */
    default void onStatus(String message) {}
    /** Call only after the first frame has been submitted to the Surface. */
    void onFirstFrame();
    /** Terminal failure; host will close this session. Do not include credentials in messages. */
    void onError(String message);
}

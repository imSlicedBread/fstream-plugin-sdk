package com.fstream.plugin.sdk;

/** API 4 entry point. All host-invoked callbacks are serialized off the UI thread.
 * Return within five seconds. These are trusted in-process extensions, not a sandbox. */
public interface FStreamPlugin extends AutoCloseable {
    int API_VERSION = 4;
    default void onStart(PluginContext context) {}
    @Override default void close() {}
}

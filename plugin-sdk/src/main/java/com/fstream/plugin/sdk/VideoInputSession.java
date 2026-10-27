package com.fstream.plugin.sdk;

import android.view.Surface;

/** One producer for one host-owned SDR surface. Never release the supplied Surface.
 * start is called once, close is idempotent and must join producer work before returning.
 * After close returns, the producer must not access the surface or invoke callbacks.
 * A replacement surface is delivered through a new session.
 */
public interface VideoInputSession extends AutoCloseable {
    void start(Surface surface, int width, int height, int fps, VideoInputEvents events);
    @Override void close();
}

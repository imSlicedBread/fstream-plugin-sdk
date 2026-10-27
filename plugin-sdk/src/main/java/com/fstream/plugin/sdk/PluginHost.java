package com.fstream.plugin.sdk;

import android.content.Context;
import java.io.File;

/** In-process convenience API, not a security sandbox. Configuration must not contain secrets. */
public interface PluginHost {
    Context getApplicationContext();
    /** Device-local directory excluded from Android backup. Put pairing secrets here. */
    File getPrivateDirectory();
    /** SDK v3: a video-only H.264 RTMP listener. Path must be app/key; port 1024..65535.
     * The returned session owns the listener until close, never the supplied Surface.
     * Start it before asking a camera to publish. Audio is intentionally discarded. */
    default VideoInputSession createRtmpReceiver(int port, String path) {
        throw new UnsupportedOperationException("RTMP receivers require SDK v3");
    }
}

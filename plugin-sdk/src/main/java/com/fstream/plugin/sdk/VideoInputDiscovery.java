package com.fstream.plugin.sdk;

/** Start once. close stops scanning and suppresses all further callbacks. */
public interface VideoInputDiscovery extends AutoCloseable {
    void start(Events events);
    @Override void close();
    interface Events {
        /** A complete replacement list. May arrive on any thread. */
        void onDevices(DiscoveredVideoInput[] devices);
        /** Terminal error. Do not include credentials. */
        void onError(String message);
    }
}

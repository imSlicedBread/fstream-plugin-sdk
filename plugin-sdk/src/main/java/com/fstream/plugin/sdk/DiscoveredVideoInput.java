package com.fstream.plugin.sdk;

/** Immutable device selection. Configuration must contain identifiers only, never credentials. */
public final class DiscoveredVideoInput {
    public final String id;
    public final String name;
    public final String configurationJson;
    public DiscoveredVideoInput(String id, String name, String configurationJson) {
        this.id = id;
        this.name = name;
        this.configurationJson = configurationJson;
    }
}

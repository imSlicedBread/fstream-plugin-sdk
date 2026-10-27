package com.fstream.plugin.sdk;
public final class StudioEvent {
    public enum Kind { INITIAL, STREAM_STATUS, RECORDING_STATUS, ACTIVE_SCENE }
    public final Kind kind;
    public final StudioState state;
    public StudioEvent(Kind kind, StudioState state) { this.kind = kind; this.state = state; }
}

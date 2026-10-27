package com.fstream.plugin.sdk;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/** Credential-free immutable state. Status strings are Idle/Starting/Live/Stopping/Error
 * for streaming and Idle/Starting/Recording/Stopping/Error for recording. Unknown values
 * may be added; integrations should compare only the values they understand. */
public final class StudioState {
    public final List<StudioScene> scenes;
    public final String activeSceneId;
    public final String streamStatus;
    public final String recordingStatus;
    public StudioState(List<StudioScene> scenes, String activeSceneId, String streamStatus, String recordingStatus) {
        this.scenes = Collections.unmodifiableList(new ArrayList<>(scenes));
        this.activeSceneId = activeSceneId;
        this.streamStatus = streamStatus;
        this.recordingStatus = recordingStatus;
    }
}

package com.fstream.plugins.scenetools;

import com.fstream.plugin.sdk.*;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

/** No video producer. Settings and actions share the host's serialized worker. */
public final class SceneToolsPlugin implements PluginActions, PluginAutomation, ConfigurablePlugin {
    private PluginContext context;
    private String target = "";
    private String result = "";
    @Override public void onStart(PluginContext context) {
        this.context = context;
        File settings = new File(context.getPrivateDirectory(), "target-scene.txt");
        if (settings.isFile()) try (FileInputStream input = new FileInputStream(settings)) {
            byte[] buffer = new byte[1024]; int count = input.read(buffer);
            if (count > 0) target = new String(buffer, 0, count, StandardCharsets.UTF_8);
        } catch (IOException error) { throw new IllegalStateException("Cannot read scene settings", error); }
    }
    @Override public String runAction(String actionId) {
        if (!"switch-scene".equals(actionId)) throw new IllegalArgumentException("Unknown action");
        switchScene();
        return "Scene switch requested";
    }
    private void switchScene() {
        if (target.isEmpty()) throw new IllegalStateException("Configure a target scene first");
        context.getStudio().selectScene(target, (accepted, message) -> result = message);
    }
    @Override public void onEvent(StudioEvent event) {
        if (event.kind == StudioEvent.Kind.STREAM_STATUS && "Live".equals(event.state.streamStatus)) switchScene();
    }
    @Override public PluginConfiguration openConfiguration() {
        return new PluginConfiguration() {
            @Override public String getSchemaJson() {
                try {
                    JSONArray options = new JSONArray();
                    for (StudioScene scene : context.getStudio().getState().scenes)
                        options.put(new JSONObject().put("value", scene.id).put("label", scene.name));
                    JSONObject field = new JSONObject().put("key", "target").put("label", "Target scene")
                        .put("value", target).put("options", options);
                    return new JSONObject().put("description", "Choose a scene for the action. If automation is enabled, switch when streaming becomes live. " + result)
                        .put("fields", new JSONArray().put(field)).toString();
                } catch (JSONException error) { throw new IllegalStateException(error); }
            }
            @Override public void save(String json) {
                try {
                    String selected = new JSONObject(json).getString("target");
                    boolean exists = false;
                    for (StudioScene scene : context.getStudio().getState().scenes) if (scene.id.equals(selected)) exists = true;
                    if (!exists) throw new IllegalArgumentException("Choose an existing scene");
                    File temporary = new File(context.getPrivateDirectory(), "target-scene.tmp");
                    try (FileOutputStream output = new FileOutputStream(temporary)) { output.write(selected.getBytes(StandardCharsets.UTF_8)); output.getFD().sync(); }
                    if (!temporary.renameTo(new File(context.getPrivateDirectory(), "target-scene.txt"))) throw new IOException("Cannot save settings");
                    target = selected;
                } catch (IOException | JSONException error) { throw new IllegalStateException("Cannot save scene", error); }
            }
            @Override public void close() {}
        };
    }
    @Override public void close() { context = null; }
}

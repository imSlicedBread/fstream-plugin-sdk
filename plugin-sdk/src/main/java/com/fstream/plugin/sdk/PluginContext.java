package com.fstream.plugin.sdk;
import java.io.File;
public interface PluginContext {
    File getPrivateDirectory();
    StudioControl getStudio();
}

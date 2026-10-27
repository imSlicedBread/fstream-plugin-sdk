package com.fstream.plugin.sdk;

/** An exclusive configuration lease; close before discovery or a session starts.
 * Schema: {"description":"...","fields":[{"key":"...","label":"...",
 * "value":"...","secret":false,"readOnly":false}]}.
 * At most 16 fields, 32 KiB JSON. Values are strings. Save receives a JSON object
 * mapping keys to strings. Secrets must remain in PluginHost.getPrivateDirectory(),
 * never in a discovered input's configurationJson or scene configuration.
 * Host invokes these methods on a worker thread; validation failures may throw.
 */
public interface PluginConfiguration extends AutoCloseable {
    String getSchemaJson();
    void save(String valuesJson);
    @Override void close();
}

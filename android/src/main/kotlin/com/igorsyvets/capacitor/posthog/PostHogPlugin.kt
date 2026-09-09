package com.igorsyvets.capacitor.posthog

import android.util.Log
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.posthog.PostHog
import com.posthog.android.PostHogAndroid
import com.posthog.android.PostHogAndroidConfig
import org.json.JSONObject

/**
 * Bridges the PostHog Android SDK into Capacitor.
 *
 * The SDK is configured on plugin load from `assets/posthog.config.json`,
 * which must supply an `apiKey` and a `host`.
 */
@CapacitorPlugin(name = "PostHog")
class PostHogPlugin : Plugin() {

    private companion object {
        const val TAG = "Capacitor/Plugin"
        const val CONFIG_FILE = "posthog.config.json"
    }

    override fun load() {
        super.load()
        configureSdk()
    }

    private fun configureSdk() {
        val config = try {
            val raw = context.assets.open(CONFIG_FILE).bufferedReader().use { it.readText() }
            JSONObject(raw)
        } catch (e: Exception) {
            Log.e(TAG, "PostHog Analytics: could not read $CONFIG_FILE", e)
            return
        }

        val apiKey = config.optString("apiKey").takeIf { it.isNotBlank() }
        val host = config.optString("host").takeIf { it.isNotBlank() }

        if (apiKey == null || host == null) {
            Log.e(TAG, "PostHog Analytics: $CONFIG_FILE is missing apiKey or host")
            return
        }

        Log.d(TAG, "PostHog Analytics: configuring plugin...")
        PostHogAndroid.setup(context, PostHogAndroidConfig(apiKey, host, captureScreenViews = false))
        Log.d(TAG, "PostHog Analytics: success")
    }

    /** Flattens a Capacitor [JSObject] argument into the map shape the PostHog SDK expects. */
    private fun PluginCall.propertyMap(name: String): Map<String, Any> {
        val source = getObject(name) ?: return emptyMap()
        return buildMap {
            source.keys().forEach { key ->
                source.opt(key)?.let { put(key, it) }
            }
        }
    }

    @PluginMethod
    fun capture(call: PluginCall) {
        val eventName = call.getString("event_name")
        if (eventName == null) {
            call.reject("event_name was not provided")
            return
        }
        Log.i(TAG, "PostHog Analytics: capturing event: $eventName")
        PostHog.capture(event = eventName, properties = call.propertyMap("properties"))
        call.resolve()
    }

    @PluginMethod
    fun screen(call: PluginCall) {
        val screenTitle = call.getString("screenTitle")
        if (screenTitle == null) {
            call.reject("screenTitle was not provided")
            return
        }
        Log.i(TAG, "PostHog Analytics: capturing screen: $screenTitle")
        PostHog.screen(screenTitle, call.propertyMap("properties"))
        call.resolve()
    }

    @PluginMethod
    fun identify(call: PluginCall) {
        val distinctId = call.getString("new_distinct_id")
        if (distinctId == null) {
            call.reject("new_distinct_id was not provided")
            return
        }
        Log.i(TAG, "PostHog Analytics: identifying: $distinctId")
        PostHog.identify(
            distinctId,
            call.propertyMap("userPropertiesToSet"),
            call.propertyMap("userPropertiesToSetOnce")
        )
        call.resolve()
    }

    @PluginMethod
    fun group(call: PluginCall) {
        val type = call.getString("type")
        val key = call.getString("key")
        if (type == null || key == null) {
            call.reject("type and key must both be provided")
            return
        }
        Log.i(TAG, "PostHog Analytics: identifying group: {$type: $key}")
        PostHog.group(type, key, call.propertyMap("properties"))
        call.resolve()
    }

    @PluginMethod
    fun reset(call: PluginCall) {
        Log.i(TAG, "PostHog Analytics: reset")
        PostHog.reset()
        call.resolve()
    }
}

package com.igorsyvets.capacitor.posthog


import android.content.Context
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


@CapacitorPlugin(name = "PostHog")
class PostHogPlugin : Plugin() {



    override fun load() {

        val jsonObject = JSONObject(context.assets.open("posthog.config.json").bufferedReader().use { it.readText() })

        val apiKey = jsonObject.getString("apiKey") ?: null
        val host = jsonObject.getString("host") ?: null

        if (apiKey != null && host != null) {
            val config = PostHogAndroidConfig(apiKey,host, captureScreenViews = false)
            PostHogAndroid.setup(context, config)
        }
        else {
            Log.e("Capacitor/Plugin", "PostHog Analytics: Config file with apiKey or host is missing")
        }

        super.load()

    }


    @PluginMethod
    fun capture(call: PluginCall) {
        val event_name = call.getString("event_name")
        // Assuming call.getObject("properties") returns a JSObject or null
        val jsProperties = call.getObject("properties")
        // Initialize an empty map which will be filled with the properties from jsProperties
        val properties = jsProperties?.let { jsObject ->
            val map = mutableMapOf<String, Any>()
            jsObject.keys().forEach { key ->
                if(jsObject.opt(key) != null) map[key] = jsObject.opt(key) ?: ""
            }
            map.toMap()
        } ?: emptyMap()



        if (event_name != null) {
            PostHog.capture(event = event_name, properties = properties)
        }
        call.resolve()
    }

    @PluginMethod
    fun screen(call: PluginCall) {
        val screenTitle = call.getString("screenTitle")
        // Assuming call.getObject("properties") returns a JSObject or null
        val jsProperties = call.getObject("properties")
        // Initialize an empty map which will be filled with the properties from jsProperties
        val properties = jsProperties?.let { jsObject ->
            val map = mutableMapOf<String, Any>()
            jsObject.keys().forEach { key ->
                if(jsObject.opt(key) != null) map[key] = jsObject.opt(key) ?: ""
            }
            map.toMap()
        } ?: emptyMap()


        if (screenTitle != null) {
            PostHog.screen(screenTitle,properties)
        }
        call.resolve()
    }

    @PluginMethod
    fun identify(call: PluginCall) {
        val new_distinct_id = call.getString("new_distinct_id")
        // Assuming call.getObject("properties") returns a JSObject or null
        val jsUserPropertiesToSet = call.getObject("userPropertiesToSet")
        // Initialize an empty map which will be filled with the properties from jsProperties
        val userPropertiesToSet = jsUserPropertiesToSet?.let { jsObject ->
            val map = mutableMapOf<String, Any>()
            jsObject.keys().forEach { key ->
                if(jsObject.opt(key) != null) map[key] = jsObject.opt(key) ?: ""
            }
            map.toMap()
        } ?: emptyMap()

        val jsUserPropertiesToSetOnce = call.getObject("userPropertiesToSetOnce")
        val userPropertiesToSetOnce = jsUserPropertiesToSetOnce?.let { jsObject ->
            val map = mutableMapOf<String, Any>()
            jsObject.keys().forEach { key ->
                if(jsObject.opt(key) != null) map[key] = jsObject.opt(key) ?: ""
            }
            map.toMap()
        } ?: emptyMap()


        if (new_distinct_id != null) {
            PostHog.identify(new_distinct_id, userPropertiesToSet, userPropertiesToSetOnce)
        }
        call.resolve()
    }

    @PluginMethod
    fun group(call: PluginCall) {
        val type = call.getString("type")
        val key = call.getString("key")
        // Assuming call.getObject("properties") returns a JSObject or null
        val jsProperties = call.getObject("properties")
        // Initialize an empty map which will be filled with the properties from jsProperties
        val properties = jsProperties?.let { jsObject ->
            val map = mutableMapOf<String, Any>()
            jsObject.keys().forEach { key ->
                if(jsObject.opt(key) != null) map[key] = jsObject.opt(key) ?: ""
            }
            map.toMap()
        } ?: emptyMap()

        if (type != null && key != null) {
            PostHog.group(type, key, properties)
        }
        call.resolve()
    }

    @PluginMethod
    fun reset(call: PluginCall) {
        PostHog.reset()
        call.resolve()
    }
}
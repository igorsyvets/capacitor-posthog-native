import Foundation
import Capacitor
import PostHog

/**
 * Bridges the PostHog iOS SDK into Capacitor.
 *
 * The SDK itself must be configured by the host app before any of these
 * methods are called — see `PostHogSDK.shared.setup(_:)`.
 */
@objc(PostHogPlugin)
public class PostHogPlugin: CAPPlugin {

    @objc func capture(_ call: CAPPluginCall) {
        guard let eventName = call.getString("event_name") else {
            call.reject("event_name was not provided")
            return
        }
        PostHogSDK.shared.capture(eventName, properties: call.getObject("properties"))
        call.resolve()
    }

    @objc func screen(_ call: CAPPluginCall) {
        guard let screenTitle = call.getString("screenTitle") else {
            call.reject("screenTitle was not provided")
            return
        }
        PostHogSDK.shared.screen(screenTitle, properties: call.getObject("properties"))
        call.resolve()
    }

    @objc func identify(_ call: CAPPluginCall) {
        guard let distinctId = call.getString("new_distinct_id") else {
            call.reject("new_distinct_id was not provided")
            return
        }
        PostHogSDK.shared.identify(
            distinctId,
            userProperties: call.getObject("userPropertiesToSet"),
            userPropertiesSetOnce: call.getObject("userPropertiesToSetOnce")
        )
        call.resolve()
    }

    @objc func group(_ call: CAPPluginCall) {
        guard let type = call.getString("type") else {
            call.reject("type was not provided")
            return
        }
        guard let key = call.getString("key") else {
            call.reject("key was not provided")
            return
        }
        PostHogSDK.shared.group(type: type, key: key, groupProperties: call.getObject("properties"))
        call.resolve()
    }

    @objc func reset(_ call: CAPPluginCall) {
        PostHogSDK.shared.reset()
        call.resolve()
    }
}

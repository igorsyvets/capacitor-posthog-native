import Foundation
import Capacitor
import PostHog

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */

@objc(PostHogPlugin)
public class PostHogPlugin: CAPPlugin {
    
       
    @objc func capture(_ call: CAPPluginCall) {
            
        if let event_name = call.getString("event_name") {
            let properties = call.getObject("properties")
            PostHogSDK.shared.capture(event_name, properties: properties)
        } else {
            print("PostHogPlugin.capture Error: event_name was not provided")
        }
    }

    @objc func screen(_ call: CAPPluginCall) {
        if let screenTitle = call.getString("screenTitle") {
            let properties = call.getObject("properties")
            PostHogSDK.shared.capture(screenTitle, properties: properties)
        } 
        else {
            print("PostHogPlugin.screen Error: screenTitle was not provided")
        }
    }
    
    @objc func identify(_ call: CAPPluginCall) {
      
        if let userID = call.getString("new_distinct_id") {
            let userPropertiesToSet = call.getObject("userPropertiesToSet")            
            PostHogSDK.shared.identify(userID, userProperties: userPropertiesToSet)
        }   
        else {
            print("PostHogPlugin.identify Error: new_distinct_id was not provided")
        }
    }
    
    @objc func group(_ call: CAPPluginCall) {
        
        if let type = call.getString("type") {
            if let key = call.getString("key") {
                let properties = call.getObject("properties")                
                PostHogSDK.shared.group(type: type, key: key, groupProperties: properties) }
            else {
                print("PostHogPlugin.group Error: key was not provided")
            }            
        }
        else {
            print("PostHogPlugin.group Error: type was not provided")
        }
    }
        

     @objc func reset(_ call: CAPPluginCall) {        
        PostHogSDK.shared.reset()
    }

}

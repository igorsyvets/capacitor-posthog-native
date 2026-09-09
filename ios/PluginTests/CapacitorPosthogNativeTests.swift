import XCTest
import Capacitor
@testable import Plugin

/// The plugin is a thin passthrough to the PostHog SDK, so these tests cover the
/// bridge itself: that every method declared to JavaScript in `PostHogPlugin.m`
/// is actually implemented and reachable through the Objective-C runtime.
class CapacitorPosthogNativeTests: XCTestCase {

    private let bridgedSelectors = [
        "capture:",
        "screen:",
        "identify:",
        "group:",
        "reset:"
    ]

    func testPluginExposesEveryBridgedMethod() {
        let plugin = PostHogPlugin()
        for name in bridgedSelectors {
            XCTAssertTrue(
                plugin.responds(to: Selector(name)),
                "PostHogPlugin does not implement \(name), but PostHogPlugin.m declares it to JavaScript"
            )
        }
    }

    func testPluginIsRegisteredUnderExpectedName() {
        XCTAssertEqual(PostHogPlugin().pluginName(), "PostHog")
    }
}

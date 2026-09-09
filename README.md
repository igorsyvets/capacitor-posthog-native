# capacitor-posthog-native

A Capacitor plugin that wraps the **native** PostHog SDKs on iOS and Android, exposing a single JavaScript API for the analytics calls an app actually makes.

> Built in early 2024 for Rewod, my Capacitor app that needed native PostHog analytics on iOS and Android. It covers the five calls that app used — `capture`, `screen`, `identify`, `group`, `reset` — not the full PostHog surface. Targets Capacitor 5. **Not actively maintained** — published as a code sample, and still used as-is by the app it was built for.
>
> If you need a maintained Capacitor PostHog plugin today, use [`@capawesome/capacitor-posthog`](https://github.com/capawesome-team/capacitor-plugins) — it tracks current Capacitor, covers web, and implements far more of the PostHog API.

## Why it exists

When this was written, PostHog had no official Capacitor plugin. The usual workaround is dropping `posthog-js` into the webview, but that treats a native app like a web page: you lose the native app lifecycle, the device and app-version context the mobile SDKs attach automatically, and the native session handling.

This plugin takes the other path — run the real `posthog-android` and `posthog-ios` SDKs, and bridge only the calls the app needs across to JavaScript.

## API

```typescript
import { PostHog } from 'capacitor-posthog-native';

PostHog.capture({ event_name: 'order_placed', properties: { total: 42 } });
PostHog.screen({ screenTitle: 'Checkout' });
PostHog.identify({ new_distinct_id: 'user_123', userPropertiesToSet: { plan: 'pro' } });
PostHog.group({ type: 'company', key: 'acme', properties: { name: 'Acme Inc' } });
PostHog.reset();
```

| Method | Purpose |
| --- | --- |
| `capture` | Record an event with optional properties |
| `screen` | Record a screen view |
| `identify` | Associate the session with a distinct ID and set user properties |
| `group` | Associate the session with a group (company, team, …) |
| `reset` | Clear the stored identity, e.g. on logout |

## Platform support

| | Status |
| --- | --- |
| **Android** | Native, via `com.posthog:posthog-android` |
| **iOS** | Native, via the `PostHog` pod |
| **Web** | Not implemented — methods are no-ops |

## Setup

### Android

Configuration is read on plugin load from `android/app/src/main/assets/posthog.config.json`:

```json
{
  "apiKey": "phc_your_project_api_key",
  "host": "https://eu.i.posthog.com"
}
```

If the file is missing or incomplete the plugin logs an error and stays inactive — it will not crash the app.

### iOS

The plugin does **not** configure the SDK. Set it up in `AppDelegate.swift` before the webview loads:

```swift
import PostHog

let config = PostHogConfig(apiKey: "phc_your_project_api_key", host: "https://eu.i.posthog.com")
config.captureScreenViews = false
PostHogSDK.shared.setup(config)
```

## How it works

Three pieces, one per layer:

- **TypeScript** — [`src/index.ts`](src/index.ts) calls `registerPlugin('PostHog')`, which resolves to the native implementation on device and to the web stub in the browser. [`src/definitions.ts`](src/definitions.ts) is the shared contract.
- **Android** — [`PostHogPlugin.kt`](android/src/main/kotlin/com/igorsyvets/capacitor/posthog/PostHogPlugin.kt) is annotated `@CapacitorPlugin(name = "PostHog")`; each `@PluginMethod` unwraps the incoming `PluginCall`, flattens its `JSObject` properties into the `Map<String, Any>` the SDK expects, and resolves. `PostHogAndroid.setup` runs in `load()`.
- **iOS** — [`PostHogPlugin.m`](ios/Plugin/PostHogPlugin.m) declares the bridged methods with the `CAP_PLUGIN` macro, and [`PostHogPlugin.swift`](ios/Plugin/PostHogPlugin.swift) implements them against `PostHogSDK.shared`.

## What I'd do differently

Worth naming, since the code shows it:

- **Split configuration paths.** Android self-configures from a JSON asset; iOS expects the host app to call `setup` itself. That asymmetry is the plugin's worst design flaw — a single `setup()` method exposed to JavaScript, or config read from `capacitor.config.ts` on both platforms, would be far better.
- **Consistent parameter casing.** The API mixes `event_name` and `new_distinct_id` with `screenTitle` and `userPropertiesToSet`, because the former mirror PostHog's own naming and the latter don't. One convention, chosen once.
- **Honest web behaviour.** The web implementation silently discards events. Throwing `unimplemented()` — or delegating to `posthog-js` — beats pretending to work.
- **Trimmed type surface.** `definitions.ts` still exports `CaptureOptions`/`XHROptions` copied from `posthog-js` that no native implementation reads.

## Development

```bash
npm install
npm run build        # tsc + rollup
npm run verify       # builds iOS, Android and web
```

Android requires JDK 17 (the Gradle wrapper here predates JDK 21 support).

## License

MIT — see [LICENSE](LICENSE).

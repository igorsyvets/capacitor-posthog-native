export interface PostHogPlugin {
  capture(options: {
    event_name: string;
    properties?: Properties | null;
    options?: CaptureOptions;
  }): void;

  identify(options: {
    new_distinct_id?: string;
    userPropertiesToSet?: Properties;
    userPropertiesToSetOnce?: Properties;
  }): void;

  group(options: {
    type: string;
    key: string;
    properties: {
      name: string;
      [key: string]: any;
    };
  }): void;

  screen(options: {
    screenTitle: string;
    properties?: Properties | null;
  }): void;

  reset(): void;
}

type Property = any;
type Properties = Record<string, Property>;

interface XHROptions {
  transport?: 'XHR' | 'fetch' | 'sendBeacon';
  method?: 'POST' | 'GET';
  urlQueryArgs?: {
    compression: Compression;
  };
  verbose?: boolean;
  blob?: boolean;
  sendBeacon?: boolean;
}
interface CaptureOptions extends XHROptions {
  $set?: Properties /** used with $identify */;
  $set_once?: Properties /** used with $identify */;
  _batchKey?: string /** key of queue, e.g. 'sessionRecording' vs 'event' */;
  _metrics?: Properties;
  _noTruncate?: boolean /** if set, overrides and disables config.properties_string_max_length */;
  endpoint?: string /** defaults to '/e/' */;
  send_instantly?: boolean /** if set skips the batched queue */;
  timestamp?: Date;
}

declare enum Compression {
  GZipJS = 'gzip-js',
  Base64 = 'base64',
}

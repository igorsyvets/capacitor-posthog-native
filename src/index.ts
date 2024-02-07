import { registerPlugin } from '@capacitor/core';

import type { PostHogPlugin } from './definitions';

const PostHog = registerPlugin<PostHogPlugin>('PostHog', {
  web: () => import('./web').then(m => new m.PostHogWeb()),
});

export * from './definitions';
export { PostHog };

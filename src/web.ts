import { WebPlugin } from '@capacitor/core';

import type { PostHogPlugin } from './definitions';

export class PostHogWeb extends WebPlugin implements PostHogPlugin {
  async capture() {}
  async identify() {}
  async group() {}
  async screen() {}
  async reset() {}
}

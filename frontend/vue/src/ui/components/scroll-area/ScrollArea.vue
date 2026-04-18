<script setup lang="ts">
import {
  ScrollAreaCorner,
  ScrollAreaRoot,
  ScrollAreaScrollbar,
  ScrollAreaThumb,
  ScrollAreaViewport,
} from 'reka-ui'
import { cn } from '@/ui/lib/utils'

const props = withDefaults(
  defineProps<{
    class?: string
    showHorizontalScrollbar?: boolean
    showVerticalScrollbar?: boolean
  }>(),
  {
    showHorizontalScrollbar: true,
    showVerticalScrollbar: true,
  },
)
</script>

<template>
  <ScrollAreaRoot data-slot="scroll-area" :class="cn('relative', props.class)">
    <ScrollAreaViewport
      data-slot="scroll-area-viewport"
      class="focus-visible:ring-ring/50 size-full rounded-[inherit] transition-[color,box-shadow] outline-none focus-visible:ring-[3px] focus-visible:outline-1"
    >
      <slot />
    </ScrollAreaViewport>
    <ScrollAreaScrollbar
      v-if="props.showVerticalScrollbar"
      data-slot="scroll-area-scrollbar"
      orientation="vertical"
      class="flex touch-none p-px transition-colors select-none h-full w-2.5 border-l border-l-transparent"
    >
      <ScrollAreaThumb
        data-slot="scroll-area-thumb"
        class="relative flex-1 rounded-full bg-neutral-400/50 transition-colors hover:bg-neutral-500/65 dark:bg-neutral-500/45 dark:hover:bg-neutral-400/55"
      />
    </ScrollAreaScrollbar>
    <ScrollAreaScrollbar
      v-if="props.showHorizontalScrollbar"
      data-slot="scroll-area-scrollbar"
      orientation="horizontal"
      class="flex touch-none p-px transition-colors select-none h-2.5 flex-col border-t border-t-transparent"
    >
      <ScrollAreaThumb
        data-slot="scroll-area-thumb"
        class="relative flex-1 rounded-full bg-neutral-400/50 transition-colors hover:bg-neutral-500/65 dark:bg-neutral-500/45 dark:hover:bg-neutral-400/55"
      />
    </ScrollAreaScrollbar>
    <ScrollAreaCorner />
  </ScrollAreaRoot>
</template>

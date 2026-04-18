<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: HTMLAttributes['class']
  /** When true, marks current page for a11y */
  current?: boolean
}>()
</script>

<template>
  <span
    :aria-current="current ? 'page' : undefined"
    :class="cn('font-normal text-foreground', $props.class)"
  >
    <slot />
  </span>
</template>

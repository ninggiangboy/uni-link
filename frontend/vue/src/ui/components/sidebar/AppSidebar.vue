<script setup lang="ts">
/**
 * Minimal app sidebar shell — expand with full layout (header, groups, mobile sheet) as needed.
 */
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <aside
    data-slot="sidebar"
    :class="
      cn('flex h-full w-64 flex-col border-r bg-sidebar text-sidebar-foreground', $props.class)
    "
  >
    <slot />
  </aside>
</template>

<script setup lang="ts">
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <div
    role="list"
    data-slot="grid-list"
    :class="
      cn(
        'flex flex-col gap-2 overflow-auto rounded-md border bg-popover p-1 text-popover-foreground shadow-md outline-none max-h-80',
        $props.class,
      )
    "
  >
    <slot />
  </div>
</template>

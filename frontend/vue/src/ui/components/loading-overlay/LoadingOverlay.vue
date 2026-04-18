<script setup lang="ts">
import { Spinner } from '@/ui/components/spinner'
import { cn } from '@/ui/lib/utils'

withDefaults(
  defineProps<{
    class?: string
    /** When true, dims the slotted content and centers a spinner on top */
    isLoading?: boolean
  }>(),
  { isLoading: false },
)
</script>

<template>
  <div :class="cn('relative', isLoading && 'opacity-60', $props.class)">
    <slot />
    <div v-if="isLoading" class="absolute inset-0 z-10 flex items-center justify-center">
      <Spinner class="size-7 text-primary" />
    </div>
  </div>
</template>

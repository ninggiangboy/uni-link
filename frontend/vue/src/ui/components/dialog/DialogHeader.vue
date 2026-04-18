<script setup lang="ts">
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <div :class="cn('flex flex-col gap-1.5', $props.class)">
    <slot />
  </div>
</template>

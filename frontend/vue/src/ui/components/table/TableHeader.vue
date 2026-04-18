<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: HTMLAttributes['class']
}>()
</script>

<template>
  <thead data-slot="table-header" :class="cn('sticky top-0 z-[2]', $props.class)">
    <slot />
  </thead>
</template>

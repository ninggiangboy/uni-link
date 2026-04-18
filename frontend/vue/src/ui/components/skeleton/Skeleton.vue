<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: HTMLAttributes['class']
}>()
</script>

<template>
  <div data-slot="skeleton" :class="cn('bg-neutral-500/20 animate-pulse rounded', $props.class)" />
</template>

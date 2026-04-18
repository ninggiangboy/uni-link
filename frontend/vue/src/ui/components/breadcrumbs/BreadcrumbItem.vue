<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: HTMLAttributes['class']
}>()
</script>

<template>
  <li :class="cn('inline-flex items-center gap-1.5 sm:gap-2.5', $props.class)">
    <slot />
  </li>
</template>

<script setup lang="ts">
import type { AnchorHTMLAttributes } from 'vue'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: AnchorHTMLAttributes['class']
  href?: string
}>()
</script>

<template>
  <a :href="href" :class="cn('transition-colors hover:text-foreground', $props.class)">
    <slot />
  </a>
</template>

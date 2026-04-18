<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { cn } from '@/ui/lib/utils'
import { badgeVariants, type BadgeVariants } from './badge-variants'

const props = defineProps<{
  class?: HTMLAttributes['class']
  variant?: BadgeVariants['variant']
}>()
</script>

<template>
  <span data-slot="badge" :class="cn(badgeVariants({ variant: props.variant }), props.class)">
    <slot />
  </span>
</template>

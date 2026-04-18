<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: HTMLAttributes['class']
}>()
</script>

<template>
  <tfoot
    data-slot="table-footer"
    :class="cn('bg-neutral-400/10 border-t font-medium sticky bottom-0', $props.class)"
  >
    <slot />
  </tfoot>
</template>

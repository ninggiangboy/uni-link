<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: HTMLAttributes['class']
}>()
</script>

<template>
  <tbody
    data-slot="table-body"
    :class="
      cn(
        '[&_tr:nth-child(odd)]:bg-background-secondary [&_tr:nth-child(even)]:bg-background-tertiary',
        $props.class,
      )
    "
  >
    <slot />
  </tbody>
</template>

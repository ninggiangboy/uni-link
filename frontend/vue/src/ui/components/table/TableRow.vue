<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: HTMLAttributes['class']
}>()
</script>

<template>
  <tr
    data-slot="table-row"
    :class="
      cn('data-[state=selected]:bg-blue-200! dark:data-[state=selected]:bg-blue-900!', $props.class)
    "
  >
    <slot />
  </tr>
</template>

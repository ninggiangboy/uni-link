<script setup lang="ts">
import { DialogOverlay as RekaDialogOverlay } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <RekaDialogOverlay
    v-bind="$attrs"
    :class="
      cn(
        'fixed inset-0 z-50 bg-black/70',
        'data-[state=open]:animate-in max-md:data-[state=open]:duration-300 data-[state=open]:fade-in-0',
        'data-[state=closed]:animate-out max-md:data-[state=closed]:duration-300 data-[state=closed]:fade-out-0',
        $props.class,
      )
    "
  />
</template>

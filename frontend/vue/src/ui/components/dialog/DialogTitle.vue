<script setup lang="ts">
import { DialogTitle as RekaDialogTitle } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <RekaDialogTitle :class="cn('text-lg font-semibold', $props.class)">
    <slot />
  </RekaDialogTitle>
</template>

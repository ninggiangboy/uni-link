<script setup lang="ts">
import { DialogDescription as RekaDialogDescription } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <RekaDialogDescription :class="cn('text-sm text-muted-foreground', $props.class)">
    <slot />
  </RekaDialogDescription>
</template>

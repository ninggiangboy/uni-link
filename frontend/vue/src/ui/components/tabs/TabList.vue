<script setup lang="ts">
import { TabsList as RekaTabsList } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <RekaTabsList
    :class="
      cn(
        'inline-flex h-10 items-center justify-center rounded-md bg-muted p-1 text-muted-foreground',
        'data-[orientation=vertical]:h-auto data-[orientation=vertical]:flex-col',
        $props.class,
      )
    "
  >
    <slot />
  </RekaTabsList>
</template>

<script setup lang="ts">
import { TabsTrigger as RekaTabsTrigger } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
  value: string | number
}>()
</script>

<template>
  <RekaTabsTrigger
    :value="value"
    :class="
      cn(
        'inline-flex cursor-pointer justify-center whitespace-nowrap rounded-sm px-3 py-1.5 text-sm font-medium outline-none ring-offset-background transition-all',
        'focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2',
        'disabled:pointer-events-none disabled:opacity-50',
        'data-[state=active]:bg-background data-[state=active]:text-foreground data-[state=active]:shadow-sm',
        'group-data-[orientation=vertical]/tabs-root:w-full',
        $props.class,
      )
    "
  >
    <slot />
  </RekaTabsTrigger>
</template>

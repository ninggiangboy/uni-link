<script setup lang="ts">
import { TooltipContent as RekaTooltipContent, TooltipPortal } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

withDefaults(
  defineProps<{
    class?: string
    sideOffset?: number
  }>(),
  { sideOffset: 4 },
)
</script>

<template>
  <TooltipPortal>
    <RekaTooltipContent
      v-bind="$attrs"
      :side-offset="sideOffset"
      :class="
        cn(
          'max-w-xs z-50 text-xs overflow-hidden rounded-sm bg-neutral-800 px-2.5 py-1.5 text-neutral-200 animate-in fade-in-0',
          'data-[state=closed]:animate-out data-[state=closed]:fade-out-0',
          $props.class,
        )
      "
    >
      <slot />
    </RekaTooltipContent>
  </TooltipPortal>
</template>

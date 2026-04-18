<script setup lang="ts">
import { PopoverContent as RekaPopoverContent, PopoverPortal } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

withDefaults(
  defineProps<{
    class?: string
    /** side offset in px for floating-ui */
    sideOffset?: number
  }>(),
  { sideOffset: 4 },
)
</script>

<template>
  <PopoverPortal>
    <RekaPopoverContent
      v-bind="$attrs"
      :side-offset="sideOffset"
      :class="
        cn(
          'z-50 rounded-lg border bg-popover/80 backdrop-blur-xl text-popover-foreground shadow-popover p-3 outline-none',
          'data-[state=open]:animate-in data-[state=open]:fade-in-0',
          'data-[state=closed]:animate-out data-[state=closed]:fade-out-0',
          'data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2',
          $props.class,
        )
      "
    >
      <slot />
    </RekaPopoverContent>
  </PopoverPortal>
</template>

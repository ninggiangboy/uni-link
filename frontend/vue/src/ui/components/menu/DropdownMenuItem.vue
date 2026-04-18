<script setup lang="ts">
import { DropdownMenuItem as Reka } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <Reka
    :class="
      cn(
        'relative flex cursor-default select-none items-center gap-2.5 rounded-sm px-2 py-1.5 text-sm outline-none',
        'data-[disabled]:pointer-events-none data-[disabled]:opacity-50',
        'data-[highlighted]:bg-primary data-[highlighted]:text-white',
        $props.class,
      )
    "
    v-bind="$attrs"
  >
    <slot />
  </Reka>
</template>

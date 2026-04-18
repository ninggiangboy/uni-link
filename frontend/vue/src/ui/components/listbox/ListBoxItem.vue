<script setup lang="ts">
import { ListboxItem as RekaListboxItem, ListboxItemIndicator } from 'reka-ui'
import { Check } from 'lucide-vue-next'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
  value: string
}>()
</script>

<template>
  <RekaListboxItem
    :value="value"
    v-bind="$attrs"
    :class="
      cn(
        'relative flex w-full cursor-default select-none items-center rounded-sm px-2 py-1.5 text-sm outline-none pl-8',
        'data-[disabled]:pointer-events-none data-[disabled]:opacity-50',
        'data-[highlighted]:bg-accent data-[highlighted]:text-accent-foreground',
        'data-[state=checked]:bg-accent/50',
        $props.class,
      )
    "
  >
    <span class="absolute left-2 flex size-4 items-center justify-center">
      <ListboxItemIndicator>
        <Check class="size-4" />
      </ListboxItemIndicator>
    </span>
    <slot />
  </RekaListboxItem>
</template>

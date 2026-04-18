<script setup lang="ts">
import type { AcceptableValue } from 'reka-ui'
import { ListboxRoot } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()

const model = defineModel<AcceptableValue | AcceptableValue[] | undefined>()
</script>

<template>
  <ListboxRoot
    v-model="model"
    v-bind="$attrs"
    :class="
      cn(
        'group overflow-auto rounded-md border bg-popover p-1 text-popover-foreground shadow-md outline-none max-h-60',
        'data-[empty]:p-6 data-[empty]:text-center data-[empty]:text-sm',
        $props.class,
      )
    "
  >
    <slot />
  </ListboxRoot>
</template>

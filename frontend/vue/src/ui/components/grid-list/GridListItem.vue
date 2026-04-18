<script setup lang="ts">
import { Checkbox } from '@/ui/components/checkbox'
import { cn } from '@/ui/lib/utils'

const props = defineProps<{
  class?: string
  showCheckbox?: boolean
}>()

const selected = defineModel<boolean>('selected', { default: false })
</script>

<template>
  <div
    role="listitem"
    data-slot="grid-list-item"
    :class="
      cn(
        'relative flex w-full select-none items-center gap-3 rounded-sm px-2 py-1.5 text-sm outline-none',
        'hover:bg-accent hover:text-accent-foreground',
        'focus-visible:z-10 focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2',
        selected && 'bg-accent/80',
        props.class,
      )
    "
    tabindex="0"
    @keydown.enter.prevent="selected = !selected"
  >
    <Checkbox v-if="showCheckbox" v-model="selected" class="pointer-events-none" @click.stop />
    <slot />
  </div>
</template>

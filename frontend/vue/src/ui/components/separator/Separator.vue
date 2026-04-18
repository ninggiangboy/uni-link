<script setup lang="ts">
import { Separator as RekaSeparator } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

const props = withDefaults(
  defineProps<{
    orientation?: 'horizontal' | 'vertical'
    decorative?: boolean
    class?: string
  }>(),
  { orientation: 'horizontal' },
)
</script>

<template>
  <RekaSeparator
    data-slot="separator"
    :orientation="props.orientation"
    :decorative="props.decorative"
    :class="
      cn(
        'bg-border shrink-0',
        props.orientation === 'horizontal' ? 'h-px w-full' : 'w-px h-full',
        props.class,
      )
    "
    v-bind="$attrs"
  />
</template>

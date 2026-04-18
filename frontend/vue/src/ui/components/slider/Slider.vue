<script setup lang="ts">
import { SliderRange, SliderRoot, SliderThumb, SliderTrack } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

defineProps<{
  class?: string
  min?: number
  max?: number
  step?: number
}>()

const model = defineModel<number[]>({ default: () => [50] })
</script>

<template>
  <SliderRoot
    v-model="model"
    v-bind="$attrs"
    :min="min ?? 0"
    :max="max ?? 100"
    :step="step ?? 1"
    :class="cn('relative flex w-full touch-none select-none items-center', $props.class)"
  >
    <SliderTrack class="relative h-1.5 w-full grow overflow-hidden rounded-full bg-neutral-400/30">
      <SliderRange class="absolute h-full bg-primary" />
    </SliderTrack>
    <SliderThumb
      class="block size-4 rounded-full border border-primary bg-background shadow transition-colors focus-visible:ring-2 focus-visible:ring-primary focus-visible:outline-none disabled:pointer-events-none disabled:opacity-50"
    />
  </SliderRoot>
</template>

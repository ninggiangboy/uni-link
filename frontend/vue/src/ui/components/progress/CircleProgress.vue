<script setup lang="ts">
import { ProgressIndicator, ProgressRoot } from 'reka-ui'
import { computed } from 'vue'
import { cn } from '@/ui/lib/utils'

const props = withDefaults(
  defineProps<{
    class?: string
    iconClassName?: string
    max?: number
  }>(),
  { max: 100 },
)

const model = defineModel<number | null>({ default: 0 })

const center = 16
const strokeWidth = 4
const r = 16 - strokeWidth
const c = 2 * r * Math.PI

const pct = computed(() => {
  if (model.value == null) return 0
  return Math.min(100, Math.max(0, (model.value / props.max) * 100))
})

const dashOffset = computed(() => c - (pct.value / 100) * c)
</script>

<template>
  <ProgressRoot v-model="model" :max="props.max" :class="cn('w-fit', props.class)">
    <ProgressIndicator as-child>
      <svg
        width="64"
        height="64"
        viewBox="0 0 32 32"
        fill="none"
        :stroke-width="strokeWidth"
        :class="cn('size-5', props.iconClassName)"
      >
        <circle :cx="center" :cy="center" :r="r" stroke-width="4" class="stroke-neutral-500/30" />
        <circle
          :cx="center"
          :cy="center"
          :r="r"
          class="stroke-primary-foreground"
          stroke-width="4"
          :stroke-dasharray="`${c} ${c}`"
          :stroke-dashoffset="dashOffset"
          transform="rotate(-90 16 16)"
          style="transition: stroke-dashoffset 0.3s ease-in-out"
        />
      </svg>
    </ProgressIndicator>
  </ProgressRoot>
</template>

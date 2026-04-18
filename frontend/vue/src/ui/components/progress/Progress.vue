<script setup lang="ts">
import { ProgressIndicator, ProgressRoot } from 'reka-ui'
import { computed } from 'vue'
import { cn } from '@/ui/lib/utils'

const props = withDefaults(
  defineProps<{
    class?: string
    barClassName?: string
    fillClassName?: string
    max?: number
  }>(),
  { max: 100 },
)

const model = defineModel<number | null>({ default: 0 })

const pct = computed(() => {
  if (model.value == null) return 0
  return Math.min(100, Math.max(0, (model.value / props.max) * 100))
})
</script>

<template>
  <ProgressRoot v-model="model" :max="max" :class="cn('w-full', props.class)">
    <div
      :class="
        cn('relative h-4 w-full overflow-hidden rounded-full bg-neutral-500/15', props.barClassName)
      "
    >
      <ProgressIndicator as-child>
        <div
          :class="cn('size-full flex-1 bg-primary-foreground transition-all', props.fillClassName)"
          :style="{ transform: `translateX(-${100 - pct}%)` }"
        />
      </ProgressIndicator>
    </div>
  </ProgressRoot>
</template>

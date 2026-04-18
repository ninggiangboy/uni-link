<script setup lang="ts">
import { Label } from '@/ui/components/field'
import { labelVariants } from '@/ui/components/field/field-variants'
import Progress from './Progress.vue'
import { cn } from '@/ui/lib/utils'

const props = withDefaults(
  defineProps<{
    class?: string
    label?: string
    showValue?: boolean
    max?: number
  }>(),
  { showValue: true, max: 100 },
)

const model = defineModel<number | null>({ default: 0 })
</script>

<template>
  <div :class="cn('group flex w-full flex-col gap-2', props.class)">
    <div class="flex w-full justify-between">
      <Label v-if="label">{{ label }}</Label>
      <span v-if="showValue" :class="labelVariants()">
        {{ Math.round(((model ?? 0) / (max ?? 100)) * 100) }}%
      </span>
    </div>
    <Progress v-model="model" :max="max" />
  </div>
</template>

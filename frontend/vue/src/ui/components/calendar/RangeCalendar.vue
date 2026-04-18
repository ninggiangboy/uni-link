<script setup lang="ts">
import { computed, ref } from 'vue'
import type { DateValue } from '@internationalized/date'
import type { DateRange } from 'reka-ui'
import { RangeCalendarRoot } from 'reka-ui'
import RangeCalendarPanelGrids from './RangeCalendarPanelGrids.vue'
import { cn } from '@/ui/lib/utils'

const props = withDefaults(
  defineProps<{
    minValue?: DateValue
    maxValue?: DateValue
    variant?: 'default' | 'unstyled'
    defaultValue?: DateRange | null
    captionLayout?: 'buttons' | 'dropdown'
  }>(),
  { variant: 'default', captionLayout: 'buttons' },
)

const model = defineModel<DateRange | null>({ default: null })
const placeholder = ref<DateValue>()
function onPlaceholderChange(next: DateValue) {
  placeholder.value = next
}

const isUnstyled = computed(() => props.variant === 'unstyled')
</script>

<template>
  <RangeCalendarRoot
    v-slot="{ date, grid, weekDays, locale }"
    v-model="model"
    v-model:placeholder="placeholder"
    :default-value="defaultValue ?? undefined"
    :min-value="minValue"
    :max-value="maxValue"
    :week-starts-on="1"
    fixed-weeks
    :class="
      cn(
        'w-fit',
        !isUnstyled && 'rounded-lg border border-border bg-background-secondary/40 p-1',
      )
    "
  >
    <RangeCalendarPanelGrids
      :grid="grid"
      :week-days="weekDays"
      :unstyled="isUnstyled"
      :placeholder="placeholder ?? date"
      :on-placeholder-change="onPlaceholderChange"
      :locale="locale"
      :caption-layout="props.captionLayout"
      :min-value="minValue"
      :max-value="maxValue"
    >
      <template #day-cell="slotProps">
        <slot name="day-cell" v-bind="slotProps">
          {{ slotProps.dayValue }}
        </slot>
      </template>
    </RangeCalendarPanelGrids>
  </RangeCalendarRoot>
</template>

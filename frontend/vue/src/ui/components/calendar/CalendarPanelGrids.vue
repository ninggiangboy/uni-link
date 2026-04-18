<script setup lang="ts">
import { computed } from 'vue'
import type { DateValue } from '@internationalized/date'
import {
  CalendarCell,
  CalendarCellTrigger,
  CalendarGrid,
  CalendarGridBody,
  CalendarGridHead,
  CalendarGridRow,
  CalendarHeadCell,
  CalendarHeader,
  CalendarHeading,
  CalendarNext,
  CalendarPrev,
} from 'reka-ui'
import { buttonVariants } from '@/ui/components/button/button-variants'
import { cn } from '@/ui/lib/utils'
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'

const props = withDefaults(
  defineProps<{
    grid: { value: DateValue; rows: DateValue[][] }[]
    weekDays: string[]
    unstyled?: boolean
    placeholder?: DateValue
    onPlaceholderChange?: (date: DateValue) => void
    locale?: string
    captionLayout?: 'buttons' | 'dropdown'
    minValue?: DateValue
    maxValue?: DateValue
  }>(),
  { unstyled: false, captionLayout: 'buttons' },
)

const isUnstyled = computed(() => props.unstyled)
const isDropdownCaption = computed(
  () => props.captionLayout === 'dropdown' && !!props.placeholder && !!props.locale,
)
const weekColIndexes = [0, 1, 2, 3, 4, 5, 6] as const

const monthOptions = computed(() => {
  if (!props.placeholder || !props.locale) return []
  const monthsInYear = props.placeholder.calendar.getMonthsInYear(props.placeholder)
  return Array.from({ length: monthsInYear }, (_, i) => {
    const month = i + 1
    const d = props.placeholder!.set({ day: 1, month })
    const label = d.toDate('UTC').toLocaleString(props.locale, { month: 'short' })
    return { value: month, label }
  })
})

const yearOptions = computed(() => {
  if (!props.placeholder) return []
  const currentYear = props.placeholder.year
  const minYear = props.minValue?.year ?? currentYear - 100
  const maxYear = props.maxValue?.year ?? currentYear + 100
  return Array.from({ length: maxYear - minYear + 1 }, (_, i) => minYear + i)
})

function onMonthChange(event: Event) {
  if (!props.placeholder) return
  const selectEl = event.target as HTMLSelectElement
  const value = Number(selectEl.value)
  if (!Number.isNaN(value)) {
    props.onPlaceholderChange?.(props.placeholder.set({ day: 1, month: value }))
    requestAnimationFrame(() => selectEl.blur())
  }
}

function onYearChange(event: Event) {
  if (!props.placeholder) return
  const selectEl = event.target as HTMLSelectElement
  const value = Number(selectEl.value)
  if (!Number.isNaN(value)) {
    props.onPlaceholderChange?.(props.placeholder.set({ day: 1, year: value }))
    requestAnimationFrame(() => selectEl.blur())
  }
}

const navBtnClass = computed(() =>
  cn(
    buttonVariants({ variant: 'ghost', size: 'icon' }),
    !isUnstyled.value &&
      'shrink-0 rounded-full hover:bg-muted-foreground/10',
  ),
)

const triggerClass = computed(() =>
  cn(
    buttonVariants({ variant: 'unstyled', size: 'icon' }),
    'flex size-8 items-center justify-center rounded-full p-0 text-sm font-normal transition-none',
    !isUnstyled.value && [
      'cursor-pointer outline-none',
      'data-[disabled]:cursor-default data-[disabled]:text-muted-foreground data-[disabled]:opacity-50',
      'data-[selected]:bg-primary data-[selected]:text-white',
      'data-[focused]:data-[selected]:bg-primary',
      '[&[data-today]:not([data-selected])]:bg-neutral-400/10 [&[data-today]:not([data-selected])]:text-accent-foreground',
      'hover:bg-neutral-400/20 data-[selected]:hover:bg-primary',
      'data-[unavailable]:cursor-default data-[unavailable]:text-destructive-foreground',
      'data-[outside-view]:hidden',
    ],
  ),
)
</script>

<template>
  <CalendarHeader class="grid w-full grid-cols-[auto_1fr_auto] items-center gap-0.5 pb-1">
    <div class="flex shrink-0 items-center gap-0.5">
      <CalendarPrev :class="navBtnClass">
        <ChevronLeft class="size-4" aria-hidden="true" />
      </CalendarPrev>
    </div>
    <div class="flex items-center justify-center gap-1">
      <template v-if="isDropdownCaption">
        <select
          :value="placeholder?.month"
          class="h-8 px-2 text-sm"
          @change="onMonthChange"
        >
          <option v-for="month in monthOptions" :key="month.value" :value="month.value">
            {{ month.label }}
          </option>
        </select>
        <select
          :value="placeholder?.year"
          class="h-8 px-2 text-sm"
          @change="onYearChange"
        >
          <option v-for="year in yearOptions" :key="year" :value="year">
            {{ year }}
          </option>
        </select>
      </template>
      <CalendarHeading v-else class="text-center text-sm font-medium" />
    </div>
    <div class="flex shrink-0 items-center gap-0.5 justify-self-end">
      <CalendarNext :class="navBtnClass">
        <ChevronRight class="size-4" aria-hidden="true" />
      </CalendarNext>
    </div>
  </CalendarHeader>
  <div class="flex flex-col gap-4 sm:flex-row">
    <CalendarGrid
      v-for="month in grid"
      :key="month.value.toString()"
      :class="
        cn(
          'table-fixed border-separate border-spacing-x-0 border-spacing-y-0.5',
          isUnstyled && 'border-0 bg-transparent',
        )
      "
    >
      <colgroup>
        <col v-for="i in weekColIndexes" :key="i" class="w-8 min-w-8 max-w-8" />
      </colgroup>
      <CalendarGridHead>
        <CalendarGridRow>
          <CalendarHeadCell
            v-for="day in weekDays"
            :key="day"
            class="p-0 align-middle font-normal"
          >
            <span
              class="flex size-8 items-center justify-center rounded-md text-[0.8rem] text-muted-foreground"
            >
              {{ day }}
            </span>
          </CalendarHeadCell>
        </CalendarGridRow>
      </CalendarGridHead>
      <CalendarGridBody class="[&>tr>td]:p-0">
        <CalendarGridRow v-for="(weekDates, wi) in month.rows" :key="`w-${wi}`">
          <CalendarCell
            v-for="weekDate in weekDates"
            :key="weekDate.toString()"
            :date="weekDate"
            class="relative w-8 p-0 align-middle text-sm"
          >
            <div class="flex justify-center">
              <CalendarCellTrigger
                :day="weekDate"
                :month="month.value"
                :class="triggerClass"
              >
                <template #default="{ dayValue, disabled, unavailable, selected }">
                  <slot
                    name="day-cell"
                    :date="weekDate"
                    :month="month.value"
                    :day-value="dayValue"
                    :disabled="disabled"
                    :unavailable="unavailable"
                    :selected="selected"
                  >
                    {{ dayValue }}
                  </slot>
                </template>
              </CalendarCellTrigger>
            </div>
          </CalendarCell>
        </CalendarGridRow>
      </CalendarGridBody>
    </CalendarGrid>
  </div>
</template>

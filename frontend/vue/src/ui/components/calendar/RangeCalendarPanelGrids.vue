<script setup lang="ts">
import { computed } from 'vue'
import type { DateValue } from '@internationalized/date'
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'
import {
  RangeCalendarCell,
  RangeCalendarCellTrigger,
  RangeCalendarGrid,
  RangeCalendarGridBody,
  RangeCalendarGridHead,
  RangeCalendarGridRow,
  RangeCalendarHeadCell,
  RangeCalendarHeader,
  RangeCalendarHeading,
  RangeCalendarNext,
  RangeCalendarPrev,
} from 'reka-ui'
import { buttonVariants } from '@/ui/components/button/button-variants'
import { rangeCalendarCellLabelVariants } from './calendar-cell-styles'
import { cn } from '@/ui/lib/utils'

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
  const placeholder = props.placeholder
  const locale = props.locale
  if (!placeholder || !locale) return []
  const monthsInYear = placeholder.calendar.getMonthsInYear(placeholder)
  return Array.from({ length: monthsInYear }, (_, i) => {
    const month = i + 1
    const d = placeholder.set({ day: 1, month })
    const label = d.toDate('UTC').toLocaleString(locale, { month: 'short' })
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
      'shrink-0 rounded-full text-primary-foreground hover:bg-muted-foreground/10',
  ),
)

const triggerShellClass = computed(() =>
  cn(
    'group relative text-sm outline outline-0',
    !isUnstyled.value && 'cursor-pointer data-[outside-view]:hidden',
  ),
)

function cellVariant(flags: {
  selected: boolean
  selectionStart: boolean
  selectionEnd: boolean
  highlighted: boolean
  highlightedStart: boolean
  highlightedEnd: boolean
}): 'none' | 'middle' | 'cap' {
  if (flags.selectionStart || flags.selectionEnd) return 'cap'
  if (flags.highlightedStart || flags.highlightedEnd) return 'cap'
  if (flags.selected) return 'middle'
  if (flags.highlighted) return 'middle'
  return 'none'
}

function rangeBgState(
  selected: boolean,
  selectionStart: boolean,
  selectionEnd: boolean,
): 'none' | 'middle' | 'cap' {
  if (selected && (selectionStart || selectionEnd)) return 'cap'
  if (selected) return 'middle'
  return 'none'
}

function monthFadeFlags(date: DateValue, bg: 'none' | 'middle' | 'cap') {
  const isEndOfMonth = date.calendar.getDaysInMonth(date) === date.day
  const isStartOfMonth = date.day === 1
  return {
    fadeRight: bg === 'middle' && isEndOfMonth,
    fadeLeft: bg === 'middle' && isStartOfMonth,
  }
}

function rangeInnerTrackClass(
  date: DateValue,
  opts: {
    disabled: boolean
    selected: boolean
    selectionStart: boolean
    selectionEnd: boolean
  },
) {
  const { disabled, selected, selectionStart, selectionEnd } = opts
  const bg = rangeBgState(selected, selectionStart, selectionEnd)
  const { fadeLeft, fadeRight } = monthFadeFlags(date, bg)
  return cn(
    'flex size-8 items-center justify-center',
    !disabled && 'cursor-pointer',
    disabled && 'cursor-default',
    selected && 'bg-neutral-400/15',
    selectionStart && 'rounded-s-full',
    selectionEnd && 'rounded-e-full',
    fadeRight && 'bg-transparent bg-gradient-to-r from-neutral-400/15 to-neutral-400/0',
    fadeLeft && 'bg-transparent bg-gradient-to-l from-neutral-400/15 to-neutral-400/0',
  )
}
</script>

<template>
  <RangeCalendarHeader class="grid w-full grid-cols-[auto_1fr_auto] items-center gap-0.5 pb-1">
    <div class="flex shrink-0 items-center gap-0.5">
      <RangeCalendarPrev :class="navBtnClass">
        <ChevronLeft class="size-4" aria-hidden="true" />
      </RangeCalendarPrev>
    </div>
    <div class="flex items-center justify-center gap-1">
      <template v-if="isDropdownCaption">
        <select
          :value="placeholder?.month"
          class="h-8 rounded-md border border-border bg-background px-2 text-sm"
          @change="onMonthChange"
        >
          <option v-for="month in monthOptions" :key="month.value" :value="month.value">
            {{ month.label }}
          </option>
        </select>
        <select
          :value="placeholder?.year"
          class="h-8 rounded-md border border-border bg-background px-2 text-sm"
          @change="onYearChange"
        >
          <option v-for="year in yearOptions" :key="year" :value="year">
            {{ year }}
          </option>
        </select>
      </template>
      <RangeCalendarHeading v-else class="text-center text-sm font-medium" />
    </div>
    <div class="flex shrink-0 items-center gap-0.5 justify-self-end">
      <RangeCalendarNext :class="navBtnClass">
        <ChevronRight class="size-4" aria-hidden="true" />
      </RangeCalendarNext>
    </div>
  </RangeCalendarHeader>
  <div class="flex flex-col gap-4 sm:flex-row">
    <RangeCalendarGrid
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
      <RangeCalendarGridHead>
        <RangeCalendarGridRow>
          <RangeCalendarHeadCell
            v-for="day in weekDays"
            :key="day"
            class="p-0 align-middle font-normal"
          >
            <span
              class="flex size-8 items-center justify-center rounded-md text-[0.8rem] text-muted-foreground"
            >
              {{ day }}
            </span>
          </RangeCalendarHeadCell>
        </RangeCalendarGridRow>
      </RangeCalendarGridHead>
      <RangeCalendarGridBody class="[&>tr>td]:p-0">
        <RangeCalendarGridRow v-for="(weekDates, wi) in month.rows" :key="`rw-${wi}`">
          <RangeCalendarCell
            v-for="weekDate in weekDates"
            :key="weekDate.toString()"
            :date="weekDate"
            class="relative w-8 p-0 align-middle text-sm"
          >
            <div class="flex justify-center">
              <RangeCalendarCellTrigger
                :day="weekDate"
                :month="month.value"
                :class="triggerShellClass"
              >
                <template
                  #default="{
                    dayValue,
                    disabled,
                    selected,
                    selectionStart,
                    selectionEnd,
                    highlighted,
                    highlightedStart,
                    highlightedEnd,
                  }"
                >
                  <div
                    :class="
                      rangeInnerTrackClass(weekDate, {
                        disabled,
                        selected,
                        selectionStart,
                        selectionEnd,
                      })
                    "
                  >
                    <span
                      :data-disabled="disabled ? true : undefined"
                      :class="
                        rangeCalendarCellLabelVariants({
                          selectionState: cellVariant({
                            selected,
                            selectionStart,
                            selectionEnd,
                            highlighted,
                            highlightedStart,
                            highlightedEnd,
                          }),
                        })
                      "
                    >
                      <slot
                        name="day-cell"
                        :date="weekDate"
                        :month="month.value"
                        :day-value="dayValue"
                        :disabled="disabled"
                        :selected="selected"
                        :selection-start="selectionStart"
                        :selection-end="selectionEnd"
                        :highlighted="highlighted"
                        :highlighted-start="highlightedStart"
                        :highlighted-end="highlightedEnd"
                      >
                        {{ dayValue }}
                      </slot>
                    </span>
                  </div>
                </template>
              </RangeCalendarCellTrigger>
            </div>
          </RangeCalendarCell>
        </RangeCalendarGridRow>
      </RangeCalendarGridBody>
    </RangeCalendarGrid>
  </div>
</template>

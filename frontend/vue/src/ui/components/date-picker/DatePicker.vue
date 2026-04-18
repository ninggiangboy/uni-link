<script setup lang="ts">
import type { DateValue } from '@internationalized/date'
import { parseDate, toCalendarDate } from '@internationalized/date'
import { computed, ref, watch } from 'vue'
import { Calendar } from 'lucide-vue-next'
import {
  DatePickerAnchor,
  DatePickerCalendar,
  DatePickerContent,
  DatePickerField,
  DatePickerInput,
  DatePickerRoot,
  type DatePickerRootProps,
  DatePickerTrigger,
} from 'reka-ui'
import CalendarPanelGrids from '@/ui/components/calendar/CalendarPanelGrids.vue'
import { buttonVariants } from '@/ui/components/button/button-variants'
import { FieldGroup } from '@/ui/components/field'
import { dateFieldSegmentClassName } from '@/ui/lib/date-field-segment-classes'
import { UI_DATE_FIELD_LOCALE } from '@/ui/lib/date-field-locale'
import { datePickerPopoverContentClass } from '@/ui/lib/date-picker-popover-content-class'
import { omitUndefinedProps } from '@/ui/lib/omit-undefined-props'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

export type DatePickerProps = Omit<DatePickerRootProps, 'modelValue'> & { class?: string }

const props = withDefaults(defineProps<DatePickerProps>(), {
  locale: UI_DATE_FIELD_LOCALE,
})

/** ISO `YYYY-MM-DD` (Gregorian) for forms / APIs; Reka still uses `DateValue` internally. */
const model = defineModel<string | undefined>()
const innerModel = ref<DateValue | undefined>()

function stringFromDate(v: DateValue | undefined): string | undefined {
  return v == null ? undefined : toCalendarDate(v).toString()
}

watch(
  () => model.value,
  (s) => {
    const innerStr = stringFromDate(innerModel.value)
    if (s === innerStr) return
    if (s == null || s === '') {
      innerModel.value = undefined
      return
    }
    try {
      innerModel.value = parseDate(s)
    } catch {
      innerModel.value = undefined
    }
  },
  { immediate: true },
)

watch(innerModel, (v) => {
  const next = stringFromDate(v)
  if (next === model.value) return
  model.value = next
})

const rootProps = computed(() => {
  const raw = { ...(props as Record<string, unknown>) }
  delete raw.class
  delete raw.open
  delete raw['onUpdate:open']
  /** `defineModel` puts these on `props`; spreading them would override `v-model="innerModel"` with a string. */
  delete raw.modelValue
  delete raw['onUpdate:modelValue']
  return omitUndefinedProps(raw)
})
</script>

<template>
  <DatePickerRoot
    v-bind="rootProps"
    v-model="innerModel"
    :week-starts-on="1"
    :class="cn('flex w-full max-w-[280px] flex-col gap-1', props.class)"
  >
    <DatePickerField v-slot="{ segments }" class="w-full min-w-0">
      <FieldGroup class="flex min-h-8 w-full items-center gap-0.5 px-1.5 py-0">
        <DatePickerAnchor as-child>
          <div class="flex min-w-0 flex-1 flex-wrap items-center gap-0">
            <template v-for="(item, i) in segments" :key="`date-picker-seg-${i}`">
              <DatePickerInput
                :part="item.part"
                :class="dateFieldSegmentClassName()"
              >
                {{ item.value }}
              </DatePickerInput>
            </template>
          </div>
        </DatePickerAnchor>
        <DatePickerTrigger as-child>
          <button
            type="button"
            :class="
              cn(
                buttonVariants({ variant: 'ghost', size: 'iconSm' }),
                '-mr-1 shrink-0 rounded-md text-muted-foreground hover:bg-muted/80 hover:text-foreground focus-visible:ring-1 focus-visible:ring-offset-0',
              )
            "
            aria-label="Open calendar"
          >
            <Calendar class="size-4" aria-hidden="true" />
          </button>
        </DatePickerTrigger>
      </FieldGroup>
    </DatePickerField>
    <DatePickerContent
      side="bottom"
      align="start"
      :side-offset="6"
      :class="datePickerPopoverContentClass()"
    >
      <DatePickerCalendar v-slot="{ grid, weekDays }">
        <div class="rounded-md bg-background-secondary/40 p-1">
          <CalendarPanelGrids :grid="grid" :week-days="weekDays" />
        </div>
      </DatePickerCalendar>
    </DatePickerContent>
  </DatePickerRoot>
</template>

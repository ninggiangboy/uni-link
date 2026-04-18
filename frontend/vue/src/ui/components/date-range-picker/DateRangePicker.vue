<script setup lang="ts">
import type { DateValue } from '@internationalized/date'
import { computed } from 'vue'
import { Calendar } from 'lucide-vue-next'
import {
  DateRangePickerAnchor,
  DateRangePickerCalendar,
  DateRangePickerContent,
  DateRangePickerField,
  DateRangePickerInput,
  DateRangePickerRoot,
  type DateRangePickerRootProps,
  DateRangePickerTrigger,
} from 'reka-ui'
import { useIsMobile } from '@/ui/composables/useIsMobile'
import RangeCalendarPanelGrids from '@/ui/components/calendar/RangeCalendarPanelGrids.vue'
import { buttonVariants } from '@/ui/components/button/button-variants'
import { FieldGroup } from '@/ui/components/field'
import { dateFieldSegmentClassName } from '@/ui/lib/date-field-segment-classes'
import { UI_DATE_FIELD_LOCALE } from '@/ui/lib/date-field-locale'
import { datePickerPopoverContentClass } from '@/ui/lib/date-picker-popover-content-class'
import { omitUndefinedProps } from '@/ui/lib/omit-undefined-props'
import { cn } from '@/ui/lib/utils'

type DateRangeModel = { start: DateValue | undefined; end: DateValue | undefined }

defineOptions({ inheritAttrs: false })

export type DateRangePickerProps = Omit<DateRangePickerRootProps, 'modelValue'> & { class?: string }

const props = withDefaults(defineProps<DateRangePickerProps>(), {
  locale: UI_DATE_FIELD_LOCALE,
})

const rootProps = computed(() => {
  const raw = { ...(props as Record<string, unknown>) }
  delete raw.class
  delete raw.open
  delete raw['onUpdate:open']
  return omitUndefinedProps(raw)
})

const model = defineModel<DateRangeModel | undefined>()

const rangeForRoot = computed({
  get: () =>
    model.value == null ? { start: undefined, end: undefined } : model.value,
  set: (v: DateRangeModel) => {
    const cleared = v.start == null && v.end == null
    model.value = cleared ? undefined : v
  },
})

const { isMobile } = useIsMobile({ breakpointPx: 768 })
const numberOfMonths = computed(() => (isMobile.value ? 1 : 2))
</script>

<template>
  <DateRangePickerRoot
    v-model="rangeForRoot"
    v-bind="rootProps"
    :week-starts-on="1"
    :number-of-months="numberOfMonths"
    :class="cn('flex w-full max-w-[360px] flex-col gap-1', props.class)"
  >
    <DateRangePickerField v-slot="{ segments }" class="w-full min-w-0">
      <FieldGroup class="flex min-h-8 w-full items-center gap-0.5 px-1.5 py-0">
        <DateRangePickerAnchor as-child>
          <div class="flex min-w-0 flex-1 flex-wrap items-center gap-0">
            <template v-for="(item, i) in segments.start" :key="`drp-start-${i}`">
              <DateRangePickerInput
                type="start"
                :part="item.part"
                :class="dateFieldSegmentClassName()"
              >
                {{ item.value }}
              </DateRangePickerInput>
            </template>
            <span class="shrink-0 px-0.5 text-sm text-muted-foreground" aria-hidden="true">–</span>
            <template v-for="(item, i) in segments.end" :key="`drp-end-${i}`">
              <DateRangePickerInput
                type="end"
                :part="item.part"
                :class="dateFieldSegmentClassName()"
              >
                {{ item.value }}
              </DateRangePickerInput>
            </template>
          </div>
        </DateRangePickerAnchor>
        <DateRangePickerTrigger as-child>
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
        </DateRangePickerTrigger>
      </FieldGroup>
    </DateRangePickerField>
    <DateRangePickerContent
      side="bottom"
      align="start"
      :side-offset="6"
      :class="datePickerPopoverContentClass()"
    >
      <DateRangePickerCalendar v-slot="{ grid, weekDays }">
        <div class="rounded-md bg-background-secondary/40 p-1">
          <RangeCalendarPanelGrids :grid="grid" :week-days="weekDays" />
        </div>
      </DateRangePickerCalendar>
    </DateRangePickerContent>
  </DateRangePickerRoot>
</template>

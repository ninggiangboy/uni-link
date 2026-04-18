<script setup lang="ts">
import type { DateValue } from '@internationalized/date'
import { Calendar } from '@/ui/components/calendar'

function isWeekend(date: DateValue) {
  const day = date.toDate('UTC').getDay()
  return day === 0 || day === 6
}
</script>

<template>
  <Calendar
    caption-layout="dropdown"
    class="[&_col]:w-10! [&_col]:min-w-10! [&_col]:max-w-10! **:data-[slot='calendar-cell-trigger']:size-10!"
  >
    <template #day-cell="{ date, dayValue, disabled, selected }">
      <div class="flex flex-col items-center justify-center leading-tight">
        <span>{{ dayValue }}</span>
        <span
          v-if="!disabled"
          :class="['text-[10px]', selected ? 'text-white/90' : 'text-muted-foreground']"
        >
          {{ isWeekend(date) ? '$120' : '$100' }}
        </span>
      </div>
    </template>
  </Calendar>
</template>

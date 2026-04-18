<script setup lang="ts">
import { getLocalTimeZone, today } from '@internationalized/date'
import { RangeCalendar } from '@/ui/components/calendar'

const t = today(getLocalTimeZone())
const def = { start: t, end: t.add({ days: 10 }) }
</script>

<template>
  <RangeCalendar :default-value="def" />
</template>

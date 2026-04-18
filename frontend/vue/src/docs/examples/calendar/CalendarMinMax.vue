<script setup lang="ts">
import { getLocalTimeZone, today } from '@internationalized/date'
import { Calendar } from '@/ui/components/calendar'

const min = today(getLocalTimeZone())
const max = min.add({ days: 10 })
</script>

<template>
    <Calendar :min-value="min" :max-value="max" />
</template>

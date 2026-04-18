<script setup lang="ts">
import { Label } from '@/ui/components/field'
import { DateRangePicker } from '@/ui/components/date-range-picker'
</script>
<template>
  <div class="grid w-full max-w-[320px] gap-1.5">
    <Label>Trip dates</Label>
    <DateRangePicker />
  </div>
</template>

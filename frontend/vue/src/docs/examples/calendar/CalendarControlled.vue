<script setup lang="ts">
import type { DateValue } from '@internationalized/date';
import { ref } from 'vue';
import { Calendar } from '@/ui/components/calendar';

const value = ref<DateValue | undefined>();
</script>

<template>
  <div class="flex flex-col gap-2">
    <Calendar v-model="value" />
    <pre class="rounded-md border bg-background-secondary p-2 font-mono text-xs">{{
      value ? value.toString() : 'No value'
    }}</pre>
  </div>
</template>

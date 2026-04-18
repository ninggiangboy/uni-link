<script setup lang="ts">
import { Label } from '@/ui/components/field'
import { DateField } from '@/ui/components/datefield'
</script>
<template>
  <div class="grid w-full max-w-xs gap-1.5">
    <Label for="dfd1">Date</Label>
    <DateField id="dfd1" />
  </div>
</template>

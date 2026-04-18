<script setup lang="ts">
import { NumberField } from '@/ui/components/numberfield'
</script>
<template>
  <div class="w-full max-w-xs">
    <NumberField :format-options="{ style: 'percent' }" placeholder="0%" />
  </div>
</template>

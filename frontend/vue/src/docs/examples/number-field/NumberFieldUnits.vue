<script setup lang="ts">
import { NumberField } from '@/ui/components/numberfield'
</script>
<template>
  <div class="w-full max-w-xs">
    <NumberField :format-options="{ style: 'unit', unit: 'kilometer', unitDisplay: 'short' }" />
  </div>
</template>

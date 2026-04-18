<script setup lang="ts">
import { NumberField } from '@/ui/components/numberfield'
</script>
<template>
  <div class="w-full max-w-xs">
    <NumberField :min="0" :max="100" :step="5" />
  </div>
</template>

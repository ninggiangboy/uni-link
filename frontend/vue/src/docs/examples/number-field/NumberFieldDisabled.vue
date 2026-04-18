<script setup lang="ts">
import { NumberField } from '@/ui/components/numberfield'
</script>
<template>
  <div class="w-full max-w-xs opacity-80">
    <NumberField :model-value="42" disabled />
  </div>
</template>

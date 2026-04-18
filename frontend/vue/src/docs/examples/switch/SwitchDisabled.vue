<script setup lang="ts">
import { Switch } from '@/ui/components/switch-ui'
</script>

<template>
  <label class="flex items-center gap-2 text-sm opacity-70">
    <Switch :model-value="true" disabled />
    Disabled
  </label>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Switch } from '@/ui/components/switch-ui'

const v = ref(true)
</script>

<template>
  <label class="flex items-center gap-2 text-sm">
    <Switch v-model="v" />
    Airplane mode
  </label>
</template>

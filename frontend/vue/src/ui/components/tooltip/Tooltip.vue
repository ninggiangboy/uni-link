<script setup lang="ts">
import { TooltipRoot } from 'reka-ui'

const open = defineModel<boolean>('open', { default: undefined })
</script>

<template>
  <TooltipRoot v-model:open="open" :delay-duration="200">
    <slot />
  </TooltipRoot>
</template>

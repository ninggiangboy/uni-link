<script setup lang="ts">
import { TooltipTrigger as RekaTooltipTrigger } from 'reka-ui'
</script>

<template>
  <RekaTooltipTrigger v-bind="$attrs" as-child>
    <slot />
  </RekaTooltipTrigger>
</template>

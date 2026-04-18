<script setup lang="ts">
import { PopoverTrigger as RekaPopoverTrigger } from 'reka-ui'
</script>

<template>
  <RekaPopoverTrigger v-bind="$attrs">
    <slot />
  </RekaPopoverTrigger>
</template>

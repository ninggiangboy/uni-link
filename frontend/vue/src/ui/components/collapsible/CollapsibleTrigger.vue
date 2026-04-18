<script setup lang="ts">
import { CollapsibleTrigger as RekaCollapsibleTrigger } from 'reka-ui'
</script>

<template>
  <RekaCollapsibleTrigger v-bind="$attrs">
    <slot />
  </RekaCollapsibleTrigger>
</template>

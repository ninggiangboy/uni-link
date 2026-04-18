<script setup lang="ts">
import { PopoverAnchor as RekaPopoverAnchor } from 'reka-ui'
</script>

<template>
  <RekaPopoverAnchor v-bind="$attrs">
    <slot />
  </RekaPopoverAnchor>
</template>

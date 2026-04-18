<script setup lang="ts">
import { CollapsibleRoot } from 'reka-ui'

const open = defineModel<boolean>('open', { default: false })
</script>

<template>
  <CollapsibleRoot v-model:open="open" v-bind="$attrs">
    <slot />
  </CollapsibleRoot>
</template>

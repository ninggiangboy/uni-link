<script setup lang="ts">
import { ListboxGroup } from 'reka-ui'
</script>

<template>
  <ListboxGroup v-bind="$attrs">
    <slot />
  </ListboxGroup>
</template>

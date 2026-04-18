<script setup lang="ts">
import { DropdownMenuTrigger as Reka } from 'reka-ui'
</script>

<template>
  <Reka v-bind="$attrs" as-child>
    <slot />
  </Reka>
</template>

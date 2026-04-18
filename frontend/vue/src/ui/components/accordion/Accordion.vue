<script setup lang="ts">
import { AccordionRoot } from 'reka-ui'

defineProps<{
  type?: 'single' | 'multiple'
  collapsible?: boolean
  defaultValue?: string | string[]
}>()
</script>

<template>
  <AccordionRoot
    data-slot="accordion"
    :type="type ?? 'single'"
    :collapsible="collapsible ?? true"
    :default-value="defaultValue"
  >
    <slot />
  </AccordionRoot>
</template>

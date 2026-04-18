<script setup lang="ts">
import { CheckboxGroupRoot } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()

const model = defineModel<string[]>({ default: () => [] })
</script>

<template>
  <CheckboxGroupRoot v-model="model" :class="cn('grid gap-2', $props.class)">
    <slot />
  </CheckboxGroupRoot>
</template>

<script setup lang="ts">
import { RadioGroupRoot } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()

const model = defineModel<string | undefined>()
</script>

<template>
  <RadioGroupRoot v-model="model" v-bind="$attrs" :class="cn('grid gap-2', $props.class)">
    <slot />
  </RadioGroupRoot>
</template>

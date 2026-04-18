<script setup lang="ts">
import { Toggle as RekaToggle } from 'reka-ui'
import { cn } from '@/ui/lib/utils'
import { toggleVariants, type ToggleVariants } from './toggle-variants'

defineOptions({ inheritAttrs: false })

const props = defineProps<{
  class?: string
  variant?: ToggleVariants['variant']
  size?: ToggleVariants['size']
}>()

const pressed = defineModel<boolean>('modelValue', { default: false })
</script>

<template>
  <RekaToggle
    v-bind="$attrs"
    v-model="pressed"
    :class="cn(toggleVariants({ variant: props.variant, size: props.size }), props.class)"
  >
    <slot />
  </RekaToggle>
</template>

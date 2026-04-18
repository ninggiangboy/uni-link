<script setup lang="ts">
import type { LabelHTMLAttributes } from 'vue'
import { computed, useAttrs } from 'vue'
import { cn } from '@/ui/lib/utils'
import { labelVariants } from './field-variants'

defineOptions({ inheritAttrs: false })

const props = defineProps<{
  class?: LabelHTMLAttributes['class']
}>()

const attrs = useAttrs()
const merged = computed(() => cn(labelVariants(), props.class, attrs.class as string))
const passthrough = computed(() => {
  const rest = { ...attrs } as Record<string, unknown>
  delete rest.class
  return rest
})
</script>

<template>
  <label data-slot="label" :class="merged" v-bind="passthrough">
    <slot />
  </label>
</template>

<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { computed, useAttrs } from 'vue'
import { cn } from '@/ui/lib/utils'
import { fieldGroupVariants, type FieldGroupVariant } from './field-variants'

defineOptions({ inheritAttrs: false })

const props = defineProps<{
  class?: HTMLAttributes['class']
  variant?: FieldGroupVariant
}>()

const attrs = useAttrs()

const mergedClass = computed(() =>
  cn(
    fieldGroupVariants({ variant: props.variant ?? 'default' }),
    props.class,
    attrs.class as string,
  ),
)
const passthrough = computed(() => {
  const rest = { ...attrs } as Record<string, unknown>
  delete rest.class
  return rest
})
</script>

<template>
  <div role="group" data-slot="field-group" :class="mergedClass" v-bind="passthrough">
    <slot />
  </div>
</template>

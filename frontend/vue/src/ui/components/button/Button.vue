<script setup lang="ts">
import type { Component, HTMLAttributes } from 'vue'
import { computed, useAttrs } from 'vue'
import { buttonVariants, type ButtonVariants } from './button-variants'
import { Spinner } from '@/ui/components/spinner'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

const props = withDefaults(
  defineProps<{
    variant?: ButtonVariants['variant']
    size?: ButtonVariants['size']
    /** Root element, e.g. `'button'`, `'a'`, or `RouterLink` */
    as?: string | Component
    class?: HTMLAttributes['class']
    loading?: boolean
    iconPosition?: 'left' | 'right'
  }>(),
  {
    variant: 'default',
    size: 'default',
    as: 'button',
    loading: false,
    iconPosition: 'left',
  },
)

const attrs = useAttrs()

const mergedClass = computed(() =>
  cn(
    buttonVariants({ variant: props.variant, size: props.size }),
    props.class,
    attrs.class as string,
  ),
)

const passthrough = computed(() => {
  const rest = { ...attrs } as Record<string, unknown>
  delete rest.class
  delete rest.type
  return rest
})

const isNativeButton = computed(() => props.as === 'button')

const resolvedType = computed(() => {
  if (!isNativeButton.value) return undefined
  const t = attrs.type as string | undefined
  return (t ?? 'button') as 'button' | 'submit' | 'reset'
})

const disabled = computed(() => {
  return props.loading || (attrs.disabled as boolean | undefined)
})
</script>

<template>
  <component
    :is="as"
    data-slot="button"
    :type="resolvedType"
    :class="mergedClass"
    v-bind="passthrough"
    :disabled="disabled"
  >
    <template v-if="iconPosition === 'left'">
      <Spinner v-if="loading" />
      <slot v-else name="icon" />
    </template>

    <slot />

    <template v-if="iconPosition === 'right'">
      <Spinner v-if="loading" />
      <slot v-else name="icon" />
    </template>
  </component>
</template>

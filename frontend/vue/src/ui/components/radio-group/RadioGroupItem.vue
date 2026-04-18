<script setup lang="ts">
import { RadioGroupIndicator, RadioGroupItem as RekaRadioGroupItem } from 'reka-ui'
import { cn } from '@/ui/lib/utils'
import { labelVariants } from '@/ui/components/field/field-variants'

defineOptions({ inheritAttrs: false })

defineProps<{
  class?: string
  value: string
}>()
</script>

<template>
  <label
    :class="cn('flex items-center gap-x-2 text-sm cursor-pointer', labelVariants(), $props.class)"
  >
    <RekaRadioGroupItem
      :value="value"
      v-bind="$attrs"
      :class="
        cn(
          'aspect-square size-4 rounded-full border border-input bg-background-secondary shadow-sm text-primary',
          'focus-visible:ring-2 focus-visible:ring-primary/40 focus-visible:ring-offset-2 outline-none',
          'disabled:cursor-not-allowed disabled:opacity-50',
          'data-[state=checked]:border-primary',
        )
      "
    >
      <RadioGroupIndicator class="flex items-center justify-center">
        <span class="size-2 rounded-full bg-primary" />
      </RadioGroupIndicator>
    </RekaRadioGroupItem>
    <slot />
  </label>
</template>

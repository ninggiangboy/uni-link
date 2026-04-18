<script setup lang="ts">
import { CheckboxIndicator, CheckboxRoot } from 'reka-ui'
import { Check, Minus } from 'lucide-vue-next'
import { cn } from '@/ui/lib/utils'
import { labelVariants } from '@/ui/components/field/field-variants'

defineOptions({ inheritAttrs: false })

defineProps<{
  class?: string
}>()

const model = defineModel<boolean | 'indeterminate'>({ default: false })
</script>

<template>
  <label
    :class="
      cn(
        'group/checkbox flex items-center gap-x-2 text-sm cursor-pointer',
        labelVariants(),
        'has-[[data-disabled]]:cursor-not-allowed has-[[data-disabled]]:opacity-70',
        $props.class,
      )
    "
  >
    <CheckboxRoot
      v-model="model"
      v-bind="$attrs"
      class="peer shrink-0 outline-none focus-visible:ring-2 focus-visible:ring-primary/40 focus-visible:ring-offset-2"
    >
      <template #default="{ state }">
        <!-- Reka only mounts the indicator when checked/indeterminate; keep mounted so the box stays visible when unchecked -->
        <CheckboxIndicator
          force-mount
          :class="
            cn(
              'flex size-4 shrink-0 items-center justify-center rounded-[5px] border bg-background-secondary text-white ring-offset-background',
              'data-[state=checked]:bg-primary data-[state=indeterminate]:bg-primary data-[state=checked]:border-black/10 data-[state=indeterminate]:border-black/10',
              'data-[state=unchecked]:text-transparent',
              'data-[state=checked]:shadow-[inset_0_1px_0_0_rgba(255,255,255,0.1)]',
              'dark:data-[state=checked]:border-none dark:data-[state=indeterminate]:border-none',
              'peer-disabled:cursor-not-allowed peer-disabled:opacity-80',
            )
          "
        >
          <Minus v-if="state === 'indeterminate'" class="size-4" />
          <Check v-else-if="state === true" class="size-4" />
        </CheckboxIndicator>
      </template>
    </CheckboxRoot>
    <slot />
  </label>
</template>

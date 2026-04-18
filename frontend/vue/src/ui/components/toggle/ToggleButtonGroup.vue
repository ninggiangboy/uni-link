<script setup lang="ts">
import type { AcceptableValue } from 'reka-ui'
import { ToggleGroupRoot } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

defineProps<{
  class?: string
  type?: 'single' | 'multiple'
  orientation?: 'horizontal' | 'vertical'
}>()

const model = defineModel<AcceptableValue | AcceptableValue[] | undefined>()
</script>

<template>
  <ToggleGroupRoot
    v-model="model"
    v-bind="$attrs"
    :type="type"
    :orientation="orientation"
    :class="
      cn(
        'group/togglegroup flex items-center justify-center gap-1 data-[orientation=vertical]:flex-col',
        $props.class,
      )
    "
  >
    <slot />
  </ToggleGroupRoot>
</template>

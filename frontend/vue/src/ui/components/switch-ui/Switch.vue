<script setup lang="ts">
import { SwitchRoot, SwitchThumb } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

defineProps<{
  class?: string
}>()

const model = defineModel<boolean>({ default: false })
</script>

<template>
  <SwitchRoot
    v-model="model"
    v-bind="$attrs"
    :class="
      cn(
        'group inline-flex h-5 w-8 shrink-0 cursor-pointer items-center rounded-full border-2 border-transparent transition-colors',
        'bg-input data-[state=checked]:bg-primary',
        'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/40 focus-visible:ring-offset-2',
        'disabled:cursor-not-allowed disabled:opacity-50',
        $props.class,
      )
    "
  >
    <SwitchThumb
      :class="
        cn(
          'pointer-events-none block size-[14px] rounded-full bg-white shadow-lg ring-0 transition-transform',
          'translate-x-0.5 data-[state=checked]:translate-x-3.5',
        )
      "
    />
  </SwitchRoot>
</template>

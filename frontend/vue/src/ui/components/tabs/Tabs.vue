<script setup lang="ts">
import { TabsRoot } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
  orientation?: 'horizontal' | 'vertical'
  defaultValue?: string | number
}>()

const tab = defineModel<string | number>('modelValue')
</script>

<template>
  <TabsRoot
    v-model="tab"
    :orientation="orientation"
    :default-value="defaultValue"
    :class="
      cn(
        'group/tabs-root group flex flex-col gap-2 data-[orientation=vertical]:flex-row',
        $props.class,
      )
    "
  >
    <slot />
  </TabsRoot>
</template>

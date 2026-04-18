<script setup lang="ts">
import { AccordionItem as RekaAccordionItem } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
  value: string
}>()
</script>

<template>
  <RekaAccordionItem
    data-slot="accordion-item"
    :value="value"
    :class="cn('border-b last:border-b-0', $props.class)"
  >
    <slot />
  </RekaAccordionItem>
</template>

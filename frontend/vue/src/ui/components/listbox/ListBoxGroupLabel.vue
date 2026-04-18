<script setup lang="ts">
import { ListboxGroupLabel } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <ListboxGroupLabel
    v-bind="$attrs"
    :class="cn('py-1.5 pl-8 pr-2 text-sm font-semibold', $props.class)"
  >
    <slot />
  </ListboxGroupLabel>
</template>

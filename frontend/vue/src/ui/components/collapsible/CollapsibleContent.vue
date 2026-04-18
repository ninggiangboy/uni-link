<script setup lang="ts">
import { CollapsibleContent as RekaCollapsibleContent } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <RekaCollapsibleContent v-bind="$attrs" :class="cn($props.class)">
    <slot />
  </RekaCollapsibleContent>
</template>

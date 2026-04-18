<script setup lang="ts">
import { AvatarRoot } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

defineProps<{
  class?: string
}>()
</script>

<template>
  <AvatarRoot
    data-slot="avatar"
    :class="cn('relative flex size-7 shrink-0 overflow-hidden rounded-full', $props.class)"
    v-bind="$attrs"
  >
    <slot />
  </AvatarRoot>
</template>

<script setup lang="ts">
import { AvatarFallback as RekaAvatarFallback } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

defineProps<{
  class?: string
}>()
</script>

<template>
  <RekaAvatarFallback
    data-slot="avatar-fallback"
    :class="
      cn(
        'bg-sky-500 flex size-full items-center justify-center rounded-full text-white',
        $props.class,
      )
    "
    v-bind="$attrs"
  >
    <slot />
  </RekaAvatarFallback>
</template>

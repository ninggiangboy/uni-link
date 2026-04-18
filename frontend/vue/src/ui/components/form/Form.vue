<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

defineProps<{
  class?: HTMLAttributes['class']
}>()
</script>

<template>
  <form data-slot="form" v-bind="$attrs" :class="cn($props.class)">
    <slot />
  </form>
</template>

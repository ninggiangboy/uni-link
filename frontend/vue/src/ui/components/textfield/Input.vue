<script setup lang="ts">
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

defineProps<{
  class?: string
}>()
</script>

<template>
  <input
    autocomplete="off"
    data-slot="input"
    :class="
      cn(
        'shadow-sm flex h-8 w-full rounded-sm bg-background-secondary px-3 py-1.5 ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground md:text-sm',
        'ring-inset ring ring-input',
        'transition-all focus-visible:ring-primary focus-visible:ring-2 aria-invalid:ring-destructive',
        'disabled:cursor-not-allowed disabled:opacity-80',
        'focus-visible:outline-none',
        $props.class,
      )
    "
    v-bind="$attrs"
  />
</template>

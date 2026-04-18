<script setup lang="ts">
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

defineProps<{
  class?: string
}>()
</script>

<template>
  <textarea
    data-slot="textarea"
    :class="
      cn(
        'shadow-sm flex min-h-[80px] w-full rounded-sm bg-background-secondary px-3 py-2 ring-offset-background placeholder:text-muted-foreground md:text-sm',
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

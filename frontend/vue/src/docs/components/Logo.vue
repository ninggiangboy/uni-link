<script setup lang="ts">
import { cn } from '@/ui/lib/utils';

const props = withDefaults(
  defineProps<{
    withName?: boolean;
    class?: string;
  }>(),
  { withName: true },
);
</script>

<template>
  <div :class="cn('flex items-center gap-2 font-semibold text-foreground', props.class)">
    <span
      class="size-8 rounded-md bg-primary/15 border border-border flex items-center justify-center text-xs text-primary"
    >
      U
    </span>
    <span v-if="withName" class="text-sm tracking-tight">UniLink UI</span>
  </div>
</template>

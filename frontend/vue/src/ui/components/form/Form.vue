<script setup lang="ts">
import type { HTMLAttributes } from 'vue';
import { cn } from '@/ui/lib/utils';

defineOptions({ inheritAttrs: false });

const props = defineProps<{
  class?: HTMLAttributes['class'];
}>();

const emit = defineEmits<{
  (e: 'submit', event: Event): void;
}>();
</script>

<template>
  <form
    data-slot="form"
    v-bind="$attrs"
    :class="cn(props.class)"
    @submit.prevent="emit('submit', $event)"
  >
    <slot />
  </form>
</template>

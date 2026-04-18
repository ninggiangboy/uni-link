<script setup lang="ts">
import { cn } from '@/ui/lib/utils'

const props = defineProps<{ class?: string }>()
</script>

<template>
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="23"
    height="23"
    fill="none"
    viewBox="0 0 23 23"
    :class="cn(props.class)"
  >
    <path fill="var(--background)" d="M0 0h23v23H0z" />
    <path stroke="var(--color-border)" d="M4 11.5h8M11 11.5h8M11.5 11v8M11.5 4v8" />
  </svg>
</template>

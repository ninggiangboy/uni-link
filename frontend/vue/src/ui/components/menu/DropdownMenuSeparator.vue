<script setup lang="ts">
import { DropdownMenuSeparator as Reka } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineProps<{
  class?: string
}>()
</script>

<template>
  <Reka :class="cn('-mx-1 my-1 h-px bg-border', $props.class)" />
</template>

<script setup lang="ts">
import { DropdownMenuSubContent as Reka } from 'reka-ui'
import { dropdownMenuContentClass } from './dropdown-menu-surface'

const props = withDefaults(
  defineProps<{
    class?: string
    sideOffset?: number
  }>(),
  { sideOffset: 4 },
)
</script>

<template>
  <Reka
    v-bind="$attrs"
    :side-offset="props.sideOffset"
    :class="dropdownMenuContentClass(props.class)"
  >
    <slot />
  </Reka>
</template>

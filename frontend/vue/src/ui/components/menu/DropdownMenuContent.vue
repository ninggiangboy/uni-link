<script setup lang="ts">
import { DropdownMenuContent as Reka, DropdownMenuPortal } from 'reka-ui'
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
  <DropdownMenuPortal>
    <Reka
      v-bind="$attrs"
      :side-offset="props.sideOffset"
      :class="dropdownMenuContentClass(props.class)"
    >
      <slot />
    </Reka>
  </DropdownMenuPortal>
</template>

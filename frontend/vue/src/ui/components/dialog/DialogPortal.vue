<script setup lang="ts">
import { DialogPortal as RekaDialogPortal } from 'reka-ui'
</script>

<template>
  <RekaDialogPortal v-bind="$attrs">
    <slot />
  </RekaDialogPortal>
</template>

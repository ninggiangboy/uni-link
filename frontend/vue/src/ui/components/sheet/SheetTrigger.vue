<script setup lang="ts">
import { DialogTrigger } from 'reka-ui'
</script>

<template>
  <DialogTrigger v-bind="$attrs" as-child>
    <slot />
  </DialogTrigger>
</template>

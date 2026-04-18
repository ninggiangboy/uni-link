<script setup lang="ts">
import { DialogTrigger as RekaDialogTrigger } from 'reka-ui'
</script>

<template>
  <RekaDialogTrigger v-bind="$attrs" as-child>
    <slot />
  </RekaDialogTrigger>
</template>

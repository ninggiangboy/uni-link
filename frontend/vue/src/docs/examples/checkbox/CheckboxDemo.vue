<script setup lang="ts">
import { ref } from 'vue'
import { Checkbox } from '@/ui/components/checkbox'

const v = ref(false)
</script>

<template>
  <Checkbox v-model="v"> Accept terms and conditions </Checkbox>
</template>

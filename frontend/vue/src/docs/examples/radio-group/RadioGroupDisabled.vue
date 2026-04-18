<script setup lang="ts">
import { ref } from 'vue'
import { RadioGroup, RadioGroupItem } from '@/ui/components/radio-group'

const v = ref('a')
</script>

<template>
  <RadioGroup v-model="v" disabled class="max-w-xs">
    <RadioGroupItem value="a">A</RadioGroupItem>
    <RadioGroupItem value="b">B</RadioGroupItem>
  </RadioGroup>
</template>

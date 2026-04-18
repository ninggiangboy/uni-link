<script setup lang="ts">
import { ref } from 'vue'
import { RadioGroup, RadioGroupItem } from '@/ui/components/radio-group'

const v = ref('b')
</script>

<template>
  <RadioGroup v-model="v" class="flex flex-wrap gap-4">
    <RadioGroupItem value="a">A</RadioGroupItem>
    <RadioGroupItem value="b">B</RadioGroupItem>
    <RadioGroupItem value="c">C</RadioGroupItem>
  </RadioGroup>
</template>

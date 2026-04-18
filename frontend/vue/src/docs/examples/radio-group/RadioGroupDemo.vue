<script setup lang="ts">
import { ref } from 'vue'
import { RadioGroup, RadioGroupItem } from '@/ui/components/radio-group'

const v = ref('comfort')
</script>

<template>
  <RadioGroup v-model="v" class="max-w-xs">
    <RadioGroupItem value="default"> Default </RadioGroupItem>
    <RadioGroupItem value="comfort"> Comfortable </RadioGroupItem>
    <RadioGroupItem value="compact"> Compact </RadioGroupItem>
  </RadioGroup>
</template>

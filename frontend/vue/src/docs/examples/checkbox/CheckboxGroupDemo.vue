<script setup lang="ts">
import { ref } from 'vue'
import { Checkbox, CheckboxGroup } from '@/ui/components/checkbox'
import { Label } from '@/ui/components/field'

const sel = ref<string[]>([])
</script>

<template>
  <CheckboxGroup v-model="sel" class="w-full">
    <Label>Choose your favorite fruits</Label>
    <div class="grid grid-cols-3 gap-4">
      <Checkbox value="apple"> Apple </Checkbox>
      <Checkbox value="banana"> Banana </Checkbox>
      <Checkbox value="orange"> Orange </Checkbox>
    </div>
  </CheckboxGroup>
</template>

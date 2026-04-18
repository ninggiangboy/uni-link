<script setup lang="ts">
import { Label } from '@/ui/components/field'
import { NumberField } from '@/ui/components/numberfield'
</script>
<template>
  <div class="grid w-full max-w-xs gap-1.5">
    <Label for="nf-q">Quantity</Label>
    <NumberField id="nf-q" />
  </div>
</template>

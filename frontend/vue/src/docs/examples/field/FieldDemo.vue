<script setup lang="ts">
import { Label } from '@/ui/components/field'
import { Input } from '@/ui/components/textfield'
</script>

<template>
  <div class="w-full max-w-xs space-y-2">
    <Label for="f1">Email</Label>
    <Input id="f1" placeholder="you@example.com" />
  </div>
</template>

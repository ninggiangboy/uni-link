<script setup lang="ts">
import { Label } from '@/ui/components/field'
import { Input } from '@/ui/components/textfield'
</script>
<template>
  <div class="grid w-full max-w-xs gap-1.5">
    <Label for="i-wl">Email</Label>
    <Input id="i-wl" placeholder="you@example.com" />
  </div>
</template>

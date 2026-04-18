<script setup lang="ts">
import { Label } from '@/ui/components/field'
import { TextArea } from '@/ui/components/textfield'
</script>
<template>
  <div class="grid w-full max-w-sm gap-1.5">
    <Label for="ta-wl">Bio</Label>
    <TextArea id="ta-wl" placeholder="Tell us about yourself" class="min-h-24" />
  </div>
</template>

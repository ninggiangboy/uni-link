<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { Input } from '@/ui/components/textfield'
</script>
<template>
  <div class="flex w-full max-w-sm items-center gap-2">
    <Input placeholder="Search…" />
    <Button type="button" variant="secondary">Go</Button>
  </div>
</template>

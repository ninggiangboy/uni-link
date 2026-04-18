<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { TextArea } from '@/ui/components/textfield'
</script>
<template>
  <div class="grid w-full max-w-sm gap-2">
    <TextArea placeholder="Type a message" class="min-h-24" />
    <Button type="button" class="w-fit">Send</Button>
  </div>
</template>

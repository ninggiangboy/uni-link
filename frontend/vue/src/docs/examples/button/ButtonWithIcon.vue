<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { Plus } from 'lucide-vue-next'
</script>

<template>
  <Button>
    <template #icon>
      <Plus />
    </template>
    Add
  </Button>
</template>

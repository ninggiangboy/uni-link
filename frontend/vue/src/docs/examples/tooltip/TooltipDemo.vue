<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { Tooltip, TooltipContent, TooltipTrigger } from '@/ui/components/tooltip'
import { Pencil } from 'lucide-vue-next'
</script>

<template>
  <Tooltip :delay-duration="700">
    <TooltipTrigger as-child>
      <Button variant="outline" size="icon" aria-label="Edit">
        <Pencil class="size-4" />
      </Button>
    </TooltipTrigger>
    <TooltipContent class="max-w-xs"> Edit </TooltipContent>
  </Tooltip>
</template>

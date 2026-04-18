<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { Tooltip, TooltipContent, TooltipTrigger } from '@/ui/components/tooltip'
</script>

<template>
  <div class="flex space-x-4">
    <Tooltip>
      <TooltipTrigger as-child>
        <Button variant="outline"> Basic Tooltip </Button>
      </TooltipTrigger>
      <TooltipContent>
        <p>Simple tooltip text</p>
      </TooltipContent>
    </Tooltip>
    <Tooltip>
      <TooltipTrigger as-child>
        <Button variant="outline"> Another Tooltip </Button>
      </TooltipTrigger>
      <TooltipContent>
        <p>Another simple tooltip</p>
      </TooltipContent>
    </Tooltip>
  </div>
</template>

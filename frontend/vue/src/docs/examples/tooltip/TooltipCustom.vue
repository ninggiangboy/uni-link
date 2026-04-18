<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { Tooltip, TooltipContent, TooltipTrigger } from '@/ui/components/tooltip'
</script>

<template>
  <div class="flex space-x-4">
    <Tooltip>
      <TooltipTrigger as-child>
        <Button variant="outline"> Custom Tooltip </Button>
      </TooltipTrigger>
      <TooltipContent class="max-w-sm rounded-md">
        <div class="p-1">
          <h4 class="mb-1 text-sm font-semibold">Custom Tooltip</h4>
          <p class="text-xs">This tooltip has custom styling with a title and description.</p>
        </div>
      </TooltipContent>
    </Tooltip>
    <Tooltip>
      <TooltipTrigger as-child>
        <Button variant="outline"> Rich Content </Button>
      </TooltipTrigger>
      <TooltipContent class="max-w-sm rounded-md">
        <div class="p-1">
          <div class="mb-1 flex items-center space-x-2">
            <div class="size-2 rounded-full bg-green-500" />
            <span class="text-sm font-medium">Status: Active</span>
          </div>
          <p class="text-xs">
            This tooltip contains rich content with status indicators and multiple lines of text.
          </p>
        </div>
      </TooltipContent>
    </Tooltip>
  </div>
</template>

<script setup lang="ts">
import { Button } from '@/ui/components/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/ui/components/menu'
</script>

<template>
  <DropdownMenu>
    <DropdownMenuTrigger as-child>
      <Button variant="outline"> Shortcuts </Button>
    </DropdownMenuTrigger>
    <DropdownMenuContent>
      <DropdownMenuItem>
        New Tab
        <span class="ml-auto text-xs tracking-widest opacity-60">⌘T</span>
      </DropdownMenuItem>
      <DropdownMenuItem>
        Close Tab
        <span class="ml-auto text-xs tracking-widest opacity-60">⌘W</span>
      </DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>
</template>

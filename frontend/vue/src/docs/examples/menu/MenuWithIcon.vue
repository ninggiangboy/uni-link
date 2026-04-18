<script setup lang="ts">
import { Button } from '@/ui/components/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/ui/components/menu'
import { Copy, FolderOpen, Menu, Pencil, Share2, Trash2 } from 'lucide-vue-next'
</script>

<template>
  <DropdownMenu>
    <DropdownMenuTrigger as-child>
      <Button aria-label="Menu" size="icon" variant="outline">
        <Menu />
      </Button>
    </DropdownMenuTrigger>
    <DropdownMenuContent>
      <DropdownMenuItem>
        <FolderOpen class="size-4" />
        Open
      </DropdownMenuItem>
      <DropdownMenuItem>
        <Pencil class="size-4" />
        Rename
      </DropdownMenuItem>
      <DropdownMenuItem>
        <Copy class="size-4" />
        Duplicate
      </DropdownMenuItem>
      <DropdownMenuItem>
        <Share2 class="size-4" />
        Share
      </DropdownMenuItem>
      <DropdownMenuItem>
        <Trash2 class="size-4" />
        Delete
      </DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>
</template>

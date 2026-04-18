<script setup lang="ts">
import { ChevronRight } from 'lucide-vue-next'
import { Button } from '@/ui/components/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSub,
  DropdownMenuSubContent,
  DropdownMenuSubTrigger,
  DropdownMenuTrigger,
} from '@/ui/components/menu'
import { cn } from '@/ui/lib/utils'

const subTriggerClass = cn(
  'relative flex cursor-default select-none items-center gap-2.5 rounded-sm px-2 py-1.5 text-sm outline-none',
  'data-[disabled]:pointer-events-none data-[disabled]:opacity-50',
  'data-[highlighted]:bg-primary data-[highlighted]:text-white',
)
</script>

<template>
  <DropdownMenu>
    <DropdownMenuTrigger as-child>
      <Button variant="outline"> Actions </Button>
    </DropdownMenuTrigger>
    <DropdownMenuContent>
      <DropdownMenuItem>Copy</DropdownMenuItem>
      <DropdownMenuItem>Cut</DropdownMenuItem>
      <DropdownMenuItem>Delete</DropdownMenuItem>
      <DropdownMenuSub>
        <DropdownMenuSubTrigger :class="subTriggerClass">
          Share
          <ChevronRight class="ml-auto size-4" />
        </DropdownMenuSubTrigger>
        <DropdownMenuSubContent>
          <DropdownMenuItem>SMS</DropdownMenuItem>
          <DropdownMenuItem>Twitter</DropdownMenuItem>
          <DropdownMenuSub>
            <DropdownMenuSubTrigger :class="subTriggerClass">
              Email
              <ChevronRight class="ml-auto size-4" />
            </DropdownMenuSubTrigger>
            <DropdownMenuSubContent>
              <DropdownMenuItem>Work</DropdownMenuItem>
              <DropdownMenuItem>Personal</DropdownMenuItem>
            </DropdownMenuSubContent>
          </DropdownMenuSub>
        </DropdownMenuSubContent>
      </DropdownMenuSub>
    </DropdownMenuContent>
  </DropdownMenu>
</template>

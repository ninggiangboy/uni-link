<script setup lang="ts">
import { DialogClose, DialogContent as RekaDialogContent } from 'reka-ui'
import { X } from 'lucide-vue-next'
import { Button } from '@/ui/components/button'
import { cn } from '@/ui/lib/utils'
import DialogOverlay from './DialogOverlay.vue'
import DialogPortal from './DialogPortal.vue'

withDefaults(
  defineProps<{
    class?: string
    closeButton?: boolean
  }>(),
  { closeButton: true },
)
</script>

<template>
  <DialogPortal>
    <DialogOverlay />
    <RekaDialogContent
      v-bind="$attrs"
      :class="
        cn(
          'fixed left-[50vw] top-1/2 z-50 grid w-full max-w-[calc(100vw-40px)] -translate-x-1/2 -translate-y-1/2 gap-4 rounded-xl bg-background p-5 shadow-2xl md:max-w-lg dark:border',
          'data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=open]:fade-in-0 data-[state=closed]:fade-out-0',
          'md:data-[state=open]:zoom-in-95 md:data-[state=closed]:zoom-out-95',
          'max-md:data-[state=closed]:duration-300 max-md:data-[state=open]:slide-in-from-bottom-5 max-md:data-[state=closed]:slide-out-to-bottom-5',
          $props.class,
        )
      "
    >
      <slot />
      <DialogClose v-if="closeButton" as-child>
        <Button variant="ghost" size="icon" class="absolute right-2.5 top-2.5">
          <X class="size-4! text-muted-foreground" />
          <span class="sr-only">Close</span>
        </Button>
      </DialogClose>
    </RekaDialogContent>
  </DialogPortal>
</template>

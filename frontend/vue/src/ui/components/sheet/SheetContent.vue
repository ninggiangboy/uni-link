<script setup lang="ts">
import { DialogClose, DialogContent as RekaDialogContent } from 'reka-ui'
import { X } from 'lucide-vue-next'
import { computed } from 'vue'
import { Button } from '@/ui/components/button'
import DialogOverlay from '@/ui/components/dialog/DialogOverlay.vue'
import DialogPortal from '@/ui/components/dialog/DialogPortal.vue'
import { cn } from '@/ui/lib/utils'

const props = withDefaults(
  defineProps<{
    class?: string
    closeButton?: boolean
    side?: 'top' | 'right' | 'bottom' | 'left'
  }>(),
  { closeButton: true, side: 'right' },
)

const sideClass = computed(() => {
  switch (props.side) {
    case 'top':
      return 'inset-x-0 top-0 border-b data-[state=closed]:slide-out-to-top data-[state=open]:slide-in-from-top rounded-b-lg'
    case 'bottom':
      return 'inset-x-0 bottom-0 border-t data-[state=closed]:slide-out-to-bottom data-[state=open]:slide-in-from-bottom rounded-t-lg'
    case 'left':
      return 'inset-y-0 left-0 h-full w-3/4 border-r max-w-sm data-[state=closed]:slide-out-to-left data-[state=open]:slide-in-from-left sm:max-w-sm'
    default:
      return 'inset-y-0 right-0 h-full w-3/4 border-l max-w-sm data-[state=closed]:slide-out-to-right data-[state=open]:slide-in-from-right sm:max-w-sm'
  }
})
</script>

<template>
  <DialogPortal>
    <DialogOverlay class="duration-300" />
    <RekaDialogContent
      v-bind="$attrs"
      :class="
        cn(
          'fixed z-50 flex flex-col gap-4 bg-background p-6 shadow-lg transition ease-in-out duration-300',
          'data-[state=open]:animate-in data-[state=closed]:animate-out',
          sideClass,
          props.class,
        )
      "
    >
      <slot />
      <DialogClose v-if="closeButton" as-child>
        <Button variant="ghost" size="icon" class="absolute right-4 top-4">
          <X class="size-4" />
          <span class="sr-only">Close</span>
        </Button>
      </DialogClose>
    </RekaDialogContent>
  </DialogPortal>
</template>

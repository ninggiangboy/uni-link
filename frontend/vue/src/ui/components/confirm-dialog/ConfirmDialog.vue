<script setup lang="ts">
import { CircleX, Info } from 'lucide-vue-next'
import { Button } from '@/ui/components/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/ui/components/dialog'
import { closeConfirm, confirmDialogData, confirmDialogOpen } from '@/ui/lib/confirmDialog'

function handleCancel() {
  confirmDialogData.value?.cancel?.onClick?.()
  closeConfirm()
}

async function handleAction() {
  await confirmDialogData.value?.action?.onClick?.()
  closeConfirm()
}
</script>

<template>
  <Dialog v-model:open="confirmDialogOpen">
    <DialogContent v-if="confirmDialogData" class="md:max-w-[425px]" :close-button="false">
      <div class="flex flex-col gap-4">
        <DialogHeader>
          <div class="mb-2">
            <Info
              v-if="confirmDialogData.variant !== 'destructive'"
              class="size-6 text-blue-500 dark:text-blue-400"
            />
            <CircleX v-else class="size-6 text-destructive-foreground" />
          </div>
          <DialogTitle>{{ confirmDialogData.title || 'Confirm' }}</DialogTitle>
          <DialogDescription>
            {{ confirmDialogData.description ?? 'Are you sure you want to confirm?' }}
          </DialogDescription>
        </DialogHeader>
        <DialogFooter>
          <Button variant="outline" @click="handleCancel">
            {{ confirmDialogData.cancel?.label ?? 'Cancel' }}
          </Button>
          <Button
            :variant="confirmDialogData.variant === 'destructive' ? 'destructive' : 'default'"
            @click="handleAction"
          >
            {{ confirmDialogData.action?.label ?? 'Confirm' }}
          </Button>
        </DialogFooter>
      </div>
    </DialogContent>
  </Dialog>
</template>

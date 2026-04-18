<script setup lang="ts">
import ConfirmDialog from '@/ui/components/confirm-dialog/ConfirmDialog.vue'
import Toaster from '@/ui/components/sonner/Toaster.vue'
import TooltipProvider from '@/ui/components/tooltip/TooltipProvider.vue'
</script>

<template>
  <TooltipProvider>
    <slot />
    <Toaster />
    <ConfirmDialog />
  </TooltipProvider>
</template>

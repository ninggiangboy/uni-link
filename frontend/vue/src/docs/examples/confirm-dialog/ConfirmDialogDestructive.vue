<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { confirm } from '@/ui/components/confirm-dialog'
import { toast } from '@/ui/components/sonner'

function handleArchive() {
  confirm({
    variant: 'destructive',
    title: 'Archive Project',
    description: 'You can restore an archived project later from settings.',
    action: {
      label: 'Archive',
      onClick: () => {
        toast.success('Project Archived', {
          description: 'Project archived successfully',
        })
      },
    },
  })
}
</script>

<template>
  <Button variant="outline" @click="handleArchive"> Archive Project </Button>
</template>

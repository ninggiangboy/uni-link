<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { confirm } from '@/ui/components/confirm-dialog'
import { toast } from '@/ui/components/sonner'

function handleDeleteUsers() {
  confirm({
    title: 'Delete Users',
    description: 'Are you sure you want to delete these users?',
    action: {
      label: 'Delete',
      onClick: () => {
        toast.success('Users Deleted', {
          description: 'Users deleted successfully',
        })
      },
    },
  })
}
</script>

<template>
  <Button variant="outline" @click="handleDeleteUsers"> Delete Users </Button>
</template>

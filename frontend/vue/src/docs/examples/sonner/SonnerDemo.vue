<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { toast } from '@/ui/components/sonner'
</script>

<template>
  <div class="grid max-w-2xl grid-cols-2 gap-2 sm:grid-cols-3">
    <Button
      variant="outline"
      @click="
        toast.success({
          title: 'Welcome aboard! 🎉',
          description: 'Account created successfully.',
        })
      "
    >
      Success
    </Button>
    <Button
      variant="outline"
      @click="
        toast.error({
          title: 'Oops! Something went wrong',
          description: 'Please try again.',
        })
      "
    >
      Error
    </Button>
    <Button
      variant="outline"
      @click="
        toast.info({
          title: 'New feature available!',
          description: 'Check the dashboard for updates.',
        })
      "
    >
      Info
    </Button>
    <Button
      variant="outline"
      @click="
        toast.warning({
          title: 'Storage space running low',
          description: '85% storage used.',
        })
      "
    >
      Warning
    </Button>
    <Button
      variant="outline"
      @click="
        toast.neutral({
          title: 'System maintenance scheduled',
          description: '2 hours downtime tomorrow at 2 AM.',
        })
      "
    >
      Neutral
    </Button>
  </div>
</template>

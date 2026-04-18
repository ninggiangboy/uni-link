<script setup lang="ts">
import { Skeleton } from '@/ui/components/skeleton'
</script>

<template>
  <div class="w-full">
    <div class="mb-8 space-y-3">
      <Skeleton class="h-7 w-5/12" />
      <Skeleton class="h-7 w-7/12" />
    </div>
    <div class="mb-6 space-y-2">
      <Skeleton class="h-4 w-10/12" />
      <Skeleton class="h-4 w-full" />
      <Skeleton class="h-4 w-9/12" />
    </div>
    <div class="mb-6 space-y-2">
      <Skeleton class="h-4 w-full" />
      <Skeleton class="h-4 w-8/12" />
    </div>
  </div>
</template>

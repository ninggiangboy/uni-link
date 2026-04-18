<script setup lang="ts">
import { Pagination, PaginationPageSizeSelector } from '@/ui/components/pagination'
</script>

<template>
  <div class="flex w-full items-center justify-between gap-2 rounded-lg border p-4">
    <PaginationPageSizeSelector />
    <Pagination :page-count="10" />
  </div>
</template>

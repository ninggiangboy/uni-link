<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Pagination } from '@/ui/components/pagination'

const route = useRoute()
const router = useRouter()
const pageCount = 10

const page = computed({
  get: () => {
    const n = Number(route.query.page ?? 1)
    return Number.isFinite(n) && n >= 1 ? Math.min(n, pageCount) : 1
  },
  set: (p: number) => {
    void router.replace({ query: { ...route.query, page: String(p) } })
  },
})
</script>

<template>
  <div class="flex w-full flex-col items-center gap-2 rounded-lg border p-4">
    <Pagination v-model="page" :page-count="pageCount" />
    <p class="text-xs text-muted-foreground tabular-nums">
      Query: <code class="rounded bg-muted px-1 py-0.5">page={{ page }}</code>
    </p>
  </div>
</template>

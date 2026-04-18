<script setup lang="ts">
import { ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { computed, watch } from 'vue'
import { useIsMobile } from '@/ui/composables/useIsMobile'
import { getPaginationItems } from './paginationModel'
import { cn } from '@/ui/lib/utils'

const baseClass =
  'cursor-pointer select-none text-[13px] font-medium flex w-8 h-8 items-center justify-center rounded-sm hover:bg-background-secondary'

const props = defineProps<{
  /** Total number of pages. */
  pageCount: number
  class?: string
}>()

/** Current page, 1-based (v-model). */
const page = defineModel<number>({ default: 1 })

const { isMobile } = useIsMobile()

const pageItems = computed(() =>
  getPaginationItems(
    page.value,
    props.pageCount,
    isMobile.value ? 1 : 2,
    1,
  ),
)

watch(
  () => props.pageCount,
  (c) => {
    if (c >= 1 && page.value > c) page.value = c
  },
)

function clampPage(p: number, count: number) {
  if (count < 1) return 1
  return Math.min(Math.max(1, p), count)
}

function goTo(p: number) {
  page.value = clampPage(p, props.pageCount)
}
</script>

<template>
  <nav
    :class="cn('flex items-center justify-center gap-1', props.class)"
    aria-label="Pagination"
  >
    <button
      type="button"
      :class="cn(baseClass, page <= 1 && 'opacity-50 cursor-not-allowed! hover:bg-transparent!')"
      :disabled="page <= 1 || pageCount < 1"
      aria-label="Previous page"
      @click="goTo(page - 1)"
    >
      <ChevronLeft class="size-4" />
    </button>

    <template v-for="(item, i) in pageItems" :key="item === 'ellipsis' ? `e-${i}` : item">
      <span
        v-if="item === 'ellipsis'"
        class="block"
        :class="cn(baseClass, 'pointer-events-none cursor-default hover:bg-transparent')"
        aria-hidden="true"
      >
        ...
      </span>
      <button
        v-else
        type="button"
        :class="cn(baseClass, item === page && 'bg-background-secondary shadow-sm border  text-foreground')"
        :aria-current="item === page ? 'page' : undefined"
        :aria-label="`Page ${item}`"
        @click="goTo(item)"
      >
        {{ item }}
      </button>
    </template>

    <button
      type="button"
      :class="
        cn(
          baseClass,
          (page >= pageCount || pageCount < 1) && 'opacity-50 cursor-not-allowed! hover:bg-transparent!',
        )
      "
      :disabled="page >= pageCount || pageCount < 1"
      aria-label="Next page"
      @click="goTo(page + 1)"
    >
      <ChevronRight class="size-4" />
    </button>
  </nav>
</template>

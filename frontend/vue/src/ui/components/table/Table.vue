<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { onMounted, ref, useAttrs } from 'vue'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

const props = defineProps<{
  class?: HTMLAttributes['class']
  containerClassName?: string
}>()

const attrs = useAttrs()
const tableContainerRef = ref<HTMLDivElement | null>(null)
const tableRef = ref<HTMLTableElement | null>(null)

function calculateScrollPosition() {
  const container = tableContainerRef.value
  const table = tableRef.value
  if (!container || !table) return
  const { scrollLeft, scrollWidth, clientWidth } = container
  const isAtHorizontalStart = scrollLeft === 0
  const isAtHorizontalEnd = Math.abs(scrollLeft + clientWidth - scrollWidth) < 1
  table.setAttribute('data-at-start', String(isAtHorizontalStart))
  table.setAttribute('data-at-end', String(isAtHorizontalEnd))
}

onMounted(() => calculateScrollPosition())
</script>

<template>
  <div class="relative grid bg-background-secondary rounded-md overflow-hidden border">
    <div
      ref="tableContainerRef"
      data-slot="table-container"
      :class="cn('w-full overflow-x-auto', props.containerClassName)"
      @scroll="calculateScrollPosition"
    >
      <table
        ref="tableRef"
        data-slot="table"
        :class="cn('group w-full caption-bottom text-sm', props.class)"
        v-bind="attrs"
      >
        <slot />
      </table>
    </div>
  </div>
</template>

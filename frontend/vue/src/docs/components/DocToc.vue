<script setup lang="ts">
import type { TocItem } from '@/docs/mdx/renderDoc'
import { cn } from '@/ui/lib/utils'
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'

const props = defineProps<{
  toc: TocItem[]
}>()

const activeId = ref<string | null>(null)
let observer: IntersectionObserver | null = null

const ids = computed(() => props.toc.map((t) => t.id))

function rebuildObserver() {
  observer?.disconnect()
  activeId.value = null
  if (!props.toc.length) return

  observer = new IntersectionObserver(
    (entries) => {
      for (const entry of entries) {
        if (entry.isIntersecting) {
          activeId.value = entry.target.id
        }
      }
    },
    { rootMargin: '0% 0% -80% 0%' },
  )

  for (const id of ids.value) {
    const el = document.getElementById(id)
    if (el) observer.observe(el)
  }
}

onMounted(rebuildObserver)
watch(ids, rebuildObserver, { deep: true })

onUnmounted(() => {
  observer?.disconnect()
})
</script>

<template>
  <div v-if="toc.length" class="docs-toc-inner pt-4 px-4">
    <h3 class="text-sm mb-2 font-semibold text-foreground">Table of contents</h3>
    <ul class="list-none space-y-0.5">
      <li v-for="item in toc" :key="item.id">
        <a
          :href="`#${item.id}`"
          :class="
            cn(
              'flex text-[13px] items-center min-h-7 py-0.5 text-muted-foreground border-l border-transparent transition-colors',
              item.level === 3 ? 'pl-4' : 'pl-2',
              activeId === item.id
                ? 'text-foreground border-foreground'
                : 'hover:text-foreground hover:border-foreground/20',
            )
          "
        >
          <span class="truncate pr-4">{{ item.text }}</span>
        </a>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { BookOpen, ChevronDown, Layers2 } from 'lucide-vue-next'
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import { cn } from '@/ui/lib/utils'
import { Button } from '@/ui/components/button'

export type ModuleValue = 'guide' | 'ui'

const route = useRoute()
const open = ref(false)
const root = ref<HTMLElement | null>(null)

const selectedModule = computed<ModuleValue>(() =>
  route.path.includes('/docs/ui') ? 'ui' : 'guide',
)

const modules: {
  value: ModuleValue
  label: string
  description: string
  icon: typeof BookOpen
  className: string
  href: string
}[] = [
  {
    value: 'guide',
    label: 'Guide',
    icon: BookOpen,
    description: 'Overview and guide',
    className: 'text-blue-500 bg-blue-500/10 border-blue-500/20',
    href: '/docs/guide/introduction',
  },
  {
    value: 'ui',
    label: 'Components',
    icon: Layers2,
    description: 'Re-usable components',
    className: 'text-green-500 bg-green-500/10 border-green-500/20',
    href: '/docs/ui/button',
  },
]

const current = computed(() => modules.find((m) => m.value === selectedModule.value)!)

function onDocClick(e: MouseEvent) {
  if (root.value && !root.value.contains(e.target as Node)) {
    open.value = false
  }
}

onMounted(() => document.addEventListener('click', onDocClick))
onUnmounted(() => document.removeEventListener('click', onDocClick))
</script>

<template>
  <div ref="root" class="pt-6 pb-2 px-6 z-2 relative">
    <Button
      variant="unstyled"
      class="text-start w-full transition-colors flex items-center gap-2 p-0 h-auto"
      @click.stop="open = !open"
    >
      <span
        :class="
          cn('inline-flex size-7 items-center justify-center rounded-md border', current.className)
        "
      >
        <component :is="current.icon" class="size-4" />
      </span>
      <span class="flex flex-col items-start min-w-0">
        <span class="text-sm font-medium leading-tight">{{ current.label }}</span>
        <span class="text-xs text-muted-foreground truncate max-w-45">{{
          current.description
        }}</span>
      </span>
      <ChevronDown class="ml-auto w-4 h-4 text-neutral-500 shrink-0" />
    </Button>

    <div
      v-if="open"
      class="absolute left-6 right-6 mt-2 rounded-xl border border-border bg-popover shadow-popover p-1 z-50"
      @click.stop
    >
      <RouterLink
        v-for="item in modules"
        :key="item.value"
        :to="item.href"
        :class="
          cn(
            'text-start w-full flex items-center gap-2 p-2 rounded-md hover:bg-neutral-400/5 no-underline text-foreground',
            item.value === selectedModule && 'bg-neutral-400/10',
          )
        "
        @click="open = false"
      >
        <span
          :class="
            cn('inline-flex size-7 items-center justify-center rounded-md border', item.className)
          "
        >
          <component :is="item.icon" class="size-4" />
        </span>
        <span class="flex flex-col items-start min-w-0">
          <span class="text-sm font-medium leading-tight">{{ item.label }}</span>
          <span class="text-xs text-muted-foreground">{{ item.description }}</span>
        </span>
      </RouterLink>
    </div>
  </div>
</template>

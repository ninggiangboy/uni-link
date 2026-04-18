<script setup lang="ts">
import { cn } from '@/ui/lib/utils'
import { RouterLink, useRoute } from 'vue-router'

const route = useRoute()

const navLinks = [
  { label: 'Guide', href: '/docs/guide/introduction' },
  { label: 'Components', href: '/docs/ui/button' },
]

function isActive(href: string) {
  if (href.includes('/guide')) return route.path.includes('/docs/guide')
  return route.path.includes('/docs/ui')
}
</script>

<template>
  <div class="flex items-center gap-5">
    <RouterLink
      v-for="link in navLinks"
      :key="link.href"
      :to="link.href"
      :class="
        cn(
          'text-sm px-0 py-1 border-0 bg-transparent cursor-pointer text-foreground no-underline opacity-75 hover:opacity-100',
          isActive(link.href) && 'opacity-100',
        )
      "
    >
      {{ link.label }}
    </RouterLink>
  </div>
</template>

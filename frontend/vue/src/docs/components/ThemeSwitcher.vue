<script setup lang="ts">
import { Moon, Sun } from 'lucide-vue-next'
import { Button } from '@/ui/components/button'
import { onMounted, ref } from 'vue'

const dark = ref(false)

function applyTheme(next: boolean) {
  dark.value = next
  document.documentElement.classList.toggle('dark', next)
  try {
    localStorage.setItem('theme', next ? 'dark' : 'light')
  } catch {
    /* ignore */
  }
}

function toggle() {
  applyTheme(!dark.value)
}

onMounted(() => {
  try {
    const stored = localStorage.getItem('theme')
    if (stored === 'dark' || stored === 'light') {
      applyTheme(stored === 'dark')
      return
    }
  } catch {
    /* ignore */
  }
  applyTheme(document.documentElement.classList.contains('dark'))
})
</script>

<template>
  <Button variant="ghost" size="icon" aria-label="Toggle theme" @click="toggle">
    <Sun v-if="dark" class="size-[18px]" />
    <Moon v-else class="size-[18px]" />
  </Button>
</template>

<script setup lang="ts">
import ThemeSwitcher from '@/docs/components/ThemeSwitcher.vue'
import { Button } from '@/ui/components/button'
import { Github } from 'lucide-vue-next'

const GITHUB_URL = 'https://github.com'
</script>

<template>
  <div class="flex items-center gap-1">
    <Button
      variant="ghost"
      size="icon"
      as="a"
      :href="GITHUB_URL"
      target="_blank"
      rel="noopener noreferrer"
    >
      <Github class="size-[18px]" />
    </Button>
    <ThemeSwitcher />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Upload } from 'lucide-vue-next'
import { Button } from '@/ui/components/button'
import { shortenFilename } from '@/ui/lib/file'

const file = ref('')

function onPick(e: Event) {
  const t = e.target as HTMLInputElement
  file.value = t.files?.[0]?.name ?? ''
  t.value = ''
}
</script>

<template>
  <div class="flex items-center gap-2 text-sm">
    <label class="inline-flex cursor-pointer">
      <input type="file" class="sr-only" @change="onPick" />
      <Button variant="outline" type="button" as="span">
        <Upload class="size-4" />
        Select a file
      </Button>
    </label>
    <span v-if="file">{{ shortenFilename(file) }}</span>
  </div>
</template>

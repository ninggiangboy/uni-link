<script setup lang="ts">
import { computed, ref } from 'vue'
import { Upload } from 'lucide-vue-next'
import { Avatar, AvatarFallback, AvatarImage } from '@/ui/components/avatar'
import { Button } from '@/ui/components/button'

const picked = ref<File | null>(null)
const previewUrl = computed(() => (picked.value ? URL.createObjectURL(picked.value) : null))

function onPick(e: Event) {
  const t = e.target as HTMLInputElement
  const f = t.files?.[0]
  picked.value = f ?? null
  t.value = ''
}
</script>

<template>
  <div class="flex items-center gap-3 text-sm">
    <Avatar class="size-20">
      <AvatarImage v-if="previewUrl" :src="previewUrl" alt="avatar" />
      <AvatarFallback class="text-lg">CN</AvatarFallback>
    </Avatar>
    <label class="inline-flex cursor-pointer">
      <input type="file" accept=".png,.jpg,.jpeg" class="sr-only" @change="onPick" />
      <Button variant="outline" type="button" as="span">
        <Upload class="size-4" />
        Select a file
      </Button>
    </label>
  </div>
</template>

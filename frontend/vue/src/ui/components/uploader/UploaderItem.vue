<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { Progress } from '@/ui/components/progress'
import { UploaderIcon } from '@/ui/components/uploader-icon'
import { shortenFilename } from '@/ui/lib/file'
import { cn } from '@/ui/lib/utils'
import type { UploaderFile } from './uploaderTypes'

defineProps<{
  fileState: UploaderFile
  class?: string
  /** When true, remove control is hidden (e.g. read-only / disabled uploader) */
  isDisabled?: boolean
}>()

const emit = defineEmits<{
  remove: []
}>()
</script>

<template>
  <div :class="cn('flex items-center gap-3 rounded-lg border bg-card px-3 py-2', $props.class)">
    <UploaderIcon :extension="fileState.extension ?? 'txt'" />
    <div class="min-w-0 flex-1">
      <div class="truncate text-sm font-medium">
        {{ shortenFilename(fileState.name ?? 'file') }}
      </div>
      <Progress
        v-if="fileState.status === 'uploading' && fileState.progress != null"
        :model-value="fileState.progress"
        class="mt-1"
      />
      <p v-if="fileState.error" class="text-xs text-destructive mt-1">
        {{ fileState.error }}
      </p>
    </div>
    <Button
      v-if="!$props.isDisabled"
      type="button"
      variant="ghost"
      size="iconSm"
      @click="emit('remove')"
    >
      ×
    </Button>
  </div>
</template>

<script setup lang="ts">
import axios, { type AxiosRequestConfig } from 'axios'
import { ref, watch } from 'vue'
import { toast } from '@/ui/components/sonner'
import { Dropzone } from '@/ui/components/dropzone'
import { UploaderTrigger } from '@/ui/components/uploader-trigger'
import { cn } from '@/ui/lib/utils'
import UploaderItem from './UploaderItem.vue'
import { UploaderAction } from './uploaderAction'
import type { UploaderFile } from './uploaderTypes'
import { useValidateFiles, type UploaderRules } from './useValidateFiles'

const props = withDefaults(
  defineProps<{
    action: UploaderAction
    defaultFileList?: UploaderFile[]
    listType?: 'list' | 'card'
    triggerType?: 'dropzone' | 'button'
    acceptedFileExtensions?: UploaderRules['acceptedFileExtensions']
    maxFiles?: number
    maxFileSize?: number
    allowMultiple?: boolean
    isDisabled?: boolean
    class?: string
    'aria-invalid'?: boolean
  }>(),
  {
    listType: 'list',
    triggerType: 'dropzone',
    maxFiles: 20,
    allowMultiple: true,
    isDisabled: false,
  },
)

const emit = defineEmits<{
  fileListChange: [files: UploaderFile[]]
}>()

const files = ref<UploaderFile[]>([...(props.defaultFileList ?? [])])

watch(
  () => props.defaultFileList,
  (v) => {
    if (v) files.value = [...v]
  },
  { deep: true },
)

function getValidate() {
  return useValidateFiles({
    acceptedFileExtensions: props.acceptedFileExtensions,
    maxFileSize: props.maxFileSize,
    maxFiles: props.maxFiles,
    allowMultiple: props.allowMultiple ?? true,
  })
}

async function addFiles(list: FileList | null) {
  if (!list?.length || props.isDisabled) return
  const validate = getValidate()
  const ok = validate(Array.from(list))
  for (const file of ok) {
    const state: UploaderFile = {
      file,
      name: file.name,
      size: file.size,
      extension: file.name.split('.').pop() ?? 'txt',
      status: 'uploading',
      progress: 0,
    }
    files.value = [...files.value, state]
    const idx = files.value.length - 1
    try {
      const cfg = props.action.buildRequest(files.value[idx]!)
      const res = await axios.request({
        ...cfg,
        onUploadProgress: (e) => {
          if (e.total) {
            const p = Math.round((e.loaded / e.total) * 100)
            const copy = [...files.value]
            copy[idx] = { ...copy[idx]!, progress: p }
            files.value = copy
          }
        },
      } as AxiosRequestConfig)
      const copy = [...files.value]
      copy[idx] = {
        ...copy[idx]!,
        ...props.action.formatResponse(res),
        status: 'success',
        progress: 100,
      }
      files.value = copy
    } catch (e) {
      const copy = [...files.value]
      copy[idx] = {
        ...copy[idx]!,
        status: 'error',
        error: props.action.formatResponseError(e),
      }
      files.value = copy
      toast.error(copy[idx]!.error ?? 'Upload failed')
    }
    emit('fileListChange', files.value)
  }
}

function removeAt(i: number) {
  if (props.isDisabled) return
  files.value = files.value.filter((_, j) => j !== i)
  emit('fileListChange', files.value)
}
</script>

<template>
  <div :class="cn('grid gap-3', props.class)" :aria-invalid="props['aria-invalid']">
    <Dropzone v-if="triggerType === 'dropzone'" :disabled="isDisabled" @drop-files="addFiles">
      <span class="text-muted-foreground text-sm">Drop files or use the trigger</span>
      <UploaderTrigger
        class="mt-2"
        :disabled="isDisabled"
        :multiple="allowMultiple"
        @change="addFiles"
      />
    </Dropzone>
    <UploaderTrigger v-else :disabled="isDisabled" :multiple="allowMultiple" @change="addFiles" />
    <div
      v-if="files.length"
      :class="listType === 'card' ? 'grid grid-cols-2 gap-2' : 'flex flex-col gap-2'"
    >
      <UploaderItem
        v-for="(f, i) in files"
        :key="f.id ?? String(i)"
        :file-state="f"
        :is-disabled="isDisabled"
        @remove="removeAt(i)"
      />
    </div>
  </div>
</template>

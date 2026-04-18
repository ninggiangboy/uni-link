<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { cn } from '@/ui/lib/utils'
import { formatFileSize } from '@/ui/lib/file'

defineOptions({ inheritAttrs: false })

const props = withDefaults(
  defineProps<{
    class?: string
    accept?: string
    multiple?: boolean
    /** Max total bytes */
    maxSize?: number
    disabled?: boolean
  }>(),
  { multiple: true },
)

const emit = defineEmits<{
  change: [files: FileList | null]
}>()

function onChange(e: Event) {
  const input = e.target as HTMLInputElement
  emit('change', input.files)
  input.value = ''
}
</script>

<template>
  <label
    :class="
      cn('inline-flex', props.disabled ? 'cursor-not-allowed' : 'cursor-pointer', props.class)
    "
  >
    <input
      type="file"
      class="sr-only"
      :disabled="props.disabled"
      :accept="accept"
      :multiple="multiple"
      v-bind="$attrs"
      @change="onChange"
    />
    <Button
      type="button"
      as="span"
      variant="outline"
      :class="cn(props.disabled && 'pointer-events-none opacity-60')"
    >
      <slot>Select files</slot>
      <span v-if="maxSize" class="text-xs text-muted-foreground ml-1">
        (max {{ formatFileSize(maxSize) }})
      </span>
    </Button>
  </label>
</template>

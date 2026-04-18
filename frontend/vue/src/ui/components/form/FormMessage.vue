<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { computed } from 'vue'
import { cn } from '@/ui/lib/utils'
import { useFormField } from './useFormField'

defineOptions({ inheritAttrs: false })

const props = defineProps<{
  class?: HTMLAttributes['class']
}>()

const { errorMessage, errors, formMessageId } = useFormField()

const body = computed(() => errorMessage.value ?? errors.value?.[0])
</script>

<template>
  <p
    v-if="body"
    :id="formMessageId"
    data-slot="form-message"
    :class="cn('text-sm font-medium text-destructive', props.class)"
  >
    {{ body }}
  </p>
</template>

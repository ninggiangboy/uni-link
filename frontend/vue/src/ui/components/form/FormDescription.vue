<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { inject, onMounted } from 'vue'
import { cn } from '@/ui/lib/utils'
import { FORM_DESCRIPTION_ACTIVE_KEY } from './injectionKeys'
import { useFormField } from './useFormField'

defineOptions({ inheritAttrs: false })

const props = defineProps<{
  class?: HTMLAttributes['class']
}>()

const { formDescriptionId } = useFormField()
const descriptionActive = inject(FORM_DESCRIPTION_ACTIVE_KEY, null)

onMounted(() => {
  if (descriptionActive) {
    descriptionActive.value = true
  }
})
</script>

<template>
  <p
    :id="formDescriptionId"
    data-slot="form-description"
    :class="cn('text-sm text-muted-foreground', props.class)"
  >
    <slot />
  </p>
</template>

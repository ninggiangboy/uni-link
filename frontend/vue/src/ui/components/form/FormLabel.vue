<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { computed } from 'vue'
import { Label } from '@/ui/components/field'
import { cn } from '@/ui/lib/utils'
import { useFormField } from './useFormField'

defineOptions({ inheritAttrs: false })

const props = defineProps<{
  class?: HTMLAttributes['class']
}>()

const { id, errorMessage } = useFormField()
const invalid = computed(() => !!errorMessage.value)
</script>

<template>
  <Label data-slot="form-label" :for="id" :class="cn(invalid && 'text-destructive', props.class)">
    <slot />
  </Label>
</template>

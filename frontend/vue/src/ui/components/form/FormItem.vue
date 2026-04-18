<script setup lang="ts">
import type { HTMLAttributes } from 'vue'
import { provide, ref, useId } from 'vue'
import { cn } from '@/ui/lib/utils'
import { FORM_DESCRIPTION_ACTIVE_KEY, FORM_ITEM_INJECTION_KEY } from './injectionKeys'

defineOptions({ inheritAttrs: false })

defineProps<{
  class?: HTMLAttributes['class']
}>()

const baseId = useId()
provide(FORM_ITEM_INJECTION_KEY, { id: baseId })

const descriptionActive = ref(false)
provide(FORM_DESCRIPTION_ACTIVE_KEY, descriptionActive)
</script>

<template>
  <div data-slot="form-item" :class="cn('space-y-2', $props.class)">
    <slot />
  </div>
</template>

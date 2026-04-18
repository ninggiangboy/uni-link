<script setup lang="ts">
import { Loader2 } from 'lucide-vue-next'
import type { HTMLAttributes } from 'vue'
import { computed, useAttrs } from 'vue'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

const props = defineProps<{
  class?: HTMLAttributes['class']
}>()

const attrs = useAttrs()

const mergedClass = computed(() => cn('animate-spin', props.class, attrs.class as string))

const passthrough = computed(() => {
  const rest = { ...attrs } as Record<string, unknown>
  delete rest.class
  return rest
})
</script>

<template>
  <Loader2 role="status" aria-label="Loading" :class="mergedClass" v-bind="passthrough" />
</template>

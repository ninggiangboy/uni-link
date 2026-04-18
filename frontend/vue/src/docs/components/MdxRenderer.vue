<script setup lang="ts">
import type { DocSegment } from '@/docs/mdx/renderDoc'
import ComponentPreview from './ComponentPreview.vue'

defineProps<{
  segments: DocSegment[]
}>()
</script>

<template>
  <div class="docs-prose">
    <template v-for="(seg, i) in segments" :key="i">
      <!-- eslint-disable-next-line vue/no-v-html -->
      <div v-if="seg.type === 'markdown'" class="docs-md" v-html="seg.html" />
      <ComponentPreview
        v-else-if="seg.type === 'component-preview'"
        :name="seg.name"
        :class="seg.previewClass"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { cn } from '@/ui/lib/utils'

const iconColors: Record<string, string> = {
  pdf: '#C02626',
  csv: '#15803D',
  xlsx: '#15803D',
  xls: '#15803D',
  docx: '#1E40AF',
  doc: '#1E40AF',
  pptx: '#C2410C',
  ppt: '#C2410C',
  txt: '#525252',
  json: '#B45309',
  jpg: '#EA580C',
  jpeg: '#EA580C',
  png: '#0284C7',
  gif: '#7E22CE',
  svg: '#0369A1',
  webp: '#166534',
  mp4: '#7C3AED',
  avi: '#BE123C',
  other: '#1D4ED8',
}

const props = withDefaults(
  defineProps<{
    extension?: string
    class?: string
  }>(),
  { extension: 'txt' },
)

const fill = computed(() => iconColors[props.extension] ?? '#1D4ED8')
const label = computed(() => props.extension.toUpperCase())
</script>

<template>
  <div :class="cn('relative w-fit h-fit', props.class)">
    <svg
      class="size-8"
      xmlns="http://www.w3.org/2000/svg"
      width="40"
      height="41"
      fill="none"
      viewBox="0 0 40 41"
    >
      <g clip-path="url(#clip0_uploader_icon)">
        <path
          stroke-width="1.5"
          class="stroke-neutral-300 dark:stroke-neutral-600"
          d="M9 1.453h17.044L34.25 8.99v27.713a3.25 3.25 0 0 1-3.25 3.25H9a3.25 3.25 0 0 1-3.25-3.25v-32A3.25 3.25 0 0 1 9 1.453Z"
        />
        <path
          class="fill-neutral-300 dark:fill-neutral-600"
          d="M25 1.703h8.5zm8.5 9.5H26a2 2 0 0 1-2-2h9.5zm-7.5 0a2 2 0 0 1-2-2v-7.5h2v9.5m7.5-9.5v8.5z"
          mask="url(#path-inside-uploader)"
        />
      </g>
      <defs>
        <clipPath id="clip0_uploader_icon">
          <path fill="#fff" d="M0 .703h40v40H0z" />
        </clipPath>
      </defs>
    </svg>
    <div
      class="absolute top-[40%] text-[7px] leading-[7px] font-bold rounded-xs px-0.5 py-0.5 text-white"
      :style="{ background: fill }"
    >
      {{ label }}
    </div>
  </div>
</template>

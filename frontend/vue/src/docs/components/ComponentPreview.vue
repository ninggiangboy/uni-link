<script setup lang="ts">
import { ChevronDown, ChevronUp } from 'lucide-vue-next'
import { computed, nextTick, onMounted, ref, shallowRef, watch } from 'vue'
import { getPreviewComponent, loadPreviewSource } from '@/docs/examples/previewRegistry'
import { getShikiHighlighter } from '@/docs/mdx/shikiHighlighter'
import { Button } from '@/ui/components/button'
import { cn } from '@/ui/lib/utils'

const props = withDefaults(
  defineProps<{
    name: string
    class?: string
    withPreview?: boolean
  }>(),
  {
    withPreview: true,
  },
)

const Preview = shallowRef<ReturnType<typeof getPreviewComponent>>(null)
const html = ref('')
const copyDone = ref(false)

const codeRoot = ref<HTMLElement | null>(null)
const codeExpanded = ref(false)
const needsToggle = ref(false)

/** Match sample `CodeCollapsible`: show expand when content taller than this (px). */
const COLLAPSE_AT_PX = 200

const rootClass = computed(() => cn('w-full mt-4 border-b border-border pb-8'))

async function highlightSource(code: string) {
  const highlighter = await getShikiHighlighter()
  return highlighter.codeToHtml(code, {
    lang: 'vue',
    themes: {
      light: 'github-light',
      dark: 'github-dark',
    },
    defaultColor: false,
  })
}

function measureCodeHeight() {
  const root = codeRoot.value
  const pre = root?.querySelector('pre')
  needsToggle.value = pre != null && pre.scrollHeight > COLLAPSE_AT_PX
  if (!needsToggle.value) codeExpanded.value = false
}

async function refresh() {
  copyDone.value = false
  const comp = getPreviewComponent(props.name)
  Preview.value = comp
  const source = await loadPreviewSource(props.name)
  html.value = source ? await highlightSource(source) : '<pre>Source not found</pre>'
}

watch(html, async () => {
  codeExpanded.value = false
  await nextTick()
  measureCodeHeight()
})

onMounted(refresh)
watch(() => props.name, refresh)

async function copy() {
  const source = await loadPreviewSource(props.name)
  if (!source) return
  await navigator.clipboard.writeText(source)
  copyDone.value = true
  setTimeout(() => {
    copyDone.value = false
  }, 2000)
}
</script>

<template>
  <div :class="rootClass">
    <div class="p-1.5 space-y-1.5 border border-border rounded-xl bg-background">
      <div
        v-if="withPreview"
        :class="
          cn(
            'p-5 min-h-25 docs-not-prose flex items-center justify-center mx-auto',
            props.class,
          )
        "
      >
        <component :is="Preview" v-if="Preview" />
        <span v-else class="text-sm text-destructive">Unknown preview: {{ name }}</span>
      </div>

      <div class="relative border border-border rounded-md overflow-hidden">
        <button
          type="button"
          class="absolute right-2 top-2 z-[3] text-xs px-2 py-1 rounded-md bg-background-secondary border border-border hover:bg-background-tertiary text-foreground"
          @click="copy"
        >
          {{ copyDone ? 'Copied' : 'Copy' }}
        </button>

        <div class="component-preview-code relative w-full bg-background-secondary text-[13px]">
          <div
            ref="codeRoot"
            :class="
              cn(
                'relative [&>pre]:my-0 [&>pre]:rounded-none [&>pre]:pb-8',
                needsToggle && !codeExpanded && 'max-h-[200px] overflow-hidden',
              )
            "
          >
            <!-- eslint-disable-next-line vue/no-v-html -->
            <div class="shiki-wrapper p-3" v-html="html" />
          </div>

          <template v-if="needsToggle">
            <div
              v-if="!codeExpanded"
              class="pointer-events-none absolute inset-x-0 bottom-0 z-[2] flex h-16 items-center justify-center bg-gradient-to-t from-background-secondary to-transparent"
            >
              <Button
                type="button"
                variant="ghost"
                size="sm"
                class="pointer-events-auto gap-1"
                @click="codeExpanded = true"
              >
                <ChevronDown class="size-4" />
                Expand code
              </Button>
            </div>
            <div
              v-else
              class="flex justify-center bg-background-secondary py-2"
            >
              <Button
                type="button"
                variant="ghost"
                size="sm"
                class="gap-1"
                @click="codeExpanded = false"
              >
                <ChevronUp class="size-4" />
                Collapse code
              </Button>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

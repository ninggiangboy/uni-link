<script setup lang="ts">
import DocToc from '@/docs/components/DocToc.vue'
import MdxRenderer from '@/docs/components/MdxRenderer.vue'
import DocsLayout from '@/docs/layouts/DocsLayout.vue'
import { loadDocSource } from '@/docs/lib/loadDocSource'
import { renderDoc, type ParsedDoc } from '@/docs/mdx/renderDoc'
import { ScrollArea } from '@/ui/components/scroll-area'
import { Button } from '@/ui/components/button'
import { ArrowUpRight, Github } from 'lucide-vue-next'
import { useQuery } from '@tanstack/vue-query'
import { computed, unref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const slug = computed(() => {
  const section = route.params.section as string
  const s = route.params.slug as string
  return `${section}/${s}`
})

const docQuery = useQuery(
  computed(() => ({
    queryKey: ['docs', slug.value] as const,
    queryFn: async (): Promise<ParsedDoc | null> => {
      const raw = await loadDocSource(slug.value)
      if (!raw) return null
      return renderDoc(raw)
    },
  })),
)

const doc = computed(() => unref(docQuery.data))
const isLoading = computed(() => unref(docQuery.isLoading))
const isError = computed(() => unref(docQuery.isError))

const loadErrorMessage = computed(() => {
  const err = unref(docQuery.error)
  return err instanceof Error ? err.message : 'Failed to load document.'
})
</script>

<template>
  <DocsLayout>
    <template #toc>
      <ScrollArea
        v-if="doc?.toc?.length"
        class="grid h-[calc(100vh-5rem)] w-full -translate-x-px"
        :show-horizontal-scrollbar="false"
      >
        <DocToc :toc="doc.toc" />
        <div class="h-10" />
      </ScrollArea>
    </template>

    <div class="docs-article px-5 py-8 lg:px-10 lg:py-10 min-h-[60vh]">
      <div v-if="isLoading" class="text-muted-foreground text-sm">Loading…</div>
      <div v-else-if="isError" class="text-destructive text-sm">
        {{ loadErrorMessage }}
      </div>
      <div v-else-if="doc === null" class="text-destructive text-sm">Document not found.</div>
      <template v-else-if="doc">
        <div class="mb-10 border-b border-border pb-10">
          <h1 class="mb-0 font-mono text-[40px] text-foreground scroll-mt-24">
            {{ doc.frontmatter.title ?? 'Untitled' }}
          </h1>
          <p
            v-if="doc.frontmatter.description"
            class="text-muted-foreground mt-2 mb-2 text-[15px] leading-relaxed max-w-3xl"
          >
            {{ doc.frontmatter.description }}
          </p>
          <div
            v-if="doc.frontmatter.originalDocs || doc.frontmatter.sourceCode"
            class="flex flex-wrap gap-2"
          >
            <Button
              v-if="doc.frontmatter.originalDocs"
              as="a"
              variant="outline"
              size="sm"
              :href="doc.frontmatter.originalDocs"
              target="_blank"
              rel="noopener noreferrer"
              class="gap-1"
            >
              Docs
              <ArrowUpRight class="size-4" />
            </Button>
            <Button
              v-if="doc.frontmatter.sourceCode"
              as="a"
              variant="outline"
              size="sm"
              :href="doc.frontmatter.sourceCode"
              target="_blank"
              rel="noopener noreferrer"
              class="gap-1"
            >
              Code
              <Github class="size-4" />
            </Button>
          </div>
        </div>
        <div class="w-full min-w-0">
          <MdxRenderer :segments="doc.segments" />
        </div>
      </template>
    </div>
  </DocsLayout>
</template>

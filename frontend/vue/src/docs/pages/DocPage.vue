<script setup lang="ts">
import DocToc from '@/docs/components/DocToc.vue';
import MdxRenderer from '@/docs/components/MdxRenderer.vue';
import DocsLayout from '@/docs/layouts/DocsLayout.vue';
import { loadDocSource } from '@/docs/lib/loadDocSource';
import { renderDoc, type ParsedDoc } from '@/docs/mdx/renderDoc';
import { ScrollArea } from '@/ui/components/scroll-area';
import { Button } from '@/ui/components/button';
import { ArrowUpRight, Github } from 'lucide-vue-next';
import { useQuery } from '@tanstack/vue-query';
import { computed, unref } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

const slug = computed(() => {
  const section = route.params.section as string;
  const s = route.params.slug as string;
  return `${section}/${s}`;
});

const docQuery = useQuery(
  computed(() => ({
    queryKey: ['docs', slug.value] as const,
    queryFn: async (): Promise<ParsedDoc | null> => {
      const raw = await loadDocSource(slug.value);
      if (!raw) return null;
      return renderDoc(raw);
    },
  })),
);

const doc = computed(() => unref(docQuery.data));
const isLoading = computed(() => unref(docQuery.isLoading));
const isError = computed(() => unref(docQuery.isError));

const loadErrorMessage = computed(() => {
  const err = unref(docQuery.error);
  return err instanceof Error ? err.message : 'Failed to load document.';
});
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

    <article
      class="prose dark:prose-invert prose-neutral mb-6 max-w-full min-w-0 w-full py-10
        prose-headings:font-semibold prose-h1:tracking-tight prose-h1:font-bold prose-headings:scroll-mt-20
        prose-h2:mb-0 prose-h3:mt-7! prose-h3:mb-0 prose-h4:mb-0 prose-h4:mt-3 prose-h5:mb-0 prose-h5:mt-3 prose-h6:mb-0 prose-h6:mt-3
        prose-blockquote:font-normal prose-blockquote:mx-5 prose-blockquote:px-4! lg:prose-blockquote:mx-10 prose-blockquote:mb-0
        prose-ul:list-inside prose-ol:list-inside prose-ul:mt-1.5 prose-ul:mb-0 prose-ol:mt-3 prose-ol:mb-0
        prose-li:mt-1 prose-li:mb-0
        prose-p:mt-3! prose-p:mb-0!
        prose-img:my-5
      "
    >
      <div v-if="isLoading" class="text-muted-foreground text-sm">Loading…</div>
      <div v-else-if="isError" class="text-destructive text-sm">
        {{ loadErrorMessage }}
      </div>
      <div v-else-if="doc === null" class="text-destructive text-sm">Document not found.</div>
      <template v-else-if="doc">
        <div class="mb-10 px-5 lg:px-10 border-b pb-10">
          <h1 class="mb-0 font-mono text-[40px]">
            {{ doc.frontmatter.title ?? 'Untitled' }}
          </h1>
          <p
            v-if="doc.frontmatter.description"
            class="text-muted-foreground not-prose mt-2 mb-2"
          >
            {{ doc.frontmatter.description }}
          </p>
          <div class="flex gap-2">
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
        <MdxRenderer :segments="doc.segments" />
      </template>
    </article>
  </DocsLayout>
</template>

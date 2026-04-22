<script setup lang="ts">
import type { Component } from 'vue';
import { computed } from 'vue';
import { RouterLink, useRoute } from 'vue-router';
import {
  Blocks,
  BookOpen,
  Brain,
  Code,
  ListChecks,
  Network,
  Settings,
  SquareTerminal,
} from 'lucide-vue-next';
import { ScrollArea } from '@/ui/components/scroll-area';
import { cn } from '@/ui/lib/utils';

const props = defineProps<{
  class?: string;
}>();

type MenuItem = {
  title: string;
  href: string;
  icon?: Component;
  /** YYYY-MM-DD — shows “new” dot when within the last week */
  createdAt?: string;
};

type MenuGroup = {
  title: string;
  items: MenuItem[];
};

const route = useRoute();

const selectedModule = computed(() => (route.path.includes('/docs/ui') ? 'ui' : 'guide'));

function isNew(createdAt?: string): boolean {
  if (!createdAt) return false;
  const t = new Date(`${createdAt}T12:00:00`).getTime();
  return t > Date.now() - 7 * 86400000;
}

function uiItem(title: string, slug: string, createdAt?: string): MenuItem {
  return { title, href: `/docs/ui/${slug}`, createdAt };
}

/** Parity with `frontend-sample` DocsLayout `getMenuGroups` (paths adapted for uni-link router). */
function getMenuGroups(module: 'guide' | 'ui'): MenuGroup[] {
  if (module === 'guide') {
    return [
      {
        title: 'Getting Started',
        items: [
          {
            title: 'Introduction',
            href: '/docs/guide/introduction',
            icon: BookOpen,
          },
          {
            title: 'Tech Stack',
            href: '/docs/guide/tech-stack',
            icon: Blocks,
          },
          {
            title: 'Installation',
            href: '/docs/guide/installation',
            icon: Settings,
          },
          {
            title: 'CLI Tool',
            href: '/docs/guide/cli',
            icon: SquareTerminal,
          },
          {
            title: 'Decisions on DX',
            href: '/docs/guide/decisions-on-dx',
            icon: ListChecks,
          },
          {
            title: 'Philosophy',
            href: '/docs/guide/philosophy',
            icon: Brain,
          },
        ],
      },
      {
        title: 'Recipes',
        items: [
          {
            title: 'React Folder Structure',
            href: '/docs/guide/react-folder-structure',
            icon: Network,
          },
          {
            title: 'Code Conventions',
            href: '/docs/guide/react-code-convention',
            icon: Code,
            createdAt: '2025-10-20',
          },
        ],
      },
      {
        title: 'Form Management',
        items: [
          { title: 'Overview', href: '/docs/guide/form-overview' },
          { title: 'Basic Form', href: '/docs/guide/form-basic' },
          { title: 'Form with Loading', href: '/docs/guide/form-with-loading' },
          { title: 'Validation Form', href: '/docs/guide/form-validation' },
          { title: 'Dependent Validation', href: '/docs/guide/form-dependant-validation' },
          { title: 'Conditional Fields', href: '/docs/guide/form-conditional-fields' },
          { title: 'Field Array', href: '/docs/guide/form-field-array' },
          { title: 'Submission Errors', href: '/docs/guide/form-submission-errors' },
          { title: 'Large Form', href: '/docs/guide/form-large' },
        ],
      },
      {
        title: 'Data Fetching',
        items: [
          { title: 'Overview', href: '/docs/guide/data-fetching-overview' },
          { title: 'Build an API Client', href: '/docs/guide/data-fetching-api-client' },
          {
            title: 'Data Invalidation Best Practices',
            href: '/docs/guide/data-fetching-invalidation',
          },
          { title: 'Enhancing User Experience', href: '/docs/guide/data-fetching-enhance-ux' },
        ],
      },
    ];
  }

  if (module === 'ui') {
    return [
      {
        title: 'Layout',
        items: [uiItem('Sidebar', 'sidebar', '2025-10-22')],
      },
      {
        title: 'Display',
        items: [
          uiItem('Accordion', 'accordion'),
          uiItem('Avatar', 'avatar'),
          uiItem('Separator', 'separator'),
        ],
      },
      {
        title: 'Buttons',
        items: [uiItem('Button', 'button'), uiItem('FileTrigger', 'file-trigger')],
      },
      {
        title: 'Collections',
        items: [
          uiItem('Table', 'table'),
          uiItem('DataTable', 'data-table'),
          uiItem('Pagination', 'pagination'),
          uiItem('Searchfield', 'search-field'),
        ],
      },
      {
        title: 'Date and Time',
        items: [
          uiItem('Calendar', 'calendar'),
          uiItem('RangeCalendar', 'range-calendar'),
          uiItem('DatePicker', 'date-picker'),
          uiItem('DateRangePicker', 'date-range-picker'),
          uiItem('DateField', 'date-field'),
          uiItem('TimeField', 'time-field'),
        ],
      },
      {
        title: 'Form Fields',
        items: [
          uiItem('Input', 'input'),
          uiItem('Textarea', 'textarea'),
          uiItem('NumberField', 'number-field'),
          uiItem('Checkbox', 'checkbox'),
          uiItem('RadioGroup', 'radio-group'),
          uiItem('Switch', 'switch'),
          uiItem('Uploader', 'uploader'),
        ],
      },
      {
        title: 'Overlays',
        items: [
          uiItem('Dialog', 'dialog'),
          uiItem('Sheet', 'sheet', '2025-10-20'),
          uiItem('ConfirmDialog', 'confirm-dialog'),
          uiItem('Menu', 'menu'),
          uiItem('Popover', 'popover'),
          uiItem('Tooltip', 'tooltip'),
          uiItem('Select', 'select'),
        ],
      },
      {
        title: 'Feedback',
        items: [
          uiItem('Sonner', 'sonner'),
          uiItem('Spinner', 'spinner'),
          uiItem('LoadingOverlay', 'loading-overlay'),
          uiItem('NProgress', 'nprogress'),
          uiItem('Skeleton', 'skeleton'),
        ],
      },
    ];
  }

  return [];
}

const groups = computed(() => getMenuGroups(selectedModule.value));
</script>

<template>
  <div :class="cn('relative z-[1] h-full min-h-0', props.class)">
    <div
      class="pointer-events-none absolute top-0 right-6 left-0 z-[1] h-7 bg-gradient-to-b from-background to-transparent"
    />
    <div class="absolute inset-0 min-h-0">
      <ScrollArea class="h-full min-h-0 -translate-x-px" :show-horizontal-scrollbar="false">
        <div class="pb-24">
          <div class="h-6" />
          <div v-for="group in groups" :key="group.title" class="mb-6 space-y-1">
            <div v-if="group.title">
              <h3
                class="flex items-center px-6 text-xs tracking-wide text-muted-foreground/70 uppercase"
              >
                {{ group.title }}
              </h3>
            </div>
            <RouterLink
              v-for="item in group.items"
              :key="item.href"
              :to="item.href"
              :class="
                cn(
                  'flex h-8 items-center gap-2 border-l border-transparent px-[23px] text-sm text-muted-foreground transition-colors focus-visible:bg-background-secondary focus-visible:outline-none',
                  route.path === item.href
                    ? 'border-foreground font-medium text-foreground'
                    : 'hover:border-foreground/20 hover:text-foreground',
                )
              "
            >
              <component
                :is="item.icon"
                v-if="item.icon"
                class="size-4 shrink-0"
                :stroke-width="1.5"
              />
              {{ item.title }}
              <span
                v-if="isNew(item.createdAt)"
                class="size-2 shrink-0 rounded-full bg-primary-foreground/70 text-xs"
                aria-label="New"
              />
            </RouterLink>
          </div>
        </div>
      </ScrollArea>
    </div>
  </div>
</template>

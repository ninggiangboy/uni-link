<script setup lang="ts">
import { Check, ChevronDown, X } from 'lucide-vue-next'
import { computed, nextTick, onBeforeUnmount, ref, useSlots, watch } from 'vue'
import {
  SelectContent,
  SelectItem,
  SelectItemIndicator,
  SelectItemText,
  SelectPortal,
  SelectRoot,
  SelectTrigger,
  SelectValue,
  SelectViewport,
} from 'reka-ui'
import { Badge } from '@/ui/components/badge'
import { cn } from '@/ui/lib/utils'

export type SelectOption = { id: string | number; name: string }

const props = withDefaults(
  defineProps<{
    options: SelectOption[]
    placeholder?: string
    class?: string
    disabled?: boolean
    multiple?: boolean
    /** Số badge tối đa hiển thị trước khi gộp `+N` (giống sample `maxVisibleBadges`) */
    maxVisibleBadges?: number
    clearable?: boolean
    /** Giống sample `<Select isSearchable />`: ô tìm trong panel, trigger vẫn là nút Select */
    searchable?: boolean
    /** Placeholder trên ô search trong panel (khác `placeholder` của trigger) */
    searchPlaceholder?: string
    emptyMessage?: string
    defaultValue?: string | string[]
    /** Extra classes on the dropdown panel (sample: `popoverClassName`) */
    contentClass?: string
  }>(),
  {
    disabled: false,
    multiple: false,
    maxVisibleBadges: 2,
    clearable: false,
    searchable: false,
    searchPlaceholder: 'Search',
    emptyMessage: 'No results found',
    defaultValue: undefined,
  },
)

type Model = string | string[] | undefined
const model = defineModel<Model>()
const slots = useSlots()
const menuOpen = ref(false)
const filterQuery = ref('')
const searchInputRef = ref<HTMLInputElement | null>(null)
let searchFocusTimer: ReturnType<typeof setTimeout> | undefined

const hasValue = computed(() => {
  const v = model.value
  if (v == null) return false
  if (Array.isArray(v)) return v.length > 0
  return String(v).length > 0
})

const filteredOptions = computed(() => {
  if (!props.searchable) return props.options
  const q = filterQuery.value.trim().toLowerCase()
  if (!q) return props.options
  return props.options.filter((o) => o.name.toLowerCase().includes(q))
})

const selectedOptionsOrdered = computed(() => {
  const mv = model.value
  if (!props.multiple || !Array.isArray(mv)) return []
  return mv
    .map((id) => props.options.find((o) => String(o.id) === String(id)))
    .filter((o): o is SelectOption => o != null)
})

const visibleBadgeOptions = computed(() =>
  selectedOptionsOrdered.value.slice(0, props.maxVisibleBadges),
)

const badgeOverflowCount = computed(() => {
  const n = selectedOptionsOrdered.value.length
  const max = props.maxVisibleBadges
  return n > max ? n - max : 0
})

function removeSelectedOption(opt: SelectOption, e: Event) {
  e.preventDefault()
  e.stopPropagation()
  const mv = model.value
  if (!Array.isArray(mv)) return
  const id = String(opt.id)
  model.value = mv.filter((v) => String(v) !== id)
}

function focusSearchInput() {
  searchInputRef.value?.focus({ preventScroll: true })
}

watch(menuOpen, (open) => {
  if (!props.searchable) return
  if (searchFocusTimer !== undefined) {
    clearTimeout(searchFocusTimer)
    searchFocusTimer = undefined
  }
  if (!open) {
    filterQuery.value = ''
    return
  }
  // SelectContent sau khi popper `placed` gọi focusSelectedItem() — focus ô search phải chạy sau đó
  void nextTick().then(() => {
    focusSearchInput()
    searchFocusTimer = setTimeout(() => {
      focusSearchInput()
      searchFocusTimer = undefined
    }, 32)
  })
})

function onSearchKeydown(e: KeyboardEvent) {
  if (['Escape', 'Tab'].includes(e.key)) return
  if (['ArrowDown', 'ArrowUp', 'Home', 'End', 'PageUp', 'PageDown'].includes(e.key)) {
    return
  }
  e.stopPropagation()
}

function clearSelection(ev: Event) {
  ev.preventDefault()
  ev.stopPropagation()
  model.value = props.multiple ? [] : undefined
}

onBeforeUnmount(() => {
  if (searchFocusTimer !== undefined) clearTimeout(searchFocusTimer)
})
</script>

<template>
  <SelectRoot
    v-model="model"
    v-model:open="menuOpen"
    :multiple="multiple"
    :disabled="disabled"
    :default-value="defaultValue"
  >
    <div class="relative w-full">
    <SelectTrigger
      :class="
        cn(
          'flex w-full items-center justify-between rounded-sm border border-input bg-background-secondary px-3 text-start text-sm font-normal outline-none',
          multiple ? 'min-h-8 h-auto items-center py-1' : 'h-8 items-center',
          'hover:bg-background-secondary focus:ring-2 focus:ring-primary disabled:cursor-not-allowed disabled:opacity-50',
          props.class,
        )
      "
    >
      <SelectValue
        :placeholder="placeholder ?? 'Select'"
        :class="
          cn(
            'min-w-0 flex-1',
            clearable && hasValue && 'pr-6',
          )
        "
      >
        <template v-if="multiple && !slots.value" #default>
          <div v-if="!selectedOptionsOrdered.length" class="text-muted-foreground">
            {{ placeholder ?? 'Select' }}
          </div>
          <div v-else class="pointer-events-auto flex min-w-0 flex-1 flex-wrap gap-1">
            <Badge
              v-for="opt in visibleBadgeOptions"
              :key="opt.id"
              variant="secondary"
              class="grid max-w-full grid-cols-[1fr_16px] pr-0.5"
            >
              <span class="truncate">{{ opt.name }}</span>
              <button
                type="button"
                class="flex !size-4 cursor-pointer items-center justify-center rounded bg-transparent hover:bg-neutral-400/15"
                :aria-label="`Remove ${opt.name}`"
                @click.stop="removeSelectedOption(opt, $event)"
                @pointerdown.stop.prevent
              >
                <X class="!size-2.5" />
              </button>
            </Badge>
            <Badge v-if="badgeOverflowCount > 0" variant="secondary">
              <span>+{{ badgeOverflowCount }}</span>
            </Badge>
          </div>
        </template>
        <template v-else-if="slots.value" #default="scope">
          <slot name="value" v-bind="scope" />
        </template>
      </SelectValue>
      <ChevronDown class="size-4 shrink-0 self-center text-muted-foreground opacity-50" />
    </SelectTrigger>
    <button
      v-if="clearable && hasValue && !disabled"
      type="button"
      class="absolute top-1/2 right-7 z-10 flex size-6 -translate-y-1/2 items-center justify-center rounded bg-background-secondary text-muted-foreground hover:bg-background-tertiary"
      :aria-label="'Clear selection'"
      @pointerdown.stop.prevent="clearSelection"
    >
      <X class="size-3.5" />
    </button>
    </div>
    <SelectPortal>
      <SelectContent
        position="popper"
        side="bottom"
        :side-offset="4"
        align="start"
        :body-lock="false"
        :class="
          cn(
            'z-50 max-h-[350px] w-[var(--reka-select-trigger-width)] overflow-hidden rounded-lg border border-border bg-popover/80 text-popover-foreground shadow-popover outline-none backdrop-blur-xl',
            props.contentClass,
            'data-[state=open]:animate-in data-[state=closed]:animate-out data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0 data-[side=bottom]:slide-in-from-top-2 data-[side=left]:slide-in-from-right-2 data-[side=right]:slide-in-from-left-2 data-[side=top]:slide-in-from-bottom-2',
            searchable && 'flex flex-col p-1.5',
            !searchable && 'p-1.5',
          )
        "
      >
        <template v-if="searchable">
          <input
            ref="searchInputRef"
            v-model="filterQuery"
            type="text"
            inputmode="search"
            enterkeyhint="search"
            :placeholder="searchPlaceholder"
            aria-label="Search"
            :class="
              cn(
                'mb-1 flex h-8 w-full shrink-0 rounded-sm border border-input bg-background-secondary px-2 text-sm',
                'placeholder:text-muted-foreground focus-visible:ring-0 focus-visible:outline-none',
              )
            "
            autocomplete="off"
            @keydown="onSearchKeydown"
            @pointerdown.stop
          />
        </template>
        <SelectViewport
          :class="
            cn('scroll-pb-1 p-0 outline-none', searchable && 'min-h-0 flex-1 overflow-y-auto')
          "
        >
          <div
            v-if="filteredOptions.length === 0"
            role="status"
            class="flex h-8 items-center justify-center px-2 text-center text-sm text-muted-foreground"
          >
            {{ emptyMessage }}
          </div>
          <template v-else>
            <SelectItem
              v-for="o in filteredOptions"
              :key="o.id"
              :value="String(o.id)"
              class="group relative flex w-full cursor-pointer select-none items-center gap-2 rounded-sm py-1.5 pr-2 pl-2 text-sm text-popover-foreground outline-none data-[highlighted]:bg-primary data-[highlighted]:text-white"
            >
              <SelectItemText
                class="min-w-0 flex-1 font-normal group-data-[highlighted]:font-medium"
              >
                <slot name="option" :option="o">
                  {{ o.name }}
                </slot>
              </SelectItemText>
              <div
                class="flex w-5 shrink-0 items-center justify-center text-primary-foreground group-data-[highlighted]:text-white"
              >
                <SelectItemIndicator>
                  <Check class="size-4" />
                </SelectItemIndicator>
              </div>
            </SelectItem>
          </template>
        </SelectViewport>
      </SelectContent>
    </SelectPortal>
  </SelectRoot>
</template>

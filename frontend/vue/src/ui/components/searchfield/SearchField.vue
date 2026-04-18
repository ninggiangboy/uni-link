<script setup lang="ts">
import { Search, X } from 'lucide-vue-next'
import { computed, useAttrs } from 'vue'
import { FieldGroup } from '@/ui/components/field'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

const props = withDefaults(
  defineProps<{
    class?: string
    placeholder?: string
  }>(),
  {
    placeholder: 'Search...',
  },
)

const model = defineModel<string>({ default: '' })

const attrs = useAttrs()

const inputAttrs = computed(() => {
  const a = { ...attrs } as Record<string, unknown>
  delete a.class
  return a
})

const hasValue = computed(() => String(model.value ?? '').length > 0)
</script>

<template>
  <FieldGroup :class="cn('gap-2 px-2', props.class)">
    <Search class="pointer-events-none size-4 shrink-0 text-muted-foreground" aria-hidden="true" />
    <input
      type="search"
      data-slot="search-input"
      v-model="model"
      :placeholder="placeholder"
      :class="
        cn(
          'min-w-0 flex-1 bg-transparent text-sm outline-none placeholder:text-muted-foreground disabled:cursor-not-allowed disabled:opacity-50 [&::-webkit-search-cancel-button]:hidden',
        )
      "
      aria-label="Search"
      v-bind="inputAttrs"
    />
    <button
      type="button"
      tabindex="-1"
      :class="
        cn(
          'shrink-0 rounded-sm p-0.5 opacity-70 ring-offset-background transition-opacity hover:opacity-100 disabled:pointer-events-none',
          !hasValue && 'invisible pointer-events-none',
        )
      "
      aria-label="Clear search"
      @click="model = ''"
    >
      <X class="size-4" aria-hidden="true" />
    </button>
  </FieldGroup>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { Select, type SelectOption } from '@/ui/components/select';
import { cn } from '@/ui/lib/utils';

const props = withDefaults(
  defineProps<{
    /** Options as numbers (labels are `String(n)`). */
    options?: number[];
    class?: string;
  }>(),
  {
    options: () => [5, 10, 20, 50, 100],
  },
);

const pageSize = defineModel<number>({ default: 5 });

const selectModel = computed({
  get: () => String(pageSize.value),
  set: (v: string | string[] | undefined) => {
    const n = Number(Array.isArray(v) ? v[0] : v);
    if (Number.isFinite(n)) pageSize.value = n;
  },
});

const selectOptions = computed<SelectOption[]>(() =>
  props.options.map((option) => ({
    id: option,
    name: String(option),
  })),
);
</script>

<template>
  <div :class="cn('flex items-center gap-2', props.class)">
    <Select
      v-model="selectModel"
      :options="selectOptions"
      placeholder=""
      class="min-w-[66px]"
      content-class="!w-[90px] min-w-[90px]"
      :clearable="false"
    />
    <span class="text-xs text-muted-foreground whitespace-nowrap">Items per page</span>
  </div>
</template>

<script setup lang="ts">
import { Select, type SelectOption } from '@/ui/components/select';

const languages: (SelectOption & { flag: string })[] = [
  { id: 1, name: 'English', flag: '🇬🇧' },
  { id: 2, name: 'Spanish', flag: '🇪🇸' },
  { id: 3, name: 'French', flag: '🇫🇷' },
];
</script>

<template>
  <Select multiple :options="languages">
    <template #value="{ modelValue: mv }">
      <div v-if="Array.isArray(mv) && mv.length" class="flex flex-1 flex-wrap gap-1">
        <template v-for="id in mv" :key="id">
          <span
            v-for="lang in languages.filter((l) => String(l.id) === String(id))"
            :key="lang.id"
            class="inline-flex items-center gap-1 rounded-md border border-input px-2 py-0.5 text-xs"
          >
            <span>{{ lang.flag }}</span>
            <span>{{ lang.name }}</span>
          </span>
        </template>
      </div>
    </template>
    <template #option="{ option }">
      <div class="flex items-center gap-2">
        <span class="text-xl">{{ (option as (typeof languages)[0]).flag }}</span>
        <span>{{ option.name }}</span>
      </div>
    </template>
  </Select>
</template>

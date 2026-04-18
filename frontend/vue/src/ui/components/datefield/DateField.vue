<script setup lang="ts">
import type { DateValue } from '@internationalized/date'
import { DateFieldInput, DateFieldRoot } from 'reka-ui'
import { FieldGroup } from '@/ui/components/field'
import { dateFieldSegmentClassName } from '@/ui/lib/date-field-segment-classes'
import { UI_DATE_FIELD_LOCALE } from '@/ui/lib/date-field-locale'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

withDefaults(
  defineProps<{
    class?: string
    granularity?: 'day' | 'hour' | 'minute' | 'second'
    locale?: string
  }>(),
  { granularity: 'day', locale: UI_DATE_FIELD_LOCALE },
)

const model = defineModel<DateValue | undefined>()
</script>

<template>
  <DateFieldRoot
    v-model="model"
    v-bind="$attrs"
    :granularity="granularity"
    :locale="locale"
    :class="cn('grid w-full gap-1', $props.class)"
  >
    <template #default="{ segments }">
      <FieldGroup class="flex min-h-8 flex-wrap items-center gap-0 px-1.5">
        <template v-for="(s, i) in segments" :key="`date-field-seg-${i}`">
          <DateFieldInput
            :part="s.part"
            :class="dateFieldSegmentClassName()"
          >
            {{ s.value }}
          </DateFieldInput>
        </template>
      </FieldGroup>
    </template>
  </DateFieldRoot>
</template>

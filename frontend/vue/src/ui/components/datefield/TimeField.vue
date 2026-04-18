<script setup lang="ts">
import { parseTime, Time } from '@internationalized/date'
import { TimeFieldInput, TimeFieldRoot, type TimeValue } from 'reka-ui'
import { computed, ref, useAttrs, watch } from 'vue'
import { FieldGroup } from '@/ui/components/field'
import { dateFieldSegmentClassName } from '@/ui/lib/date-field-segment-classes'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

withDefaults(
  defineProps<{
    class?: string
    granularity?: 'hour' | 'minute' | 'second'
    /** 24 = no AM/PM; 12 = 12h clock. */
    hourCycle?: 12 | 24
    disabled?: boolean
    readonly?: boolean
  }>(),
  { granularity: 'minute', hourCycle: 24, disabled: false, readonly: false },
)

/** ISO time `HH:mm:ss` for forms / APIs; Reka still uses `TimeValue` internally. */
const model = defineModel<string | undefined>()
const innerModel = ref<TimeValue | undefined>()

function stringFromTime(t: TimeValue | undefined): string | undefined {
  if (t == null) return undefined
  if (t instanceof Time) return t.toString()
  return new Time(t.hour, t.minute, t.second ?? 0, t.millisecond ?? 0).toString()
}

watch(
  () => model.value,
  (s) => {
    const innerStr = stringFromTime(innerModel.value)
    if (s === innerStr) return
    if (s == null || s === '') {
      innerModel.value = undefined
      return
    }
    try {
      innerModel.value = parseTime(s)
    } catch {
      innerModel.value = undefined
    }
  },
  { immediate: true },
)

watch(innerModel, (v) => {
  const next = stringFromTime(v)
  if (next === model.value) return
  model.value = next
})

const attrs = useAttrs()
/** Fallthrough can include v-model keys in some setups; never forward them to Reka (expects `TimeValue`). */
const rootAttrs = computed(() => {
  const a = { ...attrs } as Record<string, unknown>
  delete a.modelValue
  delete a['onUpdate:modelValue']
  return a
})
</script>

<template>
  <TimeFieldRoot
    v-bind="rootAttrs"
    v-model="innerModel"
    :disabled="disabled"
    :readonly="readonly"
    :granularity="granularity"
    :hour-cycle="hourCycle"
    :class="cn('inline-grid w-max min-w-0 gap-1 self-center', $props.class)"
  >
    <template #default="{ segments }">
      <FieldGroup class="flex w-full min-h-8 min-w-0 flex-wrap items-center justify-center gap-0 px-1.5 py-0">
        <template v-for="(s, i) in segments" :key="`time-field-seg-${i}`">
          <TimeFieldInput
            :part="s.part"
            :class="dateFieldSegmentClassName()"
          >
            {{ s.value }}
          </TimeFieldInput>
        </template>
      </FieldGroup>
    </template>
  </TimeFieldRoot>
</template>

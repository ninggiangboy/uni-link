<script setup lang="ts">
import { ChevronDown, ChevronUp } from 'lucide-vue-next'
import { FieldGroup } from '@/ui/components/field'
import { Separator } from '@/ui/components/separator'
import {
  NumberFieldDecrement,
  NumberFieldIncrement,
  NumberFieldInput,
  NumberFieldRoot,
} from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

const props = withDefaults(
  defineProps<{
    class?: string
    min?: number
    max?: number
    step?: number
    formatOptions?: Intl.NumberFormatOptions
    locale?: string
    disabled?: boolean
    readonly?: boolean
    /** @default true */
    showStepper?: boolean
    placeholder?: string
    'aria-invalid'?: boolean
  }>(),
  { showStepper: true },
)

const model = defineModel<number | null>({ default: null })

const stepperBtnClass = cn(
  'flex size-4 shrink-0 grow cursor-pointer items-center justify-center rounded-none px-0.5 text-muted-foreground',
  'hover:text-foreground disabled:cursor-not-allowed disabled:opacity-50',
)
</script>

<template>
  <NumberFieldRoot
    v-model="model"
    v-bind="$attrs"
    aria-label="Number Field"
    :min="props.min"
    :max="props.max"
    :step="props.step"
    :format-options="props.formatOptions"
    :locale="props.locale"
    :disabled="props.disabled"
    :readonly="props.readonly"
    :class="cn('group flex flex-col gap-2', props.class)"
  >
    <FieldGroup :aria-invalid="props['aria-invalid'] ? true : undefined">
      <NumberFieldInput
        :placeholder="props.placeholder"
        :class="
          cn(
            'h-full min-h-8 w-full min-w-0 flex-1 border-r border-transparent bg-transparent pr-2 text-sm tabular-nums outline outline-0',
            'placeholder:text-muted-foreground',
            '[&::-webkit-search-cancel-button]:hidden',
            'focus-visible:outline-none disabled:cursor-not-allowed disabled:opacity-50',
            props.showStepper && 'pr-[3.25rem]',
          )
        "
      />
      <div v-if="props.showStepper" class="pointer-events-auto absolute right-0 flex items-center">
        <Separator orientation="vertical" class="h-5" />
        <div class="flex flex-col px-1.5">
          <NumberFieldIncrement
            :class="cn(stepperBtnClass, 'translate-y-0.5')"
            aria-label="Increment"
          >
            <ChevronUp class="!size-[14px]" aria-hidden="true" />
          </NumberFieldIncrement>
          <NumberFieldDecrement
            :class="cn(stepperBtnClass, '-translate-y-0.5')"
            aria-label="Decrement"
          >
            <ChevronDown class="!size-[14px]" aria-hidden="true" />
          </NumberFieldDecrement>
        </div>
      </div>
    </FieldGroup>
  </NumberFieldRoot>
</template>

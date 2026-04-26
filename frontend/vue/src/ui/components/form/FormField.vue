<script setup lang="ts">
import { Field } from 'vee-validate';
import type { RuleExpression } from 'vee-validate';
import { computed, provide, ref, useId } from 'vue';
import { cn } from '@/ui/lib/utils';
import type { FormFieldSlotProps } from './formFieldTypes';
import { FORM_DESCRIPTION_ACTIVE_KEY, FORM_ITEM_INJECTION_KEY } from './injectionKeys';

defineOptions({ inheritAttrs: false });

const props = defineProps<{
  name: string;
  rules?: RuleExpression<unknown>;
}>();

defineSlots<{
  default: (props: FormFieldSlotProps) => void;
}>();

const baseId = useId();
provide(FORM_ITEM_INJECTION_KEY, { id: baseId });

const descriptionActive = ref(false);
provide(FORM_DESCRIPTION_ACTIVE_KEY, descriptionActive);

const formDescriptionId = computed(() => `${baseId}-description`);
const formMessageId = computed(() => `${baseId}-message`);

function computeAriaDescribedBy(errorMessage: string | undefined): string | undefined {
  const parts: string[] = [];
  if (descriptionActive.value) {
    parts.push(formDescriptionId.value);
  }
  if (errorMessage) {
    parts.push(formMessageId.value);
  }
  return parts.length ? parts.join(' ') : undefined;
}

function buildSlotProps(slotProps: any): FormFieldSlotProps {
  const ariaDescribedBy = computeAriaDescribedBy(slotProps.errorMessage);
  const ariaBinds = {
    id: `${baseId}-control`,
    'aria-describedby': ariaDescribedBy,
    'aria-invalid': slotProps.errorMessage ? true : undefined,
  };
  return {
    value: slotProps.value,
    errorMessage: slotProps.errorMessage,
    errors: slotProps.errors,
    meta: slotProps.meta,
    field: slotProps.field,
    componentField: slotProps.componentField,
    formDescriptionId: formDescriptionId.value,
    formMessageId: formMessageId.value,
    ariaBinds,
    vmBinds: {
      name: props.name,
      value: slotProps.field?.value,
      modelValue: slotProps.componentField?.modelValue,
      'onUpdate:modelValue': slotProps.componentField?.['onUpdate:modelValue'],
      onInput: slotProps.field?.onInput,
      onChange: slotProps.field?.onChange,
      onBlur: slotProps.field?.onBlur,
      ...ariaBinds,
    },
    handleChange: slotProps.handleChange,
    handleBlur: slotProps.handleBlur,
    resetField: slotProps.resetField,
    handleReset: slotProps.handleReset,
    validate: slotProps.validate,
    setTouched: slotProps.setTouched,
  } as FormFieldSlotProps;
}
</script>

<template>
  <Field :name="props.name" :rules="props.rules" v-slot="slotProps">
    <div v-bind="$attrs">
      <slot v-bind="buildSlotProps(slotProps)" />
    </div>
  </Field>
</template>

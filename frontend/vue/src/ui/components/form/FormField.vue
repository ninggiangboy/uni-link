<script setup lang="ts">
import { Field } from 'vee-validate'
import type { RuleExpression } from 'vee-validate'

defineOptions({ inheritAttrs: false })

defineProps<{
  name: string
  /** VeeValidate rules — prefer field-level Zod via `schema.ruleFn()` or `zodRules(schema)` */
  rules?: RuleExpression<unknown>
}>()
</script>

<template>
  <Field v-bind="$attrs" :name="name" :rules="rules">
    <template v-if="$slots.default" #default="slotProps">
      <slot v-bind="slotProps" />
    </template>
  </Field>
</template>

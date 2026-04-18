<script setup lang="ts" generic="TModel = unknown">
import type { ComponentFieldBindingObject } from 'vee-validate'
import type { HTMLAttributes } from 'vue'
import FormControlBase from './FormControlBase.vue'
import type { FormControlVModelBinds } from './formControlVmBinds'

defineOptions({ inheritAttrs: false })

const props = defineProps<{
  componentField?: ComponentFieldBindingObject<TModel>
  class?: HTMLAttributes['class']
}>()
</script>

<template>
  <FormControlBase
    v-bind="$attrs"
    :class="props.class"
    :component-field="props.componentField as ComponentFieldBindingObject<unknown>"
    v-slot="raw"
  >
    <slot v-bind="raw as FormControlVModelBinds<TModel>" />
  </FormControlBase>
</template>

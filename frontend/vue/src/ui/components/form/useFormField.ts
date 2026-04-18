import { FieldContextKey } from 'vee-validate'
import { computed, inject, toValue } from 'vue'
import { FORM_ITEM_INJECTION_KEY } from './injectionKeys'

export function useFormField() {
  const field = inject(FieldContextKey)
  const item = inject(FORM_ITEM_INJECTION_KEY)

  if (!field) {
    throw new Error('[useFormField] must be used inside <FormField>')
  }
  if (!item) {
    throw new Error('[useFormField] must be used inside <FormItem>')
  }

  const base = item.id
  const id = computed(() => `${base}-control`)
  const formDescriptionId = computed(() => `${base}-description`)
  const formMessageId = computed(() => `${base}-message`)

  return {
    id,
    formItemId: id,
    formDescriptionId,
    formMessageId,
    name: computed(() => String(toValue(field.name))),
    errorMessage: field.errorMessage,
    errors: field.errors,
    meta: field.meta,
  }
}

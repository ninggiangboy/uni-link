import { FieldContextKey } from 'vee-validate';
import { computed, inject, toValue } from 'vue';
import { FORM_DESCRIPTION_ACTIVE_KEY, FORM_ITEM_INJECTION_KEY } from './injectionKeys';

export function useFormField() {
  const field = inject(FieldContextKey);
  const item = inject(FORM_ITEM_INJECTION_KEY);
  const descriptionActive = inject(FORM_DESCRIPTION_ACTIVE_KEY);

  if (!field) {
    throw new Error('[useFormField] must be used inside <FormField>');
  }
  if (!item) {
    throw new Error('[useFormField] must be used inside <FormField>');
  }

  const base = item.id;
  const id = computed(() => `${base}-control`);
  const formDescriptionId = computed(() => `${base}-description`);
  const formMessageId = computed(() => `${base}-message`);

  const ariaDescribedBy = computed(() => {
    const parts: string[] = [];
    if (descriptionActive?.value) {
      parts.push(formDescriptionId.value);
    }
    if (toValue(field.errorMessage)) {
      parts.push(formMessageId.value);
    }
    return parts.length ? parts.join(' ') : undefined;
  });

  const ariaBinds = computed(() => ({
    id: id.value,
    'aria-describedby': ariaDescribedBy.value,
    'aria-invalid': toValue(field.errorMessage) ? true : undefined,
  }));

  return {
    id,
    formItemId: id,
    formDescriptionId,
    formMessageId,
    name: computed(() => String(toValue(field.name))),
    errorMessage: field.errorMessage,
    errors: field.errors,
    meta: field.meta,
    ariaBinds,
  };
}

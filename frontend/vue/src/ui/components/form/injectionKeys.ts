import type { InjectionKey, Ref } from 'vue'

export const FORM_ITEM_INJECTION_KEY: InjectionKey<{ id: string }> = Symbol('form-item')

/** Set true when <FormDescription> is mounted (for aria-describedby). */
export const FORM_DESCRIPTION_ACTIVE_KEY: InjectionKey<Ref<boolean>> =
  Symbol('form-description-active')

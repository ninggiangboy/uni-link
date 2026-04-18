/** A11y-only slot props when `FormControl` is used without `component-field`. */
export type FormControlAriaBinds = {
  id: string
  'aria-describedby'?: string
  'aria-invalid'?: boolean
}

/**
 * Slot props from `FormControl` with `:component-field` — v-model + a11y only.
 * Intentionally not `ComponentFieldBindingObject` (no `onInput`/`onChange`; those break custom controls).
 */
export type FormControlVModelBinds<T = unknown> = FormControlAriaBinds & {
  name?: string
  modelValue?: T
  'onUpdate:modelValue'?: (e: T) => unknown
  onBlur?: (e: Event) => void
}

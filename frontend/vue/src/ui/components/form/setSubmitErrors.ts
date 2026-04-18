import type { FormActions, GenericObject } from 'vee-validate'

/** Maps API-style “submit errors” to vee-validate `setErrors`. */
export function setSubmitErrors<TValues extends GenericObject>(
  form: Pick<FormActions<TValues>, 'setErrors'>,
  fields: Parameters<FormActions<TValues>['setErrors']>[0],
): void {
  form.setErrors(fields)
}

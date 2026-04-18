import { toTypedSchema } from '@vee-validate/zod'
import type { GenericValidateFunction, TypedSchema } from 'vee-validate'
import type { ZodType } from 'zod'

/** Same as `schema.ruleFn()` — use when you prefer a function over the prototype helper. */
export function ruleFn<TSchema extends ZodType>(
  schema: TSchema,
): TypedSchema<import('zod').input<TSchema>, import('zod').output<TSchema>> {
  return toTypedSchema(schema)
}

/** RHF-style `rules` object: `{ validate: … }` for VeeValidate, backed by Zod. */
export function zodRules<TSchema extends ZodType>(
  schema: TSchema,
): Record<string, GenericValidateFunction<unknown>> {
  return {
    validate: async (value: unknown) => {
      const result = await schema.safeParseAsync(value)
      if (result.success) return true
      return result.error.issues[0]?.message ?? 'Invalid'
    },
  }
}

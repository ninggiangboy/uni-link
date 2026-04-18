import type { ZodTypeDef } from 'zod'
import type { GenericValidateFunction, TypedSchema } from 'vee-validate'

declare module 'zod' {
  interface ZodType<Output, Def extends ZodTypeDef, Input = Output> {
    /**
     * VeeValidate `TypedSchema` via `toTypedSchema(this)`.
     * Return types use `unknown` (not `Input`/`Output`) so the compiler does not expand Zod generics across the whole program — that expansion can make `vue-tsc` appear to hang.
     */
    ruleFn(): TypedSchema<unknown, unknown>

    /**
     * `{ validate: fn }`-style rules; same runtime as `ruleFn()` for simple fields.
     */
    validateFn(): GenericValidateFunction<unknown>
  }
}

export {}

import { toTypedSchema } from '@vee-validate/zod'
import type { GenericValidateFunction } from 'vee-validate'
import { ZodType } from 'zod'

function ruleFn<T extends ZodType>(this: T) {
  return toTypedSchema(this)
}

function validateFn(this: ZodType): GenericValidateFunction<unknown> {
  return async (value: unknown) => {
    const result = await this.safeParseAsync(value)
    if (result.success) return true
    return result.error.issues[0]?.message ?? 'Invalid'
  }
}

const proto = ZodType.prototype as ZodType & {
  ruleFn?: typeof ruleFn
  validateFn?: typeof validateFn
}

if (!proto.ruleFn) {
  proto.ruleFn = ruleFn
  proto.validateFn = validateFn
}

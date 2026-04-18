/**
 * Side-effect: adds `ruleFn()` and `validateFn()` to every Zod schema instance.
 * Imported from `main.ts` before the app mounts.
 */
import './extendZod'

export { ruleFn, zodRules } from './fieldRule'
export { z } from 'zod'

/** Drop keys whose value is `undefined` so v-bind does not force controlled props on Reka roots. */
export function omitUndefinedProps<T extends Record<string, unknown>>(obj: T): Partial<T> {
  return Object.fromEntries(
    Object.entries(obj).filter(([, v]) => v !== undefined),
  ) as Partial<T>
}

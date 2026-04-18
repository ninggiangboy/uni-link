# PART 11 — ANTI-PATTERN REFERENCE

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


---

### Hard Violations [CRITICAL] — Must Never Appear in Output

| ID | Anti-Pattern | Why |
|----|-------------|-----|
| AP-01 | `any` type | Breaks type safety |
| AP-02 | Prop mutation | Breaks unidirectional data flow |
| AP-03 | Raw `fetch` / using `FetchClient` directly in a component | Violates layer separation |
| AP-04 | Raw `fetch` / `FetchClient` in a store | Violates layer separation |
| AP-05 | `:key="index"` in `v-for` | Causes DOM diffing bugs |
| AP-06 | Business logic in component `<script setup>` | Violates component contract |
| AP-07 | VueUse called directly in component | Violates VueUse wrapping rule |
| AP-08 | `loading = false` only in `try` block | Leaves loading stuck on error |
| AP-09 | Reactive destructuring without `storeToRefs` or ref-awareness | Silently loses reactivity |
| AP-10 | Global event bus (`emitter.on`, `emitter.emit`) | Creates untraceable coupling |
| AP-11 | Manual `ref`+`watch`+service pattern for server data | Use TanStack Query instead |
| AP-12 | Destructuring TanStack Query result (`const { data } = useQuery(...)`) | Loses context with multiple queries; use `query.data` |
| AP-13 | Pinia for URL-appropriate state (search, filters, pagination) | Use `vue-router` query params instead |
| AP-14 | Using `isFetching` to block the UI with a full spinner | `isFetching` = background indicator only, never blocks content |
| AP-15 | Inline Zod schema defined inside `<script setup>` without extracting types | Schema and inferred type must live in `*.types.ts` |
| AP-16 | Manual validation logic (`if (!email.includes('@'))`) when a Zod schema exists | Always delegate field validation to the Zod schema via `toTypedSchema` |
| AP-17 | Calling `setErrors` on every API error without distinguishing field vs. general errors | Field-specific errors go to `setErrors`; general errors go to toast |
| AP-18 | Accessing `values.someField` directly in template for conditional logic | Use a `computed` that reads from `values` |
| AP-19 | Importing `reka-ui` (or other UI primitives) directly in a feature | Wrap in `src/ui/components/*`, then consume from `@/ui` |
| AP-20 | Feature-local `*Button` / `*Input` / `*Dialog` when `@/ui` already provides them | Reuse or extend the design system |

### Soft Violations — Flag and Recommend Fix

| ID | Anti-Pattern | Recommendation |
|----|-------------|----------------|
| SP-01 | Component >300 lines | Split into smaller components |
| SP-02 | Props >7 | Consider grouping or splitting component |
| SP-03 | `watchEffect` with known dependencies | Replace with `watch` |
| SP-04 | Objects as `v-memo` dependencies | Use primitive values only |
| SP-05 | `defineExpose` used broadly | Limit to input focus / scroll / clear only |
| SP-06 | Fine-grained query invalidation with no measured reason | Use invalidate-all as the default |
| SP-07 | Optimistic UI on low-confidence mutations | Only use for actions that almost always succeed |
| SP-08 | List query without `placeholderData: keepPreviousData` | Add to prevent layout shift on filter/page change |
| SP-09 | Cancellable query without `signal` | Pass `signal` from `queryFn` into `FetchClient.get/post/...` |
| SP-10 | Large form (>6 fields) defined entirely in one component | Split into sub-section components using `useFormContext` |
| SP-11 | `useFieldArray` used without a stable `:key` on each row | Always use the `id` property returned by `useFieldArray` |

---

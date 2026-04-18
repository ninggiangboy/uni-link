# PART 10 — ERROR HANDLING

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


---

### RULE-ERR-01 · Standard Async Pattern for Manual State [CRITICAL]

For composables that do NOT use TanStack Query, every async action must follow this pattern. `FetchHttpError` (from `FetchClient`) extends `Error`, so `instanceof Error` still works; use `instanceof FetchHttpError` when you need HTTP-specific handling.

```ts
const loading = ref(false)
const error = ref<string | null>(null)

async function fetchData(id: number) {
  try {
    loading.value = true
    error.value = null              // reset on each call
    data.value = await someService.get(id)
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Unexpected error'
  } finally {
    loading.value = false           // MUST be in finally, not in try
  }
}
```

**Rules:**
- `loading.value = false` must always be in `finally`, never only in `try`
- `error.value = null` must be reset at the start of each call
- Never swallow errors silently — empty `catch` blocks are forbidden

---

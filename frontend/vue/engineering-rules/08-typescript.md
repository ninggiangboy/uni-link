# PART 7 — TYPESCRIPT

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


---

### RULE-TS-01 · No `any` [CRITICAL]

```ts
// ❌ FORBIDDEN
function process(data: any) {}

// ✅ CORRECT: use unknown and narrow
function process(data: unknown) {
  if (typeof data === 'string') { ... }
}

// ✅ CORRECT: use generics
function useFetch<T>(url: string): Promise<T> { ... }
```

**Exception:** `shallowRef<any>` is allowed only for third-party library instances (chart instances, editor instances) where the library provides no types.

---

### RULE-TS-02 · Interface for Models, Type for Aliases

```ts
// ✅ Domain models → interface
interface User {
  id: number
  name: string
  email: string
}

// ✅ Aliases, unions, computed types → type
type UserId = number
type UserRole = 'admin' | 'viewer' | 'editor'
type ApiResponse<T> = { data: T; status: number }
```

---

### RULE-TS-03 · Centralise Types

- Feature types → `[feature].types.ts` inside the feature folder
- API contract types → `api.types.ts` in `shared/`
- Never define types inline inside `.vue` files unless they are component-local prop shapes

---

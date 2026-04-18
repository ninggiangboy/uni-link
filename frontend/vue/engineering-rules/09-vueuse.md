# PART 8 — VUEUSE

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


`@vueuse/core` may appear as a transitive dependency; **if** you import VueUse in app code, still follow the composable-wrapping rules below.

---

### RULE-VU-01 · Always Wrap VueUse [CRITICAL]

```ts
// ❌ FORBIDDEN: direct VueUse in component
const token = useLocalStorage('token', null)

// ✅ REQUIRED: wrap in composable
export function useAuthStorage() {
  const token = useLocalStorage<string | null>('token', null)
  function clear() { token.value = null }
  return { token, clear }
}
```

---

### RULE-VU-02 · VueUse Is Infrastructure, Not Domain Logic

VueUse handles **how** (browser APIs, events, storage). Composables handle **what** (business rules, domain logic).

```ts
// ❌ VIOLATION: VueUse mixed with business logic
export function useAuth() {
  const token = useLocalStorage('token', null)
  // validation logic, role checks, API calls...
}

// ✅ CORRECT: each layer has one responsibility
// useAuthStorage.ts → only storage via VueUse
// useAuth.ts → imports useAuthStorage, contains domain logic
```

---

### RULE-VU-03 · VueUse Decision Table

| Need                          | VueUse Solution                   | Use? |
|-------------------------------|-----------------------------------|------|
| localStorage / sessionStorage | `useLocalStorage`                 | ✅   |
| DOM event listeners           | `useEventListener`                | ✅   |
| Debounce                      | `useDebounceFn`                   | ✅   |
| Throttle                      | `useThrottleFn`                   | ✅   |
| Window size / breakpoints     | `useWindowSize`, `useBreakpoints` | ✅   |
| Dark mode                     | `useDark`                         | ✅   |
| Clipboard                     | `useClipboard`                    | ✅   |
| API calls / server state      | —                                 | ❌   |
| Business rule validation      | —                                 | ❌   |
| Complex async workflows       | —                                 | ❌   |

---

# PART 6 — STATE MANAGEMENT (PINIA)

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


Use Pinia **only** for state that is:
1. Shared across multiple unrelated components, AND
2. Not appropriate for URL state (see PHIL-03), AND
3. Not server state (which belongs in TanStack Query)

---

### RULE-STORE-01 · Use Setup Store Syntax

```ts
// ✅ REQUIRED: Setup store pattern
export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  const isAuthenticated = computed(() => user.value !== null)

  function setUser(u: User) { user.value = u }
  function clearUser() { user.value = null }

  return { user, isAuthenticated, setUser, clearUser }
})

// ❌ FORBIDDEN: Options store pattern
export const useUserStore = defineStore('user', {
  state: () => ({ user: null }),
  // ...
})
```

---

### RULE-STORE-02 · Store Access Rules

```ts
// ✅ CORRECT: use storeToRefs for reactive state
const store = useUserStore()
const { user, isAuthenticated } = storeToRefs(store)
const { setUser, clearUser } = store   // actions: destructure directly (not reactive)

// ❌ VIOLATION: direct destructure loses reactivity silently
const { user } = useUserStore()
```

---

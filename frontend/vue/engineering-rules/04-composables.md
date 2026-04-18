# PART 3 — COMPOSABLES

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


---

### RULE-USE-01 · Composable Structure Contract

Every composable must follow this structure:

```ts
// ✅ CANONICAL PATTERN (for cases not using TanStack Query)
export function useFeatureName() {
  // 1. Reactive state
  const data = ref<DataType | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 2. Actions
  async function fetchData(id: number) {
    try {
      loading.value = true
      error.value = null
      data.value = await featureService.get(id)
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Unknown error'
    } finally {
      loading.value = false
    }
  }

  // 3. Explicit return — never return entire scope
  return { data, loading, error, fetchData }
}
```

**Rules:**
- Must start with `use`
- One responsibility per composable (Single Responsibility)
- Must always return an explicit named object
- No DOM manipulation inside composables
- No UI-specific logic (e.g. `alert`, `confirm`, `document.querySelector`)

---

### RULE-USE-02 · Avoid Destructuring Reactive Return Values [CRITICAL]

```ts
// ❌ VIOLATION: loses reactivity silently
const { user } = useUser()   // if user is a ref, this is safe; if it is a raw value, it is not

// ✅ CORRECT: always use storeToRefs for stores
const { user } = storeToRefs(useUserStore())

// ✅ CORRECT: for TanStack Query, access via the query object — never destructure
const userQuery = useQuery(api.user.detail(userId))
// use: userQuery.data, userQuery.isLoading, userQuery.error
// NOT: const { data, isLoading } = useQuery(...)
```

**Rule:** When consuming a composable, always verify which properties are refs vs raw values before destructuring.

---

### RULE-USE-03 · Keep Composables Focused

```ts
// ❌ VIOLATION: one composable doing too much
export function useUserPage() {
  // auth logic + user data fetching + form state + permissions + URL state
}

// ✅ CORRECT: one composable, one responsibility
export function useUserData(id: Ref<number>) { ... }
export function useUserPermissions(user: Ref<User>) { ... }
export function useUserForm(user: Ref<User>) { ... }
```

---

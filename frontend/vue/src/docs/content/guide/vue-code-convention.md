---
title: Code Conventions
description: A guide to coding conventions for writing consistent, maintainable Vue code
---

Writing clean, maintainable Vue code requires following consistent conventions. This guide outlines essential coding standards that will help you and your team write more readable and scalable Vue applications.

## File Naming Conventions

### Component Files

Use **PascalCase** for component files to immediately identify them as Vue components:

- ✅ `UserCard.vue`, `LoginForm.vue`, `NavigationBar.vue`
- ❌ `userCard.vue`, `login-form.vue`

### Non-Component Files

Use **camelCase** for utility files, composables, and other non-component files:

- ✅ `use-fetch.ts`, `api-client.ts`, `format-date.ts`
- ❌ `useFetch.ts`, `apiClient.ts`

### Composables

Use **camelCase** with a `use` prefix:

- ✅ `useUser.ts`, `useAuth.ts`, `useDebounce.ts`
- ❌ `UserHook.ts`, `auth.ts`

### API Module Files

Use **camelCase** with an `Api` suffix:

- ✅ `userApi.ts`, `postApi.ts`
- ❌ `user-api.ts`, `UserApi.ts`

### Types Files

Use **camelCase** with `.types` suffix:

- ✅ `user.types.ts`, `post.types.ts`

## Code Naming Conventions

| Item | Convention | Example |
|------|-----------|---------|
| Components | PascalCase | `UserList`, `AppHeader` |
| Composables | camelCase + `use` prefix | `useFetch`, `useAuth` |
| Functions | camelCase | `handleClick`, `fetchUser` |
| Variables | camelCase | `userList`, `isLoading` |
| Enums | PascalCase | `enum UserRole { Admin, Guest }` |
| Constants | UPPER_SNAKE_CASE | `API_BASE_URL`, `MAX_RETRY` |

## Component Structure and Organization

### Order of Component Internals

Organize your `<script setup>` code in a consistent order for better readability:

```vue
<script setup lang="ts">
// 1. Imports
import { computed, ref } from 'vue'
import { useQuery, useMutation } from '@tanstack/vue-query'
import { api } from '@/shared/lib/api'
import { toast } from '@/ui/components/sonner'

// 2. Props and emits
const props = defineProps<{ userId: number }>()
const emit = defineEmits<{ saved: [data: User] }>()

// 3. State hooks (ref, reactive)
const isEditing = ref(false)

// 4. Data fetching hooks (useQuery, custom data composables)
const userQuery = useQuery(api.user.detail(toRef(props, 'userId')))

// 5. Derived state (computed)
const isAuthenticated = computed(() => !!userQuery.data.value)

// 6. Event handlers and functions (arrow functions)
const handleSave = (data: UpdateUserDto) => {
  updateMutation.mutate(
    { id: props.userId, data },
    { onSuccess: () => toast.success('Saved') },
  )
}

// 7. Mutations
const updateMutation = useMutation(api.user.update())
</script>
```

## Function Declaration Style

### Event Handlers: Use Arrow Functions

Use arrow functions for event handlers and callbacks:

```ts
// ✅ Good: Arrow functions for handlers
const handleClick = (id: string) => {
  console.log('Clicked user:', id)
}

const handleSubmit = async (data: FormData) => {
  await api.user.create(data)
}
```

### Composables: Use Function Declaration

Use function declarations for composables:

```ts
// ✅ Good: Function declaration for composables
export function useUser(id: MaybeRef<number>) {
  return useQuery(api.user.detail(id))
}

export function useAuth() {
  const user = ref<User | null>(null)
  // ...
  return { user }
}
```

## Destructuring Best Practices

### Destructure Only Component Props

Avoid excessive destructuring except for component props. Keep query results and complex objects intact:

```vue
<script setup lang="ts">
// ✅ Good: Destructure only props
const props = defineProps<{ userId: number }>()

const userQuery = useQuery(api.user.detail(toRef(props, 'userId')))

// Keep query object intact, access properties directly
if (userQuery.isLoading.value) return
if (userQuery.isError.value) return

// ❌ Avoid: Excessive destructuring
const { data, isLoading, isError, refetch } = useQuery(...)
</script>
```

**Why?**

- **Clearer context**: `userQuery.isLoading` is more explicit than just `isLoading`
- **Avoids naming conflicts** when using multiple queries
- **Easier to trace** where data comes from

## Early Return Pattern

Use early returns to reduce nesting and improve code readability. Handle edge cases and loading states at the beginning of your components:

```vue
<script setup lang="ts">
const props = defineProps<{ userId: number }>()

const userQuery = useQuery(api.user.detail(toRef(props, 'userId')))

// Early returns for edge cases
const displayState = computed(() => {
  if (userQuery.isLoading.value) return 'loading'
  if (userQuery.isError.value) return 'error'
  if (!userQuery.data.value) return 'empty'
  return 'success'
})
</script>

<template>
  <div v-if="displayState === 'loading'">
    <LoadingSpinner />
  </div>
  <ErrorMessage v-else-if="displayState === 'error'" :error="userQuery.error" />
  <EmptyState v-else-if="displayState === 'empty'" message="No user found" />
  <UserCard v-else :user="userQuery.data" />
</template>
```

This is cleaner than deeply nested conditional rendering:

```vue
<!-- ❌ Avoid: Deeply nested conditions -->
<template>
  <div>
    <LoadingSpinner v-if="userQuery.isLoading" />
    <ErrorMessage v-else-if="userQuery.isError" />
    <div v-else-if="userQuery.data">
      <UserCard :user="userQuery.data" />
    </div>
    <EmptyState v-else />
  </div>
</template>
```

## Export Conventions

### Prefer Named Exports

Use named exports over default exports in most cases for better IDE support, easier refactoring, and more explicit imports:

```ts
// ✅ Preferred: Named export
export function useUser(id: MaybeRef<number>) {
  return useQuery(api.user.detail(id))
}

// ❌ Avoid: Default export (unless necessary)
export default function useUser(id: MaybeRef<number>) {
  return useQuery(api.user.detail(id))
}
```

**Exception**: Use default exports when required by Vue Router lazy loading:

```ts
// Acceptable for route lazy loading
const UserProfile = () => import('@/features/user/UserProfile.vue')
```

## Cheat Sheet

| Pattern | When to Use |
|---------|-------------|
| `computed` | Derived state, transformations |
| `ref` | Primitive mutable state |
| `reactive` | Object state with known shape |
| `watch` | Side effects triggered by state changes |
| `watchEffect` | Auto-tracking side effects (use sparingly) |
| Early returns | Complex branching with 3+ outcomes |
| Ternary | Simple if/else in templates |

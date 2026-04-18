# PART 5 — DATA FETCHING (TANSTACK QUERY)

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


TanStack Query is the **primary tool for server state**. It replaces manual `ref` + `watch` + service-call patterns for all data that comes from the server.

---

### RULE-QUERY-01 · Use TanStack Query for All Server State [CRITICAL]

```ts
// ❌ VIOLATION: manual server state management
export function useUser(id: number) {
  const user = ref<User | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  watch(() => id, async (newId) => {
    loading.value = true
    try {
      user.value = await userService.getUser(newId)
    } catch (e) {
      error.value = 'Failed'
    } finally {
      loading.value = false
    }
  }, { immediate: true })

  return { user, loading, error }
}

// ✅ CORRECT: TanStack Query handles caching, loading, error, background sync
import { useQuery } from '@tanstack/vue-query'

const userQuery = useQuery(api.user.detail(userId))
// userQuery.data, userQuery.isLoading, userQuery.isError are all handled
```

---

### RULE-QUERY-02 · API Module Structure with queryOptions

Organise API code as **classes in `src/lib/api/sdk/*.api.ts`**, taking `FetchClient` via the constructor and exposing methods that return `queryOptions` / `mutationOptions`. The `Api` class in `src/lib/api/index.ts` aggregates those modules; the `api` singleton is exported from `src/shared/lib/api.ts`.

```ts
// src/lib/api/sdk/example.api.ts (from this repo — shortened)
import { queryOptions } from '@tanstack/vue-query'
import type { FetchClient } from '../client'
import type { ExampleResponse } from './example.type'

export class ExampleApi {
  constructor(private readonly client: FetchClient) {}

  hello() {
    return queryOptions({
      queryKey: ['hello'],
      queryFn: ({ signal }) => this.client.get<ExampleResponse>('/hello', { signal }),
    })
  }
}
```

```ts
// src/lib/api/index.ts — aggregate SDK modules (add user when needed)
import { type FetchClient } from './client'
import { ExampleApi } from './sdk/example.api'

export class Api {
  example: ExampleApi

  constructor(private readonly client: FetchClient) {
    this.example = new ExampleApi(this.client)
  }
}
```

```ts
// src/shared/lib/api.ts — app singleton
import { Api, FetchClient } from '@/lib/api'

const baseURL = import.meta.env.VITE_API_BASE_URL ?? '/api'
export const api = new Api(new FetchClient({ baseURL }))
```

Extended example (user list with pagination — `FetchClient` has no `params` object; build the query string yourself):

```ts
import { queryOptions, mutationOptions, keepPreviousData } from '@tanstack/vue-query'
import type { MaybeRef } from 'vue'
import { toValue } from 'vue'
import type { FetchClient } from '@/lib/api'
import type { User, UpdateUserDto } from './user.types'

export class UserApi {
  private readonly BASE = '/users'
  constructor(private readonly client: FetchClient) {}

  list(params: { search?: string; page?: number }) {
    return queryOptions({
      queryKey: [this.BASE, 'list', params],
      queryFn: ({ signal }) => {
        const q = new URLSearchParams()
        if (params.search) q.set('search', params.search)
        if (params.page != null) q.set('page', String(params.page))
        const suffix = q.size ? `?${q}` : ''
        return this.client.get<User[]>(`${this.BASE}${suffix}`, { signal })
      },
      placeholderData: keepPreviousData,
    })
  }

  detail(id: MaybeRef<number>) {
    return queryOptions({
      queryKey: [this.BASE, 'detail', id],
      queryFn: ({ signal }) =>
        this.client.get<User>(`${this.BASE}/${toValue(id)}`, { signal }),
    })
  }

  update() {
    return mutationOptions({
      mutationFn: ({ id, data }: { id: number; data: UpdateUserDto }) =>
        this.client.put<User>(`${this.BASE}/${id}`, data),
    })
  }
}
```

Constructor typing: `import type { FetchClient } from '@/lib/api'`.

---

### RULE-QUERY-03 · Query vs Mutation Decision Rule

| Action | Use |
|--------|-----|
| Read data (GET) | `useQuery` |
| Create / Update / Delete | `useMutation` |
| User-triggered export (click to download) | `useMutation` — even though no data is mutated |
| Paginated or filtered lists | `useQuery` with `placeholderData: keepPreviousData` |

**Decision rule:** If data loading is triggered by a **click or submit event**, use `useMutation`. If data loads automatically based on component state or URL params, use `useQuery`.

---

### RULE-QUERY-04 · Using Queries in Components

```vue
<script setup lang="ts">
import { toRef } from 'vue'
import { useQuery, useMutation } from '@tanstack/vue-query'
import { api } from '@/shared/lib/api'
import { toast } from '@/ui/components/sonner'
import { Spinner } from '@/ui/components/spinner'

const props = defineProps<{ userId: number }>()

const userQuery = useQuery(api.user.detail(toRef(props, 'userId')))
const updateMutation = useMutation(api.user.update())

const handleSave = (data: UpdateUserDto) => {
  updateMutation.mutate(
    { id: props.userId, data },
    { onSuccess: () => toast.success('Saved') },
  )
}
</script>

<template>
  <div v-if="userQuery.isLoading" class="flex justify-center p-8">
    <Spinner />
  </div>
  <p v-else-if="userQuery.isError" class="text-sm text-destructive">
    {{ userQuery.error?.message }}
  </p>
  <UserForm
    v-else
    :user="userQuery.data"
    :is-saving="updateMutation.isPending"
    @save="handleSave"
  />
</template>
```

**Rules:**
- Access query properties via the query object: `userQuery.data`, `userQuery.isLoading`
- Do NOT destructure the query result: `const { data, isLoading } = useQuery(...)` loses context when multiple queries exist in the same component

---

### RULE-QUERY-05 · Loading State Handling

There are three distinct loading states — use them differently:

| State | When `true` | Correct UI response |
|-------|------------|---------------------|
| `isLoading` | First fetch ever (no cached data) | Full skeleton or spinner — block the content area |
| `isFetching` | Any background refresh (pagination, filter change, refetch) | Lightweight NProgress bar — keep existing content visible |
| mutation `isPending` | Mutation running | Disable submit button OR wrap form with `LoadingOverlay` |

```vue
<script setup lang="ts">
import { ref } from 'vue'
import { keepPreviousData, useQuery } from '@tanstack/vue-query'
import { useNProgress } from '@/ui'
import { Spinner } from '@/ui/components/spinner'
import { api } from '@/shared/lib/api'

// Assumes PostApi.list() exists — illustrative
const searchQuery = ref('')
const postsQuery = useQuery({
  ...api.post.list({ search: searchQuery.value }),
  placeholderData: keepPreviousData,
})

useNProgress({ isFetching: postsQuery.isFetching })
</script>

<template>
  <div v-if="postsQuery.isLoading" class="flex justify-center p-8">
    <Spinner />
  </div>
  <PostList v-else :posts="postsQuery.data" />
</template>
```

```vue
<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { LoadingOverlay } from '@/ui/components/loading-overlay'
</script>

<template>
  <Button type="button" :disabled="deleteMutation.isPending" @click="handleDelete">
    Delete
  </Button>

  <LoadingOverlay :is-loading="saveMutation.isPending">
    <UserForm @submit="handleSave" />
  </LoadingOverlay>
</template>
```

---

### RULE-QUERY-06 · Pass `signal` for Cancellable Requests

Always pass `signal` in queries where the `queryKey` changes frequently (search, pagination, filters). `FetchClient` forwards `signal` to `fetch`, cancelling in-flight work when the key changes — avoiding races and wasted bandwidth.

```ts
list(params: { search?: string; page?: number }) {
  return queryOptions({
    queryKey: [this.BASE, 'list', params],
    queryFn: ({ signal }) =>
      this.client.get<User[]>(/* URL + query string */, { signal }),
    placeholderData: keepPreviousData,
  })
}
```

`FetchClient.get` accepts `RequestInit` (including `signal`) and passes it through to `fetch`. Cancelled requests typically throw `AbortError`, which TanStack Query handles as expected.

---

### RULE-QUERY-07 · Data Invalidation Strategy

**This repo today:** `src/lib/query-client.ts` uses `QueryClient` with default `staleTime` / `retry` — no global post-mutation invalidation yet.

```ts
// src/lib/query-client.ts (as checked in)
import { QueryClient } from '@tanstack/vue-query'

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 60_000,
      retry: 1,
    },
  },
})
```

**As the app grows:** you may add `MutationCache.onSuccess` + `invalidateQueries` (broad or via `meta.invalidates`) — same idea as TanStack docs; add only when you have a clear need.

```ts
// Optional sketch — not in this repo (import MutationCache, matchQuery from @tanstack/vue-query)
mutationCache: new MutationCache({
  onSuccess: async (_data, _variables, _context, mutation) => {
    const invalidates = mutation.meta?.invalidates as string[][] | undefined
    await queryClient.invalidateQueries(
      invalidates
        ? { predicate: (q) => invalidates.some((key) => matchQuery({ queryKey: key }, q)) }
        : undefined,
    )
  },
})
```

**Rule:** Start with explicit `invalidateQueries` in each mutation’s `onSuccess`; move to global or predicate-based invalidation only when you can measure the benefit.

---

### RULE-QUERY-08 · Optimistic UI

Use optimistic updates only for actions that almost always succeed (toggles, quick status changes). Failed optimistic updates cause a worse UX than no optimism at all.

```ts
import { useMutation } from '@tanstack/vue-query'
import { queryClient } from '@/lib/query-client'

// `toggleUserActive` = service-layer async / mutationFn from `mutationOptions` (RULE-SVC-01)
const toggleMutation = useMutation({
  mutationFn: (id: number) => toggleUserActive(id),
  onMutate: async (id) => {
    await queryClient.cancelQueries({ queryKey: ['/users', 'list'] })
    const previous = queryClient.getQueryData(['/users', 'list'])

    queryClient.setQueryData(['/users', 'list'], (old: User[]) =>
      old.map(u => u.id === id ? { ...u, active: !u.active } : u)
    )

    return { previous }
  },
  onError: (_err, _id, context) => {
    queryClient.setQueryData(['/users', 'list'], context?.previous)
  },
})
```

---

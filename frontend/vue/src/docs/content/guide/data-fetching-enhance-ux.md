---
title: Enhancing User Experience
description: Loading states, error handling, and optimistic updates for better UX
---

## Loading States

There are three distinct loading states — use them differently:

| State | When `true` | Correct UI response |
|-------|------------|---------------------|
| `isLoading` | First fetch ever (no cached data) | Full skeleton or spinner — block the content area |
| `isFetching` | Any background refresh (pagination, filter change, refetch) | Lightweight NProgress bar — keep existing content visible |
| mutation `isPending` | Mutation running | Disable submit button OR wrap form with `LoadingOverlay` |

### Initial Load: Spinner or Skeleton

```vue
<script setup lang="ts">
import { useQuery } from '@tanstack/vue-query'
import { api } from '@/shared/lib/api'
import { Spinner } from '@/ui/components/spinner'

const postsQuery = useQuery(api.post.list({}))
</script>

<template>
  <div v-if="postsQuery.isLoading.value" class="flex justify-center p-8">
    <Spinner />
  </div>
  <PostList v-else :posts="postsQuery.data" />
</template>
```

### Background Fetch: NProgress

```vue
<script setup lang="ts">
import { keepPreviousData, useQuery } from '@tanstack/vue-query'
import { useNProgress } from '@/ui'
import { api } from '@/shared/lib/api'

const postsQuery = useQuery({
  ...api.post.list({}),
  placeholderData: keepPreviousData,
})

useNProgress({ isFetching: postsQuery.isFetching })
</script>
```

### Mutation: LoadingOverlay

```vue
<template>
  <Button type="button" :disabled="deleteMutation.isPending.value" @click="handleDelete">
    Delete
  </Button>

  <LoadingOverlay :is-loading="saveMutation.isPending.value">
    <UserForm @submit="handleSave" />
  </LoadingOverlay>
</template>
```

## Error Handling

Always provide human-friendly error messages. Say what happened and how to fix it — avoid jargon.

```vue
<script setup lang="ts">
import { useQuery } from '@tanstack/vue-query'
import { api } from '@/shared/lib/api'

const userQuery = useQuery(api.user.detail(userId))
</script>

<template>
  <div v-if="userQuery.isLoading.value" class="flex justify-center p-8">
    <Spinner />
  </div>
  <p v-else-if="userQuery.isError.value" class="text-sm text-destructive">
    {{ userQuery.error?.message || 'Failed to load user' }}
  </p>
  <UserProfile v-else :user="userQuery.data" />
</template>
```

## Optimistic Updates

Use optimistic updates only for actions that almost always succeed (toggles, quick status changes). Failed optimistic updates cause a worse UX than no optimism at all.

```vue
<script setup lang="ts">
import { useMutation } from '@tanstack/vue-query'
import { queryClient } from '@/lib/query-client'
import { api } from '@/shared/lib/api'

const toggleMutation = useMutation({
  mutationFn: (id: number) => api.user.toggleActive(id),
  onMutate: async (id) => {
    await queryClient.cancelQueries({ queryKey: ['/users', 'list'] })
    const previous = queryClient.getQueryData(['/users', 'list'])

    queryClient.setQueryData(['/users', 'list'], (old: User[]) =>
      old?.map(u => u.id === id ? { ...u, active: !u.active } : u),
    )

    return { previous }
  },
  onError: (_err, _id, context) => {
    queryClient.setQueryData(['/users', 'list'], context?.previous)
  },
})
</script>
```

## Cancellable Requests

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

Cancelled requests typically throw `AbortError`, which TanStack Query handles as expected.

## Summary

| Pattern | When to Use |
|---------|-------------|
| `Spinner` / `Skeleton` | Initial load (`isLoading`) |
| `NProgress` | Background refresh (`isFetching`) |
| `LoadingOverlay` | Mutation running (`isPending`) |
| `toast.error()` | General failures |
| Inline error message | Field-specific validation errors |
| Optimistic updates | High-success-rate actions (toggles, likes) |
| `signal` in queries | Search, pagination, filters |

---
title: Data Invalidation Best Practices
description: Strategies and examples for properly invalidating and refetching data to keep your UI up to date
---

## What Does Invalidate Query Mean?

In TanStack Query, to invalidate a query means marking a cached query as stale so that it will be refetched the next time it is used. This helps ensure your application's data stays up to date after a mutation or relevant change.

## Just Invalidate Everything

Honestly, the easiest approach to ensure your UI reflects server changes is to just invalidate all queries:

```ts
queryClient.invalidateQueries()
```

It's understandable to think this could be inefficient, and in certain situations that's true, but for most applications, the simplicity and reliability are worth it. You don't have to micromanage individual queryKeys, and your UI will remain accurate and up to date everywhere.

If you try to fine-grain invalidation, you must know every place a piece of data is used. For example, mutating a category that's also referenced by posts and filters means you must track and invalidate all relevant query keys. That's tedious and error-prone.

Invalidating everything is often the pragmatic choice.

## What If I Need Fine-Grained Invalidation?

There are cases where you want precision. You can set up a `MutationCache` with automatic invalidation:

```ts
// src/lib/query-client.ts — optional enhancement
import { QueryClient, MutationCache, matchQuery } from '@tanstack/vue-query'

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      staleTime: 60_000,
    },
  },
  mutationCache: new MutationCache({
    onSuccess: async (_data, _variables, _context, mutation) => {
      await queryClient.invalidateQueries({
        predicate: (query) =>
          // invalidate all matching tags at once
          // or everything if no meta is provided
          (mutation.meta?.invalidates as string[][])?.some((queryKey) =>
            matchQuery({ queryKey }, query),
          ) ?? true,
      })
    },
  }),
})
```

By default, we invalidate after every mutation. If you want a mutation to be an exception and manage invalidation yourself, provide a `meta` field with the keys to invalidate:

```vue
<script setup lang="ts">
import { useMutation } from '@tanstack/vue-query'
import { api } from '@/shared/lib/api'

const updateMutation = useMutation({
  ...api.post.update(),
  meta: {
    invalidates: [['posts', 'list'], ['posts', 'detail']],
  },
})
</script>
```

## Explicit Invalidation in onSuccess

For more control, invalidate queries explicitly in each mutation's `onSuccess`:

```vue
<script setup lang="ts">
import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { api } from '@/shared/lib/api'
import { toast } from '@/ui/components/sonner'

const queryClient = useQueryClient()

const deleteMutation = useMutation({
  mutationFn: (id: number) => api.post.delete(id),
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['posts', 'list'] })
    toast.success('Post deleted')
  },
})
</script>
```

## Summary

- Begin with broad query invalidation for ease and reliability
- Introduce fine-grained targeting only when necessary for your app's requirements
- Use `meta.invalidates` for mutations that should only invalidate specific queries
- Start with explicit `invalidateQueries` in each mutation's `onSuccess`; move to global or predicate-based invalidation only when you can measure the benefit

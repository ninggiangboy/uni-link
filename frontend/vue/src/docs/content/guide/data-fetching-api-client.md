---
title: Build an API Client
description: Learn how to create and use a type-safe, scalable API client with TanStack Query integration
---

## Why Build a Dedicated API Client?

In real-world applications, you often have multiple apps (web, mobile, desktop) that need to communicate with the same backend API. By creating a shared API client library, you can:

- **Share code** across different applications
- **Ensure type safety** across your entire stack
- **Centralize API logic** and business rules

## API Client Structure

Our API client is organized as follows:

```
/src
  /lib
    /api
      client.ts             <- FetchClient + FetchHttpError (fetch wrapper)
      index.ts              <- Api class — aggregates sdk/ modules
      /sdk
        user.api.ts         <- queryOptions / mutationOptions factories
        user.type.ts        <- request/response types per module
        post.api.ts
        post.type.ts
  /shared
    /lib
      api.ts                <- singleton: new Api(new FetchClient({ baseURL }))
```

Organize API methods by domain (users, posts, orders, etc.) and keep types close to their implementations.

## Creating API Modules

### Basic Module Structure

Each API module is a class that encapsulates related API calls:

```ts
// src/lib/api/sdk/post.api.ts
import { queryOptions, mutationOptions, keepPreviousData } from '@tanstack/vue-query'
import type { FetchClient } from '../client'
import type { Post, GetPostListParams, CreatePostDto } from './post.type'

export class PostApi {
  private readonly BASE_PATH = '/posts'

  constructor(private readonly client: FetchClient) {}

  list(params: GetPostListParams) {
    return queryOptions({
      queryKey: [this.BASE_PATH, 'list', params],
      queryFn: ({ signal }) => {
        const q = new URLSearchParams()
        if (params.search) q.set('search', params.search)
        if (params.page != null) q.set('page', String(params.page))
        const suffix = q.size ? `?${q}` : ''
        return this.client.get<Post[]>(`${this.BASE_PATH}${suffix}`, { signal })
      },
      placeholderData: keepPreviousData,
    })
  }

  detail(id: number) {
    return queryOptions({
      queryKey: [this.BASE_PATH, 'detail', id],
      queryFn: () => this.client.get<Post>(`${this.BASE_PATH}/${id}`),
    })
  }

  create() {
    return mutationOptions({
      mutationFn: (post: CreatePostDto) => this.client.post<Post>(this.BASE_PATH, post),
    })
  }
}
```

### Registering Your Module

Add your new module to the main API class:

```ts
// src/lib/api/index.ts
import { type FetchClient } from './client'
import { ExampleApi } from './sdk/example.api'
import { PostApi } from './sdk/post.api'

// Export types
export * from './sdk/post.type'

export class Api {
  example: ExampleApi
  post: PostApi

  constructor(private readonly client: FetchClient) {
    this.example = new ExampleApi(this.client)
    this.post = new PostApi(this.client)
  }
}
```

### Singleton Initialization

```ts
// src/shared/lib/api.ts
import { Api, FetchClient } from '@/lib/api'

const baseURL = import.meta.env.VITE_API_BASE_URL ?? '/api'
export const api = new Api(new FetchClient({ baseURL }))
```

## Using the API Client

### In Components

```vue
<script setup lang="ts">
import { toRef } from 'vue'
import { useQuery, useMutation, useQueryClient } from '@tanstack/vue-query'
import { api } from '@/shared/lib/api'
import { toast } from '@/ui/components/sonner'

const props = defineProps<{ postId: number }>()

const postQuery = useQuery(api.post.detail(toRef(props, 'postId')))
const createMutation = useMutation(api.post.create())

const handleCreate = (data: CreatePostDto) => {
  createMutation.mutate(data, {
    onSuccess: () => toast.success('Post created!'),
  })
}
</script>
```

## Query vs Mutation: When to Use What?

Using `queryOptions` or `mutationOptions` helps us easily reuse queryKeys or queryFunctions.

| Action | Use |
|--------|-----|
| Read data (GET) | `useQuery` |
| Create / Update / Delete | `useMutation` |
| User-triggered export (click to download) | `useMutation` — even though no data is mutated |
| Paginated or filtered lists | `useQuery` with `placeholderData: keepPreviousData` |

**Decision rule:** If data loading is triggered by a **click or submit event**, use `useMutation`. If data loads automatically based on component state or URL params, use `useQuery`.

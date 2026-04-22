---
title: Vue Folder Structure
description: An overview of the recommended folder structure for scalable Vue applications
---

Separation of concerns isn't about splitting up tech layers, it's about focusing on what matters most: user outcomes.

## Why Feature-Based Structure

When organizing code, simplicity should guide every decision. While countless architectural patterns exist — from flat structures to atomic design, feature-sliced architectures to micro-frontends — the feature-based (modular) approach strikes the best balance for most teams.

The beauty of organizing code by features is that it mirrors how we think about products. Instead of spreading authentication logic across folders such as `/pages`, `/components`, and `/services`, nearly all authentication-related code is grouped together in an `/auth` module. This co-location makes it easier for developers to find what they need and focus on building features.

## A Balanced Approach

Other patterns have their place. Flat structures work for tiny projects. Atomic design excels for component libraries. Feature-sliced architecture suits enterprise applications. But feature-based organization adapts to most scenarios without the overhead of complex rules or rigid hierarchies.

The goal isn't architectural purity — it's building software that teams can understand and maintain. When your folder structure reflects the features users care about, everyone can navigate the codebase intuitively.

## Implementing Feature-Based Structure in Vue

Here's how you can adopt a feature-oriented architecture in a Vue 3 project.

### 1. Routing Layer

The routing layer sits at the top level of your Vue application and directly maps to your application's URL structure.

```ts
// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/posts',
      component: () => import('@/features/posts/PostList.vue'),
    },
    {
      path: '/posts/:id',
      component: () => import('@/features/posts/PostDetail.vue'),
    },
  ],
})
```

The routing layer is intentionally thin. Each route imports and composes feature components.

### 2. Shared — Your Application's Foundation

The shared folders contain all the common code that's used across multiple features throughout your application.

```
/src
  /shared
    /lib
      api.ts                <- singleton: new Api(new FetchClient({ baseURL }))
  /lib
    /api
      client.ts             <- FetchClient + FetchHttpError (fetch wrapper)
      index.ts              <- Api class — aggregates sdk/ modules
      /sdk
        *.api.ts            <- queryOptions / mutationOptions factories
        *.type.ts           <- request/response types per module
    query-client.ts         <- TanStack QueryClient
  /ui
    /components             <- design system (Button, Input, Dialog, … — Reka UI)
    /composables            <- UI composables (e.g. useNProgress)
    /lib                    <- UI utils (cn, …)
```

| Folder | Content |
|--------|---------|
| `config/` | Application-wide configuration files (feature flags) |
| `consts/` | Global constants (enums, status codes, default values) |
| `lib/` | Utility functions and third-party library configurations (API clients, formatters) |
| `hooks/` | Custom composables that provide common functionality (`useDebounce`, `useLocalStorage`) |

### 3. Features — Vertical Slices of Functionality

The features folder is where the magic happens. Each feature represents a complete vertical slice of your application, containing everything needed to deliver a specific piece of business functionality.

```
/src
  /features
    /posts
      PostList.vue          <- list view
      PostDetail.vue        <- detail view
      PostForm.vue          <- create/edit form
      usePosts.ts           <- composable for post-related queries
      post.types.ts         <- types, schemas, constants
    /users
      UserProfile.vue
      useUser.ts
      user.types.ts
```

Each feature (users, posts, comments) maintains its own internal organization:

| File/Folder | Purpose |
|-------------|---------|
| `consts/` | Feature-specific constants |
| `*.vue` | UI components used within this feature |
| `use*.ts` | Custom composables that manage stateful logic for this feature |
| `*.types.ts` | Types, Zod schemas, and interfaces for this feature's domain |

### 4. Putting It All Together

```
/src
  /features
    /posts
      PostList.vue
      PostDetail.vue
      PostForm.vue
      usePosts.ts
      post.types.ts
    /users
      UserProfile.vue
      useUser.ts
      user.types.ts
  /shared
    /lib
      api.ts
  /lib
    /api
      client.ts
      index.ts
      /sdk
        post.api.ts
        post.type.ts
        user.api.ts
        user.type.ts
    query-client.ts
  /ui
    /components
    /composables
    /lib
  /router
    index.ts
  /assets
  App.vue
  main.ts
```

Feature-based architecture organizes by business domain. This means:

- **Locality of Behavior** — Everything related to "posts" lives in the posts feature. Need to understand how posts work? Look in one place.
- **Independent Development** — Teams can work on different features with minimal conflicts and coordination overhead.
- **Easier Deletion** — When a feature is deprecated, you can delete one folder instead of hunting through dozens of technical folders.
- **Scalability** — As your app grows to dozens or hundreds of features, the structure remains navigable because each feature is self-contained.

### 5. Dependency Rules and Best Practices

**Dependency Rules:**

| Layer | Depends On | Must Not Depend On |
|-------|-----------|-------------------|
| Routing | Features, Shared | — |
| Features | Shared (`@/ui`, `@/shared/lib/*`) | Other features, Routing |
| Shared | Nothing internal | Features, Routing |
| UI | Nothing | Features, Shared |

Following these rules also makes it easier to decide whether a file belongs in the features or shared folder. Since one feature cannot depend on another, if you have a component used by multiple features, it's best to move it to the shared folder instead of keeping it inside any single feature.

### When to Break the Rules

No architecture approach is perfect — they're all just tools to help you. It's okay to break the rules when:

- Building a proof of concept where speed matters more than structure
- The project is very small and strict organization isn't needed (like this documentation site)

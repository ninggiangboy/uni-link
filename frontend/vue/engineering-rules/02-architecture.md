# PART 1 — ARCHITECTURE

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


---

### RULE-ARCH-01 · Layered Architecture [CRITICAL]

**Intent:** Enforce unidirectional data flow and clear separation of concerns.

**Required flow:**
```
Component → Composable → Service → Backend
```

**Layer responsibilities:**

| Layer       | Responsibility                      | Forbidden                        |
|-------------|--------------------------------------|----------------------------------|
| Component   | Render UI, handle user events        | Business logic, API calls        |
| Composable  | Encapsulate reusable reactive logic  | DOM manipulation, direct API calls |
| Service     | HTTP communication                  | Reactive state, Vue imports      |
| Store       | Shared cross-component state        | UI logic, DOM access, API calls  |
| Types       | Data contracts (interfaces/types)   | Logic of any kind                |

---

### RULE-ARCH-02 · File Structure

**Current repository layout (canonical):**
```
/src
  /lib
    query-client.ts         <- TanStack QueryClient (registered in main.ts)
    /api
      client.ts             <- FetchClient + FetchHttpError (fetch wrapper)
      index.ts              <- Api class — aggregates sdk/ modules
      /sdk
        *.api.ts            <- queryOptions / mutationOptions factories (TanStack)
        *.type.ts           <- request/response types per module
  /shared
    /lib
      api.ts                <- singleton: `new Api(new FetchClient({ baseURL }))`, baseURL from VITE_API_BASE_URL
  /ui
    /components             <- design system (Button, Input, Dialog, … — typically built on Reka UI)
    /composables            <- UI composables (e.g. useNProgress)
    /lib                    <- UI utils (cn, …)
  /docs                     <- internal component docs & examples
  /router
  /assets
  App.vue
  main.ts
```

**When adding domain / app features (suggested extension):**
```
/src
  /features
    /user
      UserProfile.vue
      useUser.ts
      user.types.ts
```
- Features import UI from `@/ui/...` and the API from `@/shared/lib/api` (or a composable that wraps queries).

**Dependency rules:**
- App and feature code depend on `@/ui` and `@/shared/lib/*` — never the reverse (the UI layer must not import features)
- HTTP modules (`*.api.ts`) use only `FetchClient` / types from `@/lib/api` — no Vue imports
- The `api` singleton is constructed only in `shared/lib/api.ts`

**Placement decision test:**
- Used in a single screen/feature → keep next to that feature (or under `src/lib` if it is infrastructure)
- Shared by 3+ consumers → move the composable to a shared location or `src/ui/composables`

---

### RULE-ARCH-03 · Naming Conventions

| Artefact            | Convention              | Example                  |
|---------------------|-------------------------|--------------------------|
| Component file      | PascalCase              | `UserProfile.vue`        |
| Composable file     | camelCase + `use`       | `useUser.ts`             |
| Service file        | camelCase + Service     | `userService.ts`         |
| API module file     | camelCase + Api         | `userApi.ts`             |
| Store file          | camelCase + Store       | `useUserStore.ts`        |
| Types file          | camelCase + .types      | `user.types.ts`          |
| Constants file      | camelCase + .consts     | `user.consts.ts`         |
| Interface           | PascalCase              | `interface User {}`      |
| Type alias          | PascalCase              | `type UserId = number`   |
| Constants           | UPPER_SNAKE_CASE        | `const MAX_RETRY = 3`    |
| Event handlers      | camelCase + `handle`    | `handleSubmit`           |

---

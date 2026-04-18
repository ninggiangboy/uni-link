# PART 4 — SERVICES & API CLIENT

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


---

### RULE-SVC-01 · Service Layer Contract [CRITICAL]

Services are plain async functions with no Vue reactivity — HTTP only via `FetchClient` (or an injected client).

```ts
// ✅ PATTERN — e.g. userService.ts (shares the same client pattern as Api)
import type { User, UpdateUserDto } from './user.types'
import type { FetchClient } from '@/lib/api'

export async function getUser(client: FetchClient, id: number): Promise<User> {
  return client.get<User>(`/users/${id}`)
}

export async function updateUser(
  client: FetchClient,
  id: number,
  payload: UpdateUserDto,
): Promise<User> {
  return client.put<User>(`/users/${id}`, payload)
}

export async function deleteUser(client: FetchClient, id: number): Promise<void> {
  await client.delete(`/users/${id}`)
}
```

**Rules:**
- Raw `fetch` lives only in `src/lib/api/client.ts` (`FetchClient`)
- Always return explicit types — never `any`
- No `ref` / `reactive` / `computed` in services
- HTTP failures throw `FetchHttpError` — handle in composables or mutation `onError`

---

### RULE-SVC-02 · API Client Setup

HTTP is centralised in `src/lib/api/client.ts` — `FetchClient` is the **only** place that calls `fetch` for the standard API flow.

Register the base URL and the `api` singleton in `src/shared/lib/api.ts` (import from features / composables).

```ts
// src/shared/lib/api.ts (as in this repo)
import { Api, FetchClient } from '@/lib/api'

const baseURL = import.meta.env.VITE_API_BASE_URL ?? '/api'

export const api = new Api(new FetchClient({ baseURL }))
```

`FetchClient` (illustrative sketch — see the full file in the repo):

```ts
// src/lib/api/client.ts — essence
export class FetchHttpError extends Error {
  constructor(
    readonly status: number,
    readonly body: string,
  ) {
    super(`HTTP ${status}: ${body.slice(0, 200)}`)
    this.name = 'FetchHttpError'
  }
}

export class FetchClient {
  constructor(private readonly options: FetchClientOptions = {}) {}

  async request<T>(path: string, init: RequestInit = {}): Promise<T> {
    // join baseURL + path, merge headers, JSON body when present
    const res = await fetch(url, { ...init, headers, method, body })
    const text = await res.text()
    if (!res.ok) throw new FetchHttpError(res.status, text)
    // parse JSON or return string / undefined
  }

  get<T>(path: string, init?: Omit<RequestInit, 'method' | 'body'>) {
    return this.request<T>(path, { ...init, method: 'GET' })
  }
  // post, put, patch, delete — same pattern
}
```

**Note:** Pass `signal` via `init` on `get`/`post`/… so requests cancel when the query key changes (RULE-QUERY-06).

---

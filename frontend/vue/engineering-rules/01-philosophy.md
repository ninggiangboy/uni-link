# PART 0 — PHILOSOPHY

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


These principles apply to all code in this project. They inform every rule that follows.

---

### PHIL-01 · Write Simple Code

**Reduce cognitive load above all else.** Every line of code should be immediately understandable to any developer on the team.

- Avoid clever solutions — if you need to explain your code, it is probably too complex
- Favour explicit over implicit — magic is great in fantasy novels, terrible in codebases
- Prefer obvious over elegant — code that reads like plain English is better than code that is "clever"

```ts
// ❌ Clever — high cognitive load
const isEven = (n: number) => !(n & 1)

// ✅ Obvious — zero cognitive load
function isEven(n: number): boolean {
  return n % 2 === 0
}
```

```ts
// ❌ Complex condition — reader must hold 4 things in memory simultaneously
if (val > someConstant && (condition2 || condition3) && (condition4 && !condition5)) { ... }

// ✅ Named variables — each step is readable independently
const isValid = val > someConstant
const isAllowed = condition2 || condition3
const isSecure = condition4 && !condition5
if (isValid && isAllowed && isSecure) { ... }
```

---

### PHIL-02 · Avoid Hasty Abstractions (AHA Programming)

> Prefer duplication over the wrong abstraction.

Code duplication is often **preferable** to premature abstraction.

- Duplication is obvious — you can see exactly what is happening
- Wrong abstractions are expensive — they create complexity that is hard to unwind
- Patterns emerge naturally — after writing similar code 2–3 times, the right abstraction becomes clear
- Change is easier — modifying duplicated code is straightforward; modifying wrong abstractions requires understanding the entire system

**When to abstract:**
- ✅ After 2–3 instances of similar code
- ✅ When the pattern is stable and unlikely to change
- ✅ When the abstraction is obvious to the entire team
- ❌ Before you understand the full scope of use cases
- ❌ To satisfy DRY dogma without clear benefits

---

### PHIL-03 · Prefer URL State Over Pinia for UI State

Global state (Pinia) creates invisible dependencies. For state that comes from user interactions, prefer URL query params via `vue-router`.

```ts
// ❌ Global store for UI state — invisible, not shareable
const { searchQuery } = useSearchStore()
const { currentPage } = usePaginationStore()

// ✅ URL state — shareable, debuggable, bookmarkable
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const searchQuery = computed(() => route.query.q as string ?? '')
const page = computed(() => Number(route.query.page) ?? 1)

function setSearch(q: string) {
  router.push({ query: { ...route.query, q } })
}
```

**Benefits of URL state:**
- Shareable — users can bookmark and share specific states
- Debuggable — app state is visible in the browser URL
- Testable — test different states by changing URLs
- Predictable — state changes are visible and trackable

**When Pinia is appropriate:**
- UI-only state that should NOT be in the URL (modals open/closed, dropdowns)
- Theme preferences (dark/light mode)
- Layout configuration (sidebar collapsed, panel sizes)
- Auth tokens and user session data

---

### PHIL-04 · Obsess Over User Experience

That 2px difference you think nobody will notice? Users feel it.

**Perfect pixel UI:**
- Match designs exactly — use dev tools to measure spacing, font sizes, line heights, radii
- Cross-browser and device testing — verify on Safari, Chrome, Firefox, and real mobile hardware
- Interactive polish — implement hover states, focus rings, pressed/disabled styles, micro-interactions

**Always provide feedback:**
- Loading: prefer skeleton screens over spinners for initial loads
- Mutations: always show a loading indicator during create / update / delete
- Errors: say what happened and how to fix it — avoid jargon
- Success: confirm actions with toasts (`toast` from `@/ui/components/sonner`) without interrupting flow

**Prevent layout shift:**
- Always set explicit dimensions for images and videos
- Match skeleton dimensions to final content to avoid jumps
- Animate with `transform`/`opacity` only — never animate layout-affecting properties (`width`, `height`, `top`)

---

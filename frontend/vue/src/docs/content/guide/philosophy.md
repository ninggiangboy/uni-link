---
title: Philosophy
description: Our guiding principles for building maintainable, performant, and beautiful user interfaces
---

Our approach to building user interfaces is guided by a simple principle: **simplicity over complexity**. We believe that the best code is the code that's easiest to understand, maintain, and modify. This philosophy influences every decision we make, from component architecture to state management.

## 1. Write Simple Code

The best code isn't the most elegant or sophisticated — it's the code that future developers (including yourself) can understand quickly.

We prioritize cognitive load reduction above all else. Every line of code should be immediately understandable to any developer on the team. This means:

- **Avoid clever solutions** — If you need to explain your code to someone, it's probably too complex
- **Favor explicit over implicit** — Magic is great in fantasy novels, terrible in codebases
- **Prefer obvious over elegant** — Code that reads like plain English is better than code that's "clever"

### Example: Simple vs. Complex

```ts
// ❌ Complex
if (val > someConstant && (condition2 || condition3) && (condition4 && !condition5)) {
  // ...
}

// ✅ Simple
const isValid = val > someConstant
const isAllowed = condition2 || condition3
const isSecure = condition4 && !condition5
if (isValid && isAllowed && isSecure) {
  // ...
}
```

## 2. Avoid Hasty Abstractions

> Prefer duplication over the wrong abstraction — Sandi Metz

Following the AHA Programming principle (Avoid Hasty Abstractions), we believe that code duplication is often preferable to premature abstraction. Here's why:

- **Duplication is obvious** — You can see exactly what's happening
- **Wrong abstractions are expensive** — They create complexity that's hard to unwind
- **Patterns emerge naturally** — After you've written similar code 2–3 times, the right abstraction becomes clear
- **Change is easier** — Modifying duplicated code is straightforward; modifying wrong abstractions requires understanding the entire system

### When to Abstract

- ✅ After 2–3 instances of similar code
- ✅ When the pattern is stable and unlikely to change
- ✅ When the abstraction is obvious to the entire team
- ❌ Before you understand the full scope of use cases
- ❌ To satisfy DRY dogma without clear benefits

## 3. Limit watch and watchEffect Usage

`watch` and `watchEffect` are powerful but dangerous. They're often a sign that you're fighting Vue's declarative nature. We prefer alternatives:

### Prefer These Over watch

**Derived state**

```vue
<script setup lang="ts">
// ❌ watch for derived state
const fullName = ref('')
watch([firstName, lastName], ([first, last]) => {
  fullName.value = `${first} ${last}`
})

// ✅ Computed property
const fullName = computed(() => `${firstName.value} ${lastName.value}`)
</script>
```

**Data fetching**

```vue
<script setup lang="ts">
// ❌ watch for API calls on mount
const user = ref<User | null>(null)
watchEffect(async () => {
  user.value = await fetchUser(userId.value)
})

// ✅ TanStack Query
const userQuery = useQuery(api.user.detail(userId))
</script>
```

### When watch is Acceptable

- Synchronizing with external systems (browser APIs, third-party libraries)
- Cleanup operations (subscriptions, timers, event listeners)
- Logging and analytics that can't be handled declaratively

## 4. Prefer URL State Over Pinia for UI State

Global state management libraries (Pinia) create invisible dependencies that make code harder to understand and test. Instead, we prefer:

### URL as State Container

```ts
// ❌ Global store for UI state
const { searchQuery } = useSearchStore()
const { currentPage } = usePaginationStore()

// ✅ URL state with vue-router
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const searchQuery = computed(() => route.query.q as string ?? '')
const currentPage = computed(() => Number(route.query.page) ?? 1)

function setSearch(q: string) {
  router.push({ query: { ...route.query, q } })
}
```

### Benefits of URL State

- **Shareable** — Users can bookmark and share specific states
- **Debuggable** — App state is visible in the browser URL
- **Testable** — Easy to test different states by changing URLs
- **Predictable** — State changes are visible and trackable

### When Pinia is Acceptable

- UI-only state that shouldn't be in the URL (modals, dropdowns)
- Theme preferences (light/dark mode, color schemes)
- Layout configuration (sidebar collapsed state, panel sizes)
- Auth tokens and user session data

## 5. Obsess Over User Experience

That 2px difference you think nobody will notice? Users feel it. They may not know exactly what's wrong, but something feels off, and they leave.

### Perfect Pixel UI

- **Match designs exactly** — Use dev tools to measure spacing, font sizes, line heights, and radii
- **Cross-browser and device testing** — Verify on Safari, Chrome, Firefox, and real iOS/Android hardware
- **Interactive polish** — Implement hover states, focus rings, pressed/disabled styles, and micro-interactions
- **Design tokens** — Centralize spacing, colors, and typography tokens to enforce consistency

### Always Provide Feedback

**Loading States**

- **Skeletons**: Prefer skeleton screens over blank spaces to reduce perceived wait time
- **Loading indicators**: Display a loading indicator during any mutation (such as create, update, or delete actions)

**Error Handling**

- **Human-friendly**: Say what happened and how to fix it, avoid jargon
- **Inline validation**: Validate as users type, place messages near the field with clear guidance

**Success States**

- **Affirmation**: Confirm actions with toasts (`toast` from `@/ui/components/sonner`) without interrupting flow

### Prevent Layout Shift

- **Explicit dimensions**: Always set width/height for images and videos
- **Skeleton sizing**: Match skeleton dimensions to final content to avoid jumps
- **Performant animations**: Animate with `transform`/`opacity`, avoid layout-affecting properties (`width`, `height`, `top`)

## Summary

Our philosophy can be distilled into these core principles:

- **Simplicity first** — Reduce cognitive load at every opportunity
- **Embrace duplication** — Wait for patterns to emerge naturally
- **Minimize side effects** — Prefer `computed` over `watch`/`watchEffect`
- **URL as state** — Make app state visible and shareable with vue-router
- **Obsess Over User Experience** — Attention to detail builds trust

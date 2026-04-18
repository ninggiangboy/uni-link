# PART 2 — COMPONENTS

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


---

### RULE-COMP-01 · Always Use `<script setup>` [CRITICAL]

```vue
<!-- ✅ REQUIRED -->
<script setup lang="ts">
const props = defineProps<{ userId: number }>()
</script>

<!-- ❌ FORBIDDEN -->
<script lang="ts">
export default defineComponent({ ... })
</script>
```

---

### RULE-COMP-02 · Components Are UI-Only [CRITICAL]

**A component must not contain:**
- Business logic
- API calls (raw `fetch` or `FetchClient` outside the API/service layer)
- Data transformation (`filter`, `map`, `reduce` on domain data)
- Complex conditionals about domain rules

**Decision test:** If the code would survive a UI framework change, it belongs in a composable or service — not in a component.

```vue
<!-- ❌ VIOLATION: business logic in component -->
<script setup lang="ts">
const activeUsers = users.value.filter(u => u.role !== 'banned' && u.verified)
</script>

<!-- ✅ CORRECT: delegate to composable -->
<script setup lang="ts">
const { activeUsers } = useUserList()
</script>
```

---

### RULE-COMP-03 · Typed Props and Emits [CRITICAL]

```ts
// ✅ REQUIRED
const props = defineProps<{
  userId: number
  label?: string
}>()

const emit = defineEmits<{
  (e: 'save', value: User): void
  (e: 'cancel'): void
}>()

// ❌ FORBIDDEN — untyped props
const props = defineProps(['userId', 'label'])
```

---

### RULE-COMP-04 · Component Internal Order

Organize component code in this consistent order for readability:

```vue
<script setup lang="ts">
// 1. Props and emits
const props = defineProps<{ userId: number }>()
const emit = defineEmits<{ (e: 'save'): void }>()

// 2. Store access
const userStore = useUserStore()
const { currentUser } = storeToRefs(userStore)

// 3. Data fetching (TanStack Query) — e.g. api from @/shared/lib/api
const userQuery = useQuery(api.user.detail(toRef(props, 'userId'))) // add UserApi + property on Api class when you have it
const updateMutation = useMutation(api.user.update())

// 4. Additional composable calls
const { clipboard } = useCopyToClipboard()

// 5. Derived / computed state
const displayName = computed(() => userQuery.data?.name ?? 'Unknown')

// 6. Event handlers
const handleSave = (data: UpdateUserDto) => {
  updateMutation.mutate({ id: props.userId, data })
}

// 7. Watchers and lifecycle (keep minimal — prefer composables)
onMounted(() => {
  // only for things that cannot go in a composable
})
</script>
```

---

### RULE-COMP-05 · Early Return Pattern for Conditional Rendering

Use early returns to reduce nesting and handle loading/error states cleanly.

```vue
<!-- ✅ CORRECT: early returns, minimal nesting -->
<script setup lang="ts">
import { Spinner } from '@/ui/components/spinner'
</script>
<template>
  <div v-if="userQuery.isLoading" class="flex justify-center p-8">
    <Spinner />
  </div>
  <p v-else-if="userQuery.isError" class="text-sm text-destructive">
    {{ userQuery.error?.message ?? 'Something went wrong' }}
  </p>
  <p v-else-if="!userQuery.data" class="text-sm text-muted-foreground">No user found</p>
  <UserCard v-else :user="userQuery.data" />
</template>

<!-- ❌ VIOLATION: deeply nested ternaries -->
<template>
  <div>
    <template v-if="userQuery.isLoading"><Spinner /></template>
    <template v-else>
      <template v-if="userQuery.isError">...</template>
      <template v-else>
        <template v-if="!userQuery.data">...</template>
        <template v-else><UserCard /></template>
      </template>
    </template>
  </div>
</template>
```

---

### RULE-COMP-06 · Component Size Limit

- Hard limit: **300 lines** per `.vue` file
- Soft limit: **200 lines** — above this, consider splitting
- If a component has >5 props OR >4 emits, it likely needs to be split

---

### RULE-COMP-07 · No Inline Arrow Functions in Templates

```vue
<!-- ❌ VIOLATION: new function created every render -->
<button @click="() => handleDelete(item.id)">Delete</button>

<!-- ✅ CORRECT -->
<button @click="handleDelete(item.id)">Delete</button>
```

---

### RULE-COMP-08 · Never Mutate Props [CRITICAL]

```ts
// ❌ VIOLATION
props.user.name = 'new name'

// ✅ CORRECT
emit('update:user', { ...props.user, name: 'new name' })
```

---

### RULE-COMP-09 · Design-System Components First [CRITICAL]

Shared primitives and composites must come from `@/ui/components/...` (or the `@/ui` barrel if re-exported). Do not duplicate Button/Input/Dialog per feature.

**Required:**
- Use `Button`, `Input`, `Label`, `Dialog`, … from `@/ui/components/<domain>` (see `src/docs/examples/`)
- Add new primitives under `src/ui/components/` when the existing API is insufficient — prefer extending existing components
- Keep tokens, spacing, and focus rings in the design system; avoid ad-hoc class stacks in features

**Forbidden:**
- Creating `UserButton.vue` / `ProfileInput.vue` when `Button` / `Input` already exist
- Repeating raw `<button>` / `<input>` styling across features
- Importing third-party primitives (e.g. `reka-ui`) directly in features — wrap them in `@/ui` first

```vue
<!-- ❌ VIOLATION: feature-local primitive + repeated styling -->
<template>
  <button class="rounded-md bg-blue-600 px-4 py-2 text-white">Save</button>
  <input class="h-10 w-full rounded border px-3" />
</template>

<!-- ✅ CORRECT: consume @/ui primitives -->
<script setup lang="ts">
import { Button } from '@/ui/components/button'
import { Input } from '@/ui/components/textfield'
</script>

<template>
  <Button type="button">Save</Button>
  <Input placeholder="Enter value" />
</template>
```

---

### RULE-COMP-10 · Reka UI via `@/ui` Wrappers Only [CRITICAL]

The approved headless layer is **Reka UI** (`reka-ui`). Use it directly only inside `src/ui/components/*` when building or extending the design system — app/feature screens import wrapped components from `@/ui`.

```vue
<!-- ❌ VIOLATION: Reka UI used directly in a feature -->
<script setup lang="ts">
import { DialogRoot, DialogContent } from 'reka-ui'
</script>

<!-- ✅ CORRECT: feature imports the design-system wrapper -->
<script setup lang="ts">
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/ui/components/dialog'
</script>

<template>
  <Dialog v-model:open="isOpen">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Confirm delete</DialogTitle>
      </DialogHeader>
      ...
    </DialogContent>
  </Dialog>
</template>
```

**Rule:** For new primitives (Menu, Listbox, …), add or extend them under `src/ui/components/`; features import only from `@/ui`.

---

### RULE-COMP-11 · Design-System Component Contract

Components under `src/ui/components/*` should have:
- Typed props + emits (RULE-COMP-03)
- Sensible accessibility defaults (keyboard, ARIA, focus ring)
- A consistent API (`size`, `variant`, `disabled`, … where applicable)
- No business logic or API calls

Example structure (as in this repo):
```
src/ui/components/
  button/
  textfield/
  dialog/
  field/
  ...
```

---

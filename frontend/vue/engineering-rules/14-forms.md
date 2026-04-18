# PART 13 — FORM MANAGEMENT (VEEVALIDATE + ZOD)

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


VeeValidate with Zod is the **mandatory solution for all forms**. It replaces manual `ref`-based field state, ad-hoc validation logic, and direct error management.

**Dependencies (this repo):** `vee-validate`, `@vee-validate/zod`, `zod`, and `reka-ui` are already declared in `package.json`. Add packages only when needed, for example:

```bash
pnpm add vee-validate @vee-validate/zod zod
```

Do not use `@headlessui/vue` in this stack — headless primitives are **Reka UI**, wrapped under `src/ui/components/*`.

**Reference implementations** (copy patterns from here): `src/docs/examples/input/InputForm.vue`, `select/SelectForm.vue`, `checkbox/CheckboxForm.vue`, `switch/SwitchForm.vue`, `textarea/TextAreaForm.vue`, etc.

### Built-in form UI (`@/ui/components/form`)

Import **`useForm` from `@/ui/components/form`** (re-exported from `vee-validate`) together with the layout primitives:

| Export | Role |
|--------|------|
| `Form` | Root `<form>` with `data-slot="form"` — use `@submit="onSubmit"` where `onSubmit = handleSubmit(...)` |
| `FormField` | Wraps VeeValidate `Field`; use `v-slot="{ componentField, errors }"` (add `errors` when you need styling, e.g. `BsSelect`) |
| `FormItem` | Spacing / provider for ids used by label, control, message |
| `FormLabel` | Accessible label tied to the control |
| `FormControl` | Unified control wrapper for all fields. Without `componentField`, slot exposes a11y-only binds (native-style controls). With `:component-field="componentField"` and `generic="…"`, slot exposes typed v-model binds for custom model components (`BsSelect`, `Switch`, `Checkbox`, `CheckboxGroup`) |
| `FormMessage` | Shows field error for the current `FormField` |
| `FormDescription` | Helper text under the label |
| `setSubmitErrors` | Thin helper around `setErrors` for API-shaped field error maps |

**`FormField` + `type="checkbox"`** — use for boolean checkbox / switch-style fields (see `SwitchForm.vue`, single `Checkbox` in `CheckboxForm.vue`).

**Do not** pass the full `componentField` into `Switch` / `Checkbox` / `NumberField`-style components via raw spread — use **`FormControl`** with `:component-field`.

### Field-level Zod (default)

Putting **one giant** `useForm({ validationSchema: toTypedSchema(z.object({…})) })` works for very small forms, but it splits **rules** (form script) from **UI** (template) and makes conditional fields harder (hidden fields may still validate).

**Default in this repo:** validate **next to each field** using Zod on `<FormField :rules="…" />`.

1. **`main.ts`** imports `@/lib/validation/extendZod` once so every `z` schema gets:
   - **`schema.ruleFn()`** — returns VeeValidate **`TypedSchema`** (`@vee-validate/zod`); **default** for `:rules`
   - **`schema.validateFn()`** — returns a **plain function** validator (`GenericValidateFunction`), not a `TypedSchema`; for `{ validate: … }` or mixing rule shapes. Same Zod logic, **different VeeValidate code path** — prefer `ruleFn()` unless you need the object form

2. **Functional equivalents** (no prototype): `ruleFn(schema)` and `zodRules(schema)` from `@/lib/validation`.

```vue
<FormField
  name="email"
  :rules="z.string().email('Invalid email').ruleFn()"
  v-slot="{ componentField }"
>
  …
</FormField>
```

RHF-style object rules (also supported by VeeValidate):

```vue
<FormField name="email" :rules="zodRules(z.string().email())" v-slot="{ componentField }">
```

Or:

```vue
<FormField
  name="email"
  :rules="{ validate: z.string().email().validateFn() }"
  v-slot="{ componentField }"
>
```

**`useForm`** then only needs **`initialValues`** (and global options such as `validateOnMount`). No `validationSchema` unless you intentionally want a second, form-wide pass (rare).

**Cross-field rules** — use a function rule and read sibling values from `ctx.form` (still colocated on the dependent field):

```ts
:rules="{
  validate: (val, ctx) =>
    val === ctx.form.password || 'Must match password',
}"
```

Or combine Zod + form context in a small `validate` function that calls `z.string().safeParse` / custom checks.

**Conditional fields** — wrap optional `FormField` in `v-if`; unmounted fields do not run validation.

**Mental model:**
```
useForm({ initialValues }) only
    ↓
Form → FormField(name, :rules = z…ruleFn()) → FormItem / FormLabel / FormControl → FormMessage
    ↓  optional: useFieldArray / useFormContext
handleSubmit
    ↓
useMutation → API → setSubmitErrors / toast
```

---

### RULE-FORM-01 · Always Use VeeValidate + Zod [CRITICAL]

Manual validation logic (`if (!email.includes('@'))`, hand-rolled `errors` refs) is **forbidden** when VeeValidate is available.

```ts
// ❌ FORBIDDEN: manual validation
const email = ref('')
const emailError = ref('')

function validate() {
  if (!email.value.includes('@')) {
    emailError.value = 'Invalid email'
    return false
  }
  return true
}

// ✅ REQUIRED: Zod + VeeValidate (field-level default)
import { useForm } from '@/ui/components/form'
import { z } from 'zod'

const { handleSubmit } = useForm({
  initialValues: { email: '' },
})
```

```vue
<FormField
  name="email"
  :rules="z.string().email('Invalid email').ruleFn()"
  v-slot="{ componentField }"
>
  …
</FormField>
```

```ts
// ✅ ACCEPTABLE for very small forms only — whole-object schema on useForm
import { useForm } from '@/ui/components/form'
import { toTypedSchema } from '@vee-validate/zod'
import { z } from 'zod'

const { handleSubmit } = useForm({
  validationSchema: toTypedSchema(z.object({ email: z.string().email() })),
  initialValues: { email: '' },
})
```

---

### RULE-FORM-02 · Schema and Types in `*.types.ts` [CRITICAL]

**Intent:** Keep Zod (per-field or object) next to domain types. The `.vue` file imports builders — it does not define schemas inline.

```ts
// features/auth/auth.types.ts

import { z } from 'zod'

// ✅ Per-field schemas (preferred) — use with FormField :rules="emailField.ruleFn()"
export const emailField = z.string().email('Invalid email address')
export const nameField = z.string().min(2, 'Name must be at least 2 characters')

// ✅ Whole-form schema (optional, tiny forms) + inferred values type
export const signUpSchema = z.object({
  email: emailField,
  name: nameField,
})

export type SignUpFormValues = z.infer<typeof signUpSchema>
```

```vue
<!-- ❌ VIOLATION: schema defined inline inside the component -->
<script setup lang="ts">
const schema = z.object({ email: z.string().email() })
</script>

<!-- ✅ CORRECT: import from types file -->
<script setup lang="ts">
import { emailField, nameField, type SignUpFormValues } from './auth.types'
</script>
```

---

### RULE-FORM-03 · Basic Form Pattern (Form shell + `FormField`) [CRITICAL]

The canonical structure matches the design-system examples (`InputForm.vue`, `SelectForm.vue`, `CheckboxForm.vue`, `SwitchForm.vue`, …).

```ts
// features/auth/auth.types.ts
import { z } from 'zod'

export const emailField = z.string().email('Invalid email')
export const nameField = z.string().min(2, 'At least 2 characters')

export type SignUpFormValues = {
  email: string
  name: string
}
```

```vue
<!-- features/auth/SignUpForm.vue -->
<script setup lang="ts">
import { Button } from '@/ui/components/button'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  useForm,
} from '@/ui/components/form'
import { Input } from '@/ui/components/textfield'
import { emailField, nameField, type SignUpFormValues } from './auth.types'

const { handleSubmit } = useForm<SignUpFormValues>({
  initialValues: { email: '', name: '' },
})

const onSubmit = handleSubmit((values) => {
  console.log(values)
})
</script>

<template>
  <Form class="w-full space-y-4" @submit="onSubmit">
    <h2 class="text-xl font-semibold">Sign up</h2>

    <FormField
      v-slot="{ componentField }"
      name="email"
      :rules="emailField.ruleFn()"
    >
      <FormItem>
        <FormLabel>Email</FormLabel>
        <FormControl v-slot="controlProps">
          <Input
            v-bind="{ ...componentField, ...controlProps }"
            placeholder="Enter your email"
          />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <FormField
      v-slot="{ componentField }"
      name="name"
      :rules="nameField.ruleFn()"
    >
      <FormItem>
        <FormLabel>Name</FormLabel>
        <FormControl v-slot="controlProps">
          <Input
            v-bind="{ ...componentField, ...controlProps }"
            placeholder="Enter your name"
          />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <Button type="submit" class="w-full">Sign up</Button>
  </Form>
</template>
```

**Rules:**
- Prefer **`:rules="z…ruleFn()`** (or imported per-field schema) on each `FormField`; omit `validationSchema` on `useForm` unless the form is trivial
- Call `useForm` with a values generic + **`initialValues`** for every field
- Use **`Form`** + **`FormField`** + **`FormMessage`** — no hand-rolled error spans
- Use **`FormControl`** for both native controls and custom model controls
- **`defineField`** is optional and rare

---

### RULE-FORM-04 · Form with Loading (Mutation Integration)

Forms that submit data must integrate with TanStack Query's `useMutation`. Wrap the form body in `<LoadingOverlay>` and pass `mutation.isPending`.

```vue
<script setup lang="ts">
import { toTypedSchema } from '@vee-validate/zod'
import { useMutation } from '@tanstack/vue-query'
import { api } from '@/shared/lib/api'
import { toast } from '@/ui/components/sonner'
import { LoadingOverlay } from '@/ui/components/loading-overlay'
import { Button } from '@/ui/components/button'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  useForm,
} from '@/ui/components/form'
import { Input } from '@/ui/components/textfield'
import { signUpSchema, type SignUpFormValues } from './auth.types'

const { handleSubmit } = useForm<SignUpFormValues>({
  validationSchema: toTypedSchema(signUpSchema),
  initialValues: { email: '', name: '' },
})

// Add AuthApi.signUp() on the Api class when the endpoint exists
const signUpMutation = useMutation(api.auth.signUp())

const onSubmit = handleSubmit((values) => {
  signUpMutation.mutate(values, {
    onSuccess: () => toast.success('Account created!'),
  })
})
</script>

<template>
  <Form class="space-y-4" @submit="onSubmit">
    <h2 class="text-xl font-semibold">Sign up</h2>

    <LoadingOverlay :is-loading="signUpMutation.isPending">
      <div class="grid gap-4">
        <FormField v-slot="{ componentField }" name="email">
          <FormItem>
            <FormLabel>Email</FormLabel>
            <FormControl v-slot="controlProps">
              <Input v-bind="{ ...componentField, ...controlProps }" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ componentField }" name="name">
          <FormItem>
            <FormLabel>Name</FormLabel>
            <FormControl v-slot="controlProps">
              <Input v-bind="{ ...componentField, ...controlProps }" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>
      </div>
    </LoadingOverlay>

    <Button type="submit" :disabled="signUpMutation.isPending" class="w-full">
      Sign up
    </Button>
  </Form>
</template>
```

**Rules:**
- The submit button must be `:disabled="mutation.isPending"` — always prevent double-submission
- `LoadingOverlay` wraps the **field area only** — the submit button stays outside so the user can see the loading state
- Never use `isSubmitting` from VeeValidate for async loading — use mutation's `isPending` instead, which reflects the actual network request

---

### RULE-FORM-05 · Zod Validation Rules

Use Zod's chainable API for all field constraints. Custom messages must be concise, actionable, and user-facing.

```ts
// auth.types.ts
import { z } from 'zod'

export const registerSchema = z.object({
  email:    z.string().email('Enter a valid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  age:      z.number({ invalid_type_error: 'Age must be a number' }).min(18, 'Must be 18 or older'),
  website:  z.string().url('Enter a valid URL').optional(),
})

export type RegisterFormValues = z.infer<typeof registerSchema>
```

**Rules:**
- Every `z.string().min(n)` must include a human-readable message as the second argument
- `z.string().email()` must always carry a message — the default Zod message is too technical
- Optional fields use `.optional()` on the Zod field, not nullable inputs
- Never write `z.any()` in a form schema — if the type is unknown, use `z.unknown()` and narrow later

---

### RULE-FORM-06 · Dependent Validation (Cross-Field Rules)

When a field's validity depends on another field's value (e.g. password confirmation), add the cross-field rule at the **schema level** using `.refine()` or `.superRefine()` — never in an imperative `watch`.

```ts
// auth.types.ts
import { z } from 'zod'

export const registerSchema = z.object({
  email:           z.string().email('Enter a valid email'),
  password:        z.string().min(8, 'At least 8 characters'),
  confirmPassword: z.string().min(1, 'Please confirm your password'),
}).refine(
  (data) => data.password === data.confirmPassword,
  {
    message: "Passwords don't match",
    path: ['confirmPassword'],   // error is attached to confirmPassword field
  },
)

export type RegisterFormValues = z.infer<typeof registerSchema>
```

```vue
<script setup lang="ts">
import { toTypedSchema } from '@vee-validate/zod'
import { Button } from '@/ui/components/button'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  useForm,
} from '@/ui/components/form'
import { Input } from '@/ui/components/textfield'
import { registerSchema, type RegisterFormValues } from './auth.types'

const { handleSubmit } = useForm<RegisterFormValues>({
  validationSchema: toTypedSchema(registerSchema),
  initialValues: { email: '', password: '', confirmPassword: '' },
})

const onSubmit = handleSubmit((values) => { /* ... */ })
</script>

<template>
  <Form class="space-y-4" @submit="onSubmit">
    <FormField v-slot="{ componentField }" name="email">
      <FormItem>
        <FormLabel>Email</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" type="email" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <FormField v-slot="{ componentField }" name="password">
      <FormItem>
        <FormLabel>Password</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" type="password" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <FormField v-slot="{ componentField }" name="confirmPassword">
      <FormItem>
        <FormLabel>Confirm password</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" type="password" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
    <Button type="submit">Register</Button>
  </Form>
</template>
```

**Rules:**
- Cross-field rules live in `.refine()` / `.superRefine()` on the schema — not in `watch` or event handlers
- Always provide `path` in the refine config so VeeValidate attaches the error to the correct field
- VeeValidate re-runs the full schema on every field change, so cross-field errors update automatically

---

### RULE-FORM-07 · Conditional Fields

When fields appear or disappear based on another field's value, read the condition from `values` (VeeValidate's reactive form state) and use `v-if` to mount/unmount the field. Fields that are not mounted are automatically excluded from validation.

```ts
// user.types.ts
import { z } from 'zod'

export enum UserRole { User = 'user', Admin = 'admin' }

export const createUserSchema = z.object({
  role:        z.nativeEnum(UserRole),
  name:        z.string().min(4, 'At least 4 characters'),
  permissions: z.array(z.string()).optional(),
}).refine(
  (data) => data.role !== UserRole.Admin || (data.permissions ?? []).length > 0,
  {
    message: 'Admin users require at least one permission',
    path: ['permissions'],
  },
)

export type CreateUserFormValues = z.infer<typeof createUserSchema>
```

```vue
<script setup lang="ts">
import { computed } from 'vue'
import { toTypedSchema } from '@vee-validate/zod'
import { Button } from '@/ui/components/button'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  FormControl,
  useForm,
} from '@/ui/components/form'
import { Input } from '@/ui/components/textfield'
import { BsSelect, type BsSelectOption } from '@/ui/components/select'
import { createUserSchema, UserRole, type CreateUserFormValues } from './user.types'

const { handleSubmit, values } = useForm<CreateUserFormValues>({
  validationSchema: toTypedSchema(createUserSchema),
  initialValues: { role: UserRole.User, name: '', permissions: [] },
})

const isAdmin = computed(() => values.role === UserRole.Admin)

const roleOptions: BsSelectOption[] = [
  { id: UserRole.User, name: 'User' },
  { id: UserRole.Admin, name: 'Admin' },
]

const permissionOptions: BsSelectOption[] = [
  { id: 'read', name: 'Read' },
  { id: 'write', name: 'Write' },
]

const onSubmit = handleSubmit((formValues) => { /* ... */ })
</script>

<template>
  <Form class="space-y-4" @submit="onSubmit">
    <FormField v-slot="{ componentField, errors }" name="role">
      <FormItem>
        <FormLabel>Role</FormLabel>
        <FormControl generic="string | undefined" v-slot="vm" :component-field="componentField">
          <BsSelect
            v-bind="vm"
            :options="roleOptions"
            placeholder="Select role"
            :class="errors.length ? 'ring-2 ring-destructive' : ''"
          />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <FormField v-slot="{ componentField }" name="name">
      <FormItem>
        <FormLabel>Name</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <div v-if="isAdmin">
      <FormField v-slot="{ componentField }" name="permissions">
        <FormItem>
          <FormLabel>Permissions</FormLabel>
          <FormControl generic="string[]" v-slot="vm" :component-field="componentField">
            <BsSelect v-bind="vm" multiple :options="permissionOptions" />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>
    </div>

    <Button type="submit">Submit</Button>
  </Form>
</template>
```

**Rules:**
- Read conditional state from `values` returned by `useForm` — never from a separate `ref`
- Wrap the condition in a `computed` — keep template expressions simple (RULE-PERF-01)
- Use `v-if` (not `v-show`) for conditional fields — unmounted fields are excluded from schema validation automatically
- When a conditional field becomes hidden, VeeValidate drops its error and its value resets to `undefined` — account for this in the schema with `.optional()`

---

### RULE-FORM-08 · Field Arrays

Use `useFieldArray` for dynamic lists of grouped fields (e.g. multiple addresses, work history entries).

```ts
// invite.types.ts
import { z } from 'zod'

export const inviteUsersSchema = z.object({
  users: z.array(
    z.object({
      email: z.string().email('Enter a valid email'),
      name:  z.string().min(1, 'Name is required'),
    }),
  ).min(1, 'Add at least one user'),
})

export type InviteUsersFormValues = z.infer<typeof inviteUsersSchema>
```

```vue
<script setup lang="ts">
import { toTypedSchema } from '@vee-validate/zod'
import { Button } from '@/ui/components/button'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  useFieldArray,
  useForm,
} from '@/ui/components/form'
import { Input } from '@/ui/components/textfield'
import { inviteUsersSchema, type InviteUsersFormValues } from './invite.types'

const { handleSubmit, errors } = useForm<InviteUsersFormValues>({
  validationSchema: toTypedSchema(inviteUsersSchema),
  initialValues: { users: [{ email: '', name: '' }] },
})

const { fields, append, remove } = useFieldArray('users')

function addUser() {
  append({ email: '', name: '' })
}

const onSubmit = handleSubmit((values) => { /* ... */ })
</script>

<template>
  <Form class="space-y-4" @submit="onSubmit">
    <h2 class="text-xl font-semibold">Invite Users</h2>

    <!-- ✅ Always use field.key (stable ID from VeeValidate) as the :key -->
    <div
      v-for="(field, index) in fields"
      :key="field.key"
      class="grid grid-cols-[1fr_1fr_auto] gap-3"
    >
      <FormField v-slot="{ componentField }" :name="`users[${index}].email`">
        <FormItem>
          <FormLabel>Email</FormLabel>
          <FormControl v-slot="controlProps">
            <Input
              v-bind="{ ...componentField, ...controlProps }"
              placeholder="Email"
            />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>

      <FormField v-slot="{ componentField }" :name="`users[${index}].name`">
        <FormItem>
          <FormLabel>Name</FormLabel>
          <FormControl v-slot="controlProps">
            <Input
              v-bind="{ ...componentField, ...controlProps }"
              placeholder="Name"
            />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>

      <div class="flex items-end">
        <Button
          v-if="fields.length > 1"
          type="button"
          variant="destructive"
          @click="remove(index)"
        >
          Remove
        </Button>
      </div>
    </div>

    <p v-if="errors.users" class="text-sm font-medium text-destructive">{{ errors.users }}</p>

    <Button type="button" variant="secondary" @click="addUser">+ Add User</Button>

    <hr />

    <div class="flex justify-end gap-3">
      <Button type="submit">Invite Users</Button>
    </div>
  </Form>
</template>
```

**Rules:**
- Always use `field.key` as the `:key` in `v-for` — **never** use the loop `index` (violates AP-05 and causes input state bugs when rows are removed)
- Validate array-level constraints (min length, etc.) in the Zod schema via `.min()` on `z.array()`
- Array-level errors (e.g. `errors.users`) are **not** wired to `FormMessage` (no `FormField` context) — render them with plain text / `text-destructive` like the example above
- Call `remove(index)` from `useFieldArray` — never splice the array manually
- Import **`useFieldArray`** (and optionally **`useFormContext`**) from `@/ui/components/form` alongside **`useForm` / `Form` / `FormField`**

---

### RULE-FORM-09 · Submission Errors (Server-Side Errors)

After a failed mutation, map server error responses back to the form. Two categories of errors exist and each has a distinct UX response:

| Error type | What it is | How to display it |
|---|---|---|
| **Field-specific** | Server rejects a specific value (e.g. "Email already taken") | `setErrors({ fieldName: 'message' })` — appears inline under the field |
| **General** | Server failure unrelated to a specific field (network error, 500, etc.) | `toast.error(...)` — non-blocking notification |

```vue
<script setup lang="ts">
import { toTypedSchema } from '@vee-validate/zod'
import { useMutation } from '@tanstack/vue-query'
import { FetchHttpError } from '@/lib/api'
import { api } from '@/shared/lib/api'
import { toast } from '@/ui/components/sonner'
import { LoadingOverlay } from '@/ui/components/loading-overlay'
import { Button } from '@/ui/components/button'
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  setSubmitErrors,
  useForm,
} from '@/ui/components/form'
import { Input } from '@/ui/components/textfield'
import { signUpSchema, type SignUpFormValues } from './auth.types'

const form = useForm<SignUpFormValues>({
  validationSchema: toTypedSchema(signUpSchema),
  initialValues: { email: '', name: '' },
})

const { handleSubmit } = form

// Add AuthApi.signUp() to the Api class / sdk when the real endpoint exists
const signUpMutation = useMutation(api.auth.signUp())

const onSubmit = handleSubmit((values) => {
  signUpMutation.mutate(values, {
    onSuccess: () => {
      toast.success('Account created!')
    },
    onError: (error) => {
      if (error instanceof FetchHttpError && error.status === 422) {
        try {
          const body = JSON.parse(error.body) as { errors?: Record<string, string> }
          if (body.errors) {
            setSubmitErrors(form, body.errors)
            return
          }
        } catch {
          /* body is not JSON */
        }
      }
      toast.error(error instanceof Error ? error.message : 'Something went wrong')
    },
  })
})
</script>

<template>
  <Form class="space-y-4" @submit="onSubmit">
    <LoadingOverlay :is-loading="signUpMutation.isPending">
      <div class="grid gap-4">
        <FormField v-slot="{ componentField }" name="email">
          <FormItem>
            <FormLabel>Email</FormLabel>
            <FormControl v-slot="controlProps">
              <Input v-bind="{ ...componentField, ...controlProps }" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>
        <FormField v-slot="{ componentField }" name="name">
          <FormItem>
            <FormLabel>Name</FormLabel>
            <FormControl v-slot="controlProps">
              <Input v-bind="{ ...componentField, ...controlProps }" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>
        <Button type="submit" :disabled="signUpMutation.isPending" class="w-full">
          Sign up
        </Button>
      </div>
    </LoadingOverlay>
  </Form>
</template>
```

**Rules:**
- Always check `error instanceof FetchHttpError` before reading `status` / `body` (raw string from the server — often `JSON.parse` for structured errors)
- `setErrors` / `setSubmitErrors` keys must match VeeValidate field names exactly
- After applying field errors, `return` — do not also toast the same failure
- General errors (non-field-specific) must surface via toast — never swallow them silently

---

### RULE-FORM-10 · Large Forms — Split with `useFormContext`

Forms with more than 6–8 fields must be split into sub-section components. Each section accesses the shared form state via `useFormContext`.

```
ParentForm.vue
  ├── ProfileSection.vue    ← useFormContext<ProfileFormValues>()
  ├── WorkHistorySection.vue← useFormContext<ProfileFormValues>() + useFieldArray
  └── NotificationsSection.vue ← useFormContext<ProfileFormValues>()
```

```ts
// profile.types.ts
import { z } from 'zod'

export const profileSchema = z.object({
  isPublic:     z.boolean(),
  name:         z.string().min(2, 'At least 2 characters'),
  bio:          z.string().optional(),
  gender:       z.enum(['male', 'female', 'other', '']),
  enableNotify: z.boolean(),
  notifyType:   z.enum(['all', 'mentions', '']),
  works: z.array(z.object({
    position: z.string().min(1, 'Required'),
    company:  z.string().min(1, 'Required'),
  })),
})

export type ProfileFormValues = z.infer<typeof profileSchema>
```

```vue
<!-- features/profile/ProfileForm.vue — parent owns useForm -->
<script setup lang="ts">
import { toTypedSchema } from '@vee-validate/zod'
import { Form, useForm } from '@/ui/components/form'
import { profileSchema, type ProfileFormValues } from './profile.types'
import ProfileSection from './ProfileSection.vue'
import WorkHistorySection from './WorkHistorySection.vue'
import NotificationsSection from './NotificationsSection.vue'

const { handleSubmit } = useForm<ProfileFormValues>({
  validationSchema: toTypedSchema(profileSchema),
  initialValues: {
    isPublic: false, name: '', bio: '', gender: '',
    enableNotify: false, notifyType: '', works: [],
  },
})

const onSubmit = handleSubmit((values) => { /* submit */ })
</script>

<template>
  <Form class="divide-y" @submit="onSubmit">
    <ProfileSection />
    <WorkHistorySection />
    <NotificationsSection />
    <div class="py-6">
      <button type="submit">Save Profile</button>
    </div>
  </Form>
</template>
```

```vue
<!-- features/profile/ProfileSection.vue — fields only; parent owns useForm -->
<script setup lang="ts">
import { BsSelect, type BsSelectOption } from '@/ui/components/select'
import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  FormControl,
} from '@/ui/components/form'
import { Input } from '@/ui/components/textfield'
import { TextArea } from '@/ui/components/textfield'

const genderOptions: BsSelectOption[] = [
  { id: '', name: 'Select…' },
  { id: 'male', name: 'Male' },
  { id: 'female', name: 'Female' },
  { id: 'other', name: 'Other' },
]
</script>

<template>
  <div class="space-y-4 py-6">
    <h2 class="section-title">Profile</h2>

    <FormField v-slot="{ componentField }" name="name">
      <FormItem>
        <FormLabel>Name</FormLabel>
        <FormControl v-slot="controlProps">
          <Input v-bind="{ ...componentField, ...controlProps }" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <FormField v-slot="{ componentField }" name="bio">
      <FormItem>
        <FormLabel>Bio</FormLabel>
        <FormControl v-slot="controlProps">
          <TextArea v-bind="{ ...componentField, ...controlProps }" />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>

    <FormField v-slot="{ componentField, errors }" name="gender">
      <FormItem>
        <FormLabel>Gender</FormLabel>
        <FormControl generic="string | undefined" v-slot="vm" :component-field="componentField">
          <BsSelect
            v-bind="vm"
            :options="genderOptions"
            placeholder="Gender"
            :class="errors.length ? 'ring-2 ring-destructive' : ''"
          />
        </FormControl>
        <FormMessage />
      </FormItem>
    </FormField>
  </div>
</template>
```

Use this section only under a parent that called `useForm()` (e.g. `ProfileForm.vue`). For imperative API access in the child (`validate`, `values`, …), call `useFormContext<ProfileFormValues>()` from `vee-validate`.

```vue
<!-- features/profile/WorkHistorySection.vue — child with field array -->
<script setup lang="ts">
import { Button } from '@/ui/components/button'
import {
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  useFieldArray,
} from '@/ui/components/form'
import { Input } from '@/ui/components/textfield'

const { fields, append, remove } = useFieldArray('works')

function addWork() {
  append({ position: '', company: '' })
}
</script>

<template>
  <div class="space-y-4 py-6">
    <h2 class="section-title">Work History</h2>

    <div
      v-for="(field, index) in fields"
      :key="field.key"
      class="border rounded p-4 grid grid-cols-2 gap-3"
    >
      <FormField v-slot="{ componentField }" :name="`works[${index}].position`">
        <FormItem>
          <FormLabel>Position</FormLabel>
          <FormControl v-slot="controlProps">
            <Input v-bind="{ ...componentField, ...controlProps }" />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>
      <FormField v-slot="{ componentField }" :name="`works[${index}].company`">
        <FormItem>
          <FormLabel>Company</FormLabel>
          <FormControl v-slot="controlProps">
            <Input v-bind="{ ...componentField, ...controlProps }" />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>
      <div class="col-span-2 flex justify-end">
        <Button type="button" variant="destructive" @click="remove(index)">Delete</Button>
      </div>
    </div>

    <Button type="button" variant="secondary" @click="addWork">+ Add Work</Button>
  </div>
</template>
```

**Rules:**
- Only the **parent component** calls `useForm` — child sections call `useFormContext`
- `useFormContext` must be typed with the same generic as the parent `useForm<T>`
- Never pass the form instance as a prop — `useFormContext` handles this automatically via Vue's provide/inject
- The submit button lives in the parent — child sections render fields only
- Each section component follows RULE-COMP-06 (300-line limit) independently

---

### RULE-FORM-11 · Form Validation Mode

By default VeeValidate validates on submit. Adjust the mode on **`useForm()`** (imported from `@/ui/components/form`) for better UX.

```ts
// ✅ Recommended defaults per use case

// Standard form — validate on submit, then re-validate on change after first submit
const { handleSubmit } = useForm({
  validationSchema: toTypedSchema(schema),
  validateOnMount: false,
})

// Edit form (pre-populated data) — validate eagerly so user sees state immediately
const { handleSubmit } = useForm({
  validationSchema: toTypedSchema(schema),
  initialValues: existingData,
  validateOnMount: true,
})
```

**`FormField` / `Field`** — for per-field validation timing, pass props supported by VeeValidate’s `Field` on `FormField` (e.g. `validateOnBlur`, `validateOnInput`) or fall back to **`defineField`** in script-only setups.

```ts
import { defineField } from 'vee-validate'

// defineField (optional pattern) — fine-grained rules without slot templates
const [email, emailProps] = defineField('email', {
  validateOnBlur: true,
  validateOnChange: false,
  validateOnInput: false,
})
```

**Rules:**
- `validateOnMount: false` is the default for new forms — avoid showing errors before the user has typed anything
- Edit forms with pre-populated data should use `validateOnMount: true`
- Real-time validation (on input) is reserved for fields where immediate feedback genuinely helps the user (e.g. password strength indicator)
- Prefer the **`Form` + `FormField`** layout from RULE-FORM-03; `defineField` is for exceptions

---

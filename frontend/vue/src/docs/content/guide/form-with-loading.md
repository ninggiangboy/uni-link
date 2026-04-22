---
title: Form with Loading
description: Learn how to build a form with a loading overlay, handling form state and UX during async operations
---

## Usage

Simply wrap your form fields with the `LoadingOverlay` component and set `is-loading` when submitting data. If you're using a mutation from TanStack Query, pass the `isPending` state to `is-loading`.

<ComponentPreview name="FormWithLoading" />

## Code Breakdown

### 1. Mutation Integration

```vue
<script setup lang="ts">
import { useMutation } from '@tanstack/vue-query'
import { api } from '@/shared/lib/api'

// Fake mutation for demo — replace with real API call
const signUpMutation = useMutation({
  mutationFn: (data: { email: string; name: string }) =>
    new Promise((resolve) => setTimeout(() => resolve(data), 2000)),
})
</script>
```

### 2. LoadingOverlay Wrapping

```vue
<LoadingOverlay :is-loading="signUpMutation.isPending.value">
  <div class="grid gap-4">
    <!-- Form fields here -->
  </div>
</LoadingOverlay>
```

### 3. Submit Button State

The submit button must be `:disabled="mutation.isPending"` — always prevent double-submission:

```vue
<Button type="submit" :disabled="signUpMutation.isPending.value" class="w-full">
  Sign up
</Button>
```

## Key Points

- `LoadingOverlay` wraps the **field area only** — the submit button stays outside so the user can see the loading state
- Never use `isSubmitting` from VeeValidate for async loading — use mutation's `isPending` instead, which reflects the actual network request
- Always disable the submit button during mutation to prevent double-submission

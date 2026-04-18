# APPENDIX — Architecture Mental Model

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


```
User Interaction
      |
  Component              <- render UI, fire events, handle loading/error display
      |
  Composable (useXxx)    <- reactive logic, wraps TanStack Query or manual state
      |
  Service / API Module   <- pure async HTTP functions, queryOptions/mutationOptions
      |
  Backend API

  ─────────────────────────────────────────────────────────────
  State classification:

  Server state            -> TanStack Query  (cache, sync, background updates)
  URL-appropriate state   -> vue-router      (search, filters, pagination)
  Shared UI state         -> Pinia           (auth session, theme, layout prefs)
  Local UI state          -> ref() in comp   (modal open/closed, tab index)
  Form state              -> VeeValidate     (values, errors, submission state)
  ─────────────────────────────────────────────────────────────
```

```
TanStack Query loading states:
  isLoading          -> first fetch, no cache  -> full skeleton / spinner
  isFetching         -> background refetch     -> NProgress bar, keep content
  mutation.isPending -> mutation running        -> disable button / LoadingOverlay
```

```
Form state flow:
  useForm({ initialValues })  (+ optional form-level toTypedSchema for tiny forms)
      ↓
  useForm / useFormContext
      ↓
  FormField(:rules = z…ruleFn()) → FormControl / FormVm → bound controls
      ↓
  handleSubmit → runs Zod validation first
      ↓
  useMutation.mutate()
      ↓
  onError → setErrors (field errors) | toast (general errors)
```

```
Reactivity budget:
  ref<T>       -> simple values, objects replaced wholesale
  computed     -> derived values (read-only, cached)
  shallowRef   -> large/external objects (chart, editor instances)
  reactive     -> avoid unless deep object reactivity is explicitly needed
```

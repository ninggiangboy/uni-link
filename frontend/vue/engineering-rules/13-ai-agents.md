# PART 12 — AI AGENT INSTRUCTIONS

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


If you are an AI agent generating or reviewing Vue 3 code, follow this protocol:

### Before Generating Code

1. Identify which layer the code belongs to: Component / Composable / Service / Store / Types
2. Determine if the data is **server state** (use TanStack Query) or **client state** (Pinia or URL params)
3. Apply the rules for that layer exclusively
4. Ensure all types are explicit — no `any`, no implicit `any` from missing generics

### While Generating Code

1. Start with types/interfaces, then implement logic
2. For server data → use TanStack Query (`useQuery`, `useMutation`) — never manual ref+watch
3. For manual async → apply RULE-ERR-01 (loading/error/finally pattern)
4. For every composable → return an explicit named object
5. Do not call `fetch` outside `FetchClient`; do not spin up ad-hoc `FetchClient` instances in components (use `api` from `@/shared/lib/api` / query options)
6. For paginated/filtered lists → always include `placeholderData: keepPreviousData` and pass `signal`
7. For forms → VeeValidate + Zod **per field** via **`:rules="schema.ruleFn()"`** (after `extendZod` in `main.ts`); **`useForm` from `@/ui/components/form`** with **`initialValues`**; default UI is **`Form` → `FormField` → `FormItem` / `FormLabel` / `FormControl` or `FormVm` → `FormMessage`** (see PART 13)

### After Generating Code — Self-Review Checklist

- [ ] No `any` types anywhere
- [ ] No prop mutations
- [ ] No API calls outside service layer
- [ ] All composables start with `use`
- [ ] All props/emits are typed with generics
- [ ] All `v-for` have stable `:key` values (not index)
- [ ] Server state uses TanStack Query, not manual ref+watch
- [ ] TanStack Query results accessed as `query.data`, not destructured
- [ ] `loading.value = false` is in `finally` (for manual async only)
- [ ] All VueUse calls are wrapped in composables
- [ ] Destructured store values use `storeToRefs`
- [ ] Template contains no inline computation
- [ ] URL state used for search/filter/pagination — not Pinia
- [ ] List queries include `placeholderData: keepPreviousData`
- [ ] Queries with frequently-changing keys pass `signal`
- [ ] Forms use VeeValidate + Zod; per-field **`:rules`** with **`ruleFn()`** / `zodRules()`; schemas in `*.types.ts`; **`useForm` from `@/ui/components/form`** with **`initialValues`**
- [ ] Form layout uses `Form` / `FormField` / `FormControl` (text) or `FormVm` (`BsSelect`, `Switch`, `Checkbox`, …); not ad-hoc `errors.x` spans
- [ ] Field arrays use stable `field.key` from `useFieldArray` as the `:key`
- [ ] Server errors mapped with `setErrors` or `setSubmitErrors`; unrecognised errors shown as toast
- [ ] Feature UI uses components from `@/ui/components/...` instead of raw primitives
- [ ] `reka-ui` is only used inside `src/ui/components/*`, not imported directly from features

### When Reviewing Existing Code

- Flag every item in the Hard Violations list (AP-01 through AP-18) as a **blocking issue**
- Flag every item in the Soft Violations list (SP-01 through SP-11) as a **non-blocking recommendation**
- Reference rule IDs in feedback (e.g. "Violation of RULE-QUERY-01")

---

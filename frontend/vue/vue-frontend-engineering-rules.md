# Frontend Engineering Rules (hub)

**Stack:** Vue 3 · TypeScript · Vite · Pinia · TanStack Query · VeeValidate · Zod · Tailwind CSS 4 · Reka UI · vue-sonner · Axios (present in dependencies; default HTTP path in this codebase uses `FetchClient` in `src/lib/api/client.ts`)

**Version:** 3.4 (aligned with this repo)

**Target:** AI agents + human engineers

Detailed rules live under [`engineering-rules/`](./engineering-rules/). **Start here**, then open only the chapters you need.

---

## HOW TO READ THESE DOCUMENTS

Each rule follows this format:

- **RULE ID** — unique identifier for referencing
- **Intent** — why this rule exists
- **Required / Forbidden** — explicit DO / DON'T
- **Code example** — canonical pattern to follow
- **Violation example** — what to never produce

Rules marked `[CRITICAL]` are hard constraints. Violations must be flagged and corrected before output is accepted.

---

## Which file should I open? (use cases)

| Use case | Open |
|----------|------|
| Onboarding, product/UI mindset, URL vs Pinia | [01 — Philosophy](./engineering-rules/01-philosophy.md) |
| Folder layout, layers, naming | [02 — Architecture](./engineering-rules/02-architecture.md) |
| `.vue` files: `<script setup>`, props/emits, Reka UI, `@/ui` | [03 — Components](./engineering-rules/03-components.md) |
| `useXxx` composables (non-server) | [04 — Composables](./engineering-rules/04-composables.md) |
| `FetchClient`, services, `api` singleton | [05 — Services & API client](./engineering-rules/05-services-api-client.md) |
| TanStack Query: queries, mutations, keys, loading, invalidation | [06 — Data fetching](./engineering-rules/06-data-fetching.md) |
| Pinia stores | [07 — Pinia](./engineering-rules/07-pinia.md) |
| TypeScript: `any`, models vs aliases, where types live | [08 — TypeScript](./engineering-rules/08-typescript.md) |
| VueUse / browser utilities | [09 — VueUse](./engineering-rules/09-vueuse.md) |
| Templates, `v-for` keys, `v-memo`, `watch` | [10 — Performance](./engineering-rules/10-performance.md) |
| Manual async + loading/error (without TanStack Query) | [11 — Error handling](./engineering-rules/11-error-handling.md) |
| AP-/SP- violation lists, PR review | [12 — Anti-patterns](./engineering-rules/12-anti-patterns.md) |
| AI agent workflow + checklist | [13 — AI agents](./engineering-rules/13-ai-agents.md) |
| VeeValidate + Zod forms | [14 — Forms](./engineering-rules/14-forms.md) |
| Diagrams: state classification, query loading, form flow | [15 — Appendix](./engineering-rules/15-appendix.md) |

**Quick paths**

- **New feature screen:** [02](./engineering-rules/02-architecture.md) → [03](./engineering-rules/03-components.md) → [06](./engineering-rules/06-data-fetching.md) (and [05](./engineering-rules/05-services-api-client.md) if you touch HTTP).
- **New form:** [14](./engineering-rules/14-forms.md) + [06](./engineering-rules/06-data-fetching.md) for submit mutations.
- **Code review / AI review:** [12](./engineering-rules/12-anti-patterns.md) + [13](./engineering-rules/13-ai-agents.md).

---

## Full table of contents

| # | Topic | File |
|---|--------|------|
| 0 | Philosophy | [01-philosophy.md](./engineering-rules/01-philosophy.md) |
| 1 | Architecture | [02-architecture.md](./engineering-rules/02-architecture.md) |
| 2 | Components | [03-components.md](./engineering-rules/03-components.md) |
| 3 | Composables | [04-composables.md](./engineering-rules/04-composables.md) |
| 4 | Services & API client | [05-services-api-client.md](./engineering-rules/05-services-api-client.md) |
| 5 | Data fetching (TanStack Query) | [06-data-fetching.md](./engineering-rules/06-data-fetching.md) |
| 6 | Pinia | [07-pinia.md](./engineering-rules/07-pinia.md) |
| 7 | TypeScript | [08-typescript.md](./engineering-rules/08-typescript.md) |
| 8 | VueUse | [09-vueuse.md](./engineering-rules/09-vueuse.md) |
| 9 | Performance | [10-performance.md](./engineering-rules/10-performance.md) |
| 10 | Error handling | [11-error-handling.md](./engineering-rules/11-error-handling.md) |
| 11 | Anti-pattern reference | [12-anti-patterns.md](./engineering-rules/12-anti-patterns.md) |
| 12 | AI agent instructions | [13-ai-agents.md](./engineering-rules/13-ai-agents.md) |
| 13 | Forms (VeeValidate + Zod) | [14-forms.md](./engineering-rules/14-forms.md) |
| — | Appendix (mental model) | [15-appendix.md](./engineering-rules/15-appendix.md) |

Folder index: [engineering-rules/README.md](./engineering-rules/README.md).

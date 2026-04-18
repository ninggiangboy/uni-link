# PART 9 — PERFORMANCE

> **Hub:** [Frontend Engineering Rules](../vue-frontend-engineering-rules.md)


---

### RULE-PERF-01 · Template Must Contain No Computation

```vue
<!-- ❌ VIOLATION -->
<div>{{ items.filter(i => i.active).map(i => i.name).join(', ') }}</div>

<!-- ✅ CORRECT -->
<div>{{ activeItemNames }}</div>

<script setup lang="ts">
const activeItemNames = computed(() =>
  items.value.filter(i => i.active).map(i => i.name).join(', ')
)
</script>
```

---

### RULE-PERF-02 · Key Strategy [CRITICAL]

```vue
<!-- ❌ FORBIDDEN: index as key causes DOM bugs and broken animations -->
<div v-for="item in list" :key="index">

<!-- ✅ REQUIRED: stable unique ID -->
<div v-for="item in list" :key="item.id">
```

---

### RULE-PERF-03 · `v-memo` Usage

Use `v-memo` only when: (1) the list has >50 items AND (2) each item renders a non-trivial subtree.

```vue
<div v-for="item in list" :key="item.id" v-memo="[item.id, item.status]">
  <HeavyItemCard :item="item" />
</div>
```

Dependencies must be **primitive** and **minimal**. Never pass objects as `v-memo` dependencies.

---

### RULE-PERF-04 · `shallowRef` for Non-Reactive Objects

```ts
// ✅ CORRECT: third-party instances should not be deep-reactive
const chartInstance = shallowRef<ChartJS | null>(null)

// ❌ VIOLATION: unnecessary deep reactivity
const chartInstance = ref<ChartJS | null>(null)
```

---

### RULE-PERF-05 · `v-if` vs `v-show`

| Toggle frequency | Directive |
|-----------------|-----------|
| Rare (auth gates, feature flags) | `v-if` |
| Frequent (tabs, accordions, tooltips) | `v-show` |

---

### RULE-PERF-06 · `watch` vs `watchEffect`

```ts
// ✅ PREFERRED: explicit dependencies, easy to trace
watch(userId, (id) => fetchUser(id))

// ⚠️ Only when dependencies are truly dynamic or unknown
watchEffect(() => {
  if (userId.value) fetchUser(userId.value)
})
```

---

<script setup lang="ts">
import { computed, ref } from 'vue'
import { Checkbox } from '@/ui/components/checkbox'

const checkedItems = ref([false, false, false])

const allChecked = computed(() => checkedItems.value.every(Boolean))
const someChecked = computed(() => checkedItems.value.some(Boolean))

const isIndeterminate = computed(() => someChecked.value && !allChecked.value)

const selectAllModel = computed<boolean | 'indeterminate'>({
  get() {
    if (allChecked.value) return true
    if (isIndeterminate.value) return 'indeterminate'
    return false
  },
  set(next: boolean | 'indeterminate') {
    if (next === 'indeterminate') return
    checkedItems.value = checkedItems.value.map(() => next)
  },
})

function setItem(i: number, v: boolean | string) {
  const next = [...checkedItems.value]
  next[i] = v === true
  checkedItems.value = next
}
</script>

<template>
  <div class="grid gap-2">
    <Checkbox v-model="selectAllModel"> Select all </Checkbox>
    <div class="ml-4 grid gap-2">
      <Checkbox :model-value="checkedItems[0]!" @update:model-value="(v) => setItem(0, v)">
        Item 1
      </Checkbox>
      <Checkbox :model-value="checkedItems[1]!" @update:model-value="(v) => setItem(1, v)">
        Item 2
      </Checkbox>
      <Checkbox :model-value="checkedItems[2]!" @update:model-value="(v) => setItem(2, v)">
        Item 3
      </Checkbox>
    </div>
  </div>
</template>

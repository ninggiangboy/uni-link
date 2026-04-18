<script setup lang="ts">
import { computed, ref } from 'vue'
import { createColumnHelper } from '@tanstack/vue-table'
import { Button } from '@/ui/components/button'
import { DataTable } from '@/ui/components/datatable'

interface User {
  id: string
  name: string
  email: string
  department: string
  role: string
}

const data: User[] = [
  {
    id: '1',
    name: 'Alice Johnson',
    email: 'alice@company.com',
    department: 'Engineering',
    role: 'Senior Developer',
  },
  {
    id: '2',
    name: 'Bob Smith',
    email: 'bob@company.com',
    department: 'Marketing',
    role: 'Marketing Manager',
  },
  {
    id: '3',
    name: 'Carol Davis',
    email: 'carol@company.com',
    department: 'Engineering',
    role: 'Product Manager',
  },
  {
    id: '4',
    name: 'David Wilson',
    email: 'david@company.com',
    department: 'Sales',
    role: 'Sales Representative',
  },
  { id: '5', name: 'Eva Brown', email: 'eva@company.com', department: 'HR', role: 'HR Specialist' },
]

const columnHelper = createColumnHelper<User>()
const columns = [
  columnHelper.accessor('name', { header: 'Name' }),
  columnHelper.accessor('email', { header: 'Email' }),
  columnHelper.accessor('department', { header: 'Department' }),
]

const rowSelection = ref<Record<string, boolean>>({})
const selectedCount = computed(() => Object.keys(rowSelection.value).length)

function bulk(action: string) {
  window.alert(`${action} ${selectedCount.value} selected users`)
}
</script>

<template>
  <div class="w-full space-y-3">
    <div class="flex h-8 items-center justify-between">
      <span class="text-sm text-muted-foreground"
        >{{ selectedCount }} of {{ data.length }} selected</span
      >
      <div v-if="selectedCount > 0" class="flex gap-2">
        <Button variant="destructive" @click="bulk('Delete')"> Delete Selected </Button>
        <Button variant="outline" @click="bulk('Export')"> Export Selected </Button>
      </div>
    </div>
    <DataTable
      enable-row-selection
      :row-selection="rowSelection"
      :columns="columns"
      :data="data"
      @update:row-selection="rowSelection = $event"
    />
  </div>
</template>

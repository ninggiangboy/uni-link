<script setup lang="ts">
import { createColumnHelper } from '@tanstack/vue-table'
import { DataTable } from '@/ui/components/datatable'

interface User {
  id: string
  name: string
  email: string
  balance: string
}

const data: User[] = [
  { id: '1', name: 'John Doe', email: 'john@example.com', balance: '$1.000' },
  { id: '2', name: 'Jane Smith', email: 'jane@example.com', balance: '$2.000' },
  { id: '3', name: 'Bob Johnson', email: 'bob@example.com', balance: '$3.000' },
]

const columnHelper = createColumnHelper<User>()
const columns = [
  columnHelper.accessor('name', { header: 'Name' }),
  columnHelper.accessor('email', { header: 'Email' }),
  columnHelper.accessor('balance', {
    header: 'Balance',
    meta: { className: 'text-right' },
  }),
]
</script>

<template>
  <div class="w-full">
    <DataTable :columns="columns" :data="data" />
  </div>
</template>

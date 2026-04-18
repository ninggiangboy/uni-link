<script setup lang="ts">
import { createColumnHelper } from '@tanstack/vue-table'
import { DataTable } from '@/ui/components/datatable'
import { demoPayments, type DemoPayment } from '@/docs/examples/data-table/paymentsDemoData'

const columnHelper = createColumnHelper<DemoPayment>()
const columns = [
  columnHelper.accessor('email', { header: 'Email', size: 270 }),
  columnHelper.accessor('transactionDate', { header: 'Transaction Date' }),
  columnHelper.accessor('paymentReference', { header: 'Payment Reference' }),
]
</script>

<template>
  <div class="w-full">
    <DataTable :columns="columns" :data="demoPayments" />
  </div>
</template>

<script setup lang="ts">
import { createColumnHelper } from '@tanstack/vue-table'
import { h } from 'vue'
import { Pencil, Trash2 } from 'lucide-vue-next'
import { Button } from '@/ui/components/button'
import { DataTable } from '@/ui/components/datatable'

interface Order {
  id: string
  orderNumber: string
  customer: string
  email: string
  product: string
  quantity: number
  price: number
  status: 'pending' | 'processing' | 'shipped' | 'delivered' | 'cancelled'
  orderDate: string
  total: number
}

const data: Order[] = [
  {
    id: '1',
    orderNumber: 'ORD-001',
    customer: 'John Smith',
    email: 'john@example.com',
    product: 'Laptop Pro 15"',
    quantity: 1,
    price: 1299.99,
    status: 'delivered',
    orderDate: '2024-01-15',
    total: 1299.99,
  },
  {
    id: '2',
    orderNumber: 'ORD-002',
    customer: 'Sarah Johnson',
    email: 'sarah@example.com',
    product: 'Wireless Mouse',
    quantity: 2,
    price: 29.99,
    status: 'shipped',
    orderDate: '2024-01-16',
    total: 59.98,
  },
  {
    id: '3',
    orderNumber: 'ORD-003',
    customer: 'Mike Wilson',
    email: 'mike@example.com',
    product: 'Mechanical Keyboard',
    quantity: 1,
    price: 149.99,
    status: 'processing',
    orderDate: '2024-01-17',
    total: 149.99,
  },
]

const columnHelper = createColumnHelper<Order>()
const columns = [
  columnHelper.accessor('orderNumber', { header: 'Order #', size: 90 }),
  columnHelper.accessor('customer', { header: 'Customer', size: 150 }),
  columnHelper.accessor('email', { header: 'Email', size: 200 }),
  columnHelper.accessor('product', { header: 'Product', size: 180 }),
  columnHelper.accessor('quantity', {
    header: 'Qty',
    size: 80,
    meta: { className: 'text-center' },
  }),
  columnHelper.accessor('price', {
    header: 'Price',
    size: 100,
    cell: (info) => `$${info.getValue().toFixed(2)}`,
    meta: { className: 'text-right' },
  }),
  columnHelper.accessor('orderDate', {
    header: 'Order Date',
    size: 120,
    cell: (info) => new Date(info.getValue()).toLocaleDateString(),
  }),
  columnHelper.accessor('total', {
    header: 'Total',
    size: 100,
    cell: (info) => `$${info.getValue().toFixed(2)}`,
    meta: { className: 'text-right font-medium' },
  }),
  columnHelper.display({
    id: 'actions',
    header: 'Actions',
    cell: () =>
      h('div', { class: 'space-x-1' }, [
        h(
          Button,
          { variant: 'ghost', size: 'icon', class: 'h-8 w-8' },
          { default: () => h(Pencil, { class: 'h-4 w-4 text-primary-foreground' }) },
        ),
        h(
          Button,
          { variant: 'ghost', size: 'icon', class: 'h-8 w-8' },
          { default: () => h(Trash2, { class: 'h-4 w-4 text-destructive-foreground' }) },
        ),
      ]),
    size: 120,
    enableSorting: false,
    meta: { className: 'text-center' },
  }),
]
</script>

<template>
  <div class="w-full">
    <DataTable
      :columns="columns"
      :data="data"
      :column-pinning="{ left: ['orderNumber'], right: ['actions'] }"
    />
  </div>
</template>

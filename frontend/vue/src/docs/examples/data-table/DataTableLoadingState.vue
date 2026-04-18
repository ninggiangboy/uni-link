<script setup lang="ts">
import { ref } from 'vue'
import { createColumnHelper } from '@tanstack/vue-table'
import { Button } from '@/ui/components/button'
import { DataTable } from '@/ui/components/datatable'

interface Product {
  id: string
  name: string
  category: string
  price: number
  stock: number
}

const data: Product[] = [
  { id: '1', name: 'Laptop Pro', category: 'Electronics', price: 1299.99, stock: 15 },
  { id: '2', name: 'Wireless Mouse', category: 'Accessories', price: 29.99, stock: 50 },
  { id: '3', name: 'Mechanical Keyboard', category: 'Accessories', price: 149.99, stock: 25 },
]

const columnHelper = createColumnHelper<Product>()
const columns = [
  columnHelper.accessor('name', { header: 'Product Name' }),
  columnHelper.accessor('category', { header: 'Category' }),
  columnHelper.accessor('price', {
    header: 'Price',
    cell: (info) => `$${info.getValue().toFixed(2)}`,
    meta: { className: 'text-right' },
  }),
]

const isLoading = ref(false)

function simulate() {
  isLoading.value = true
  window.setTimeout(() => {
    isLoading.value = false
  }, 1000)
}
</script>

<template>
  <div class="w-full space-y-4">
    <div class="flex gap-2">
      <Button variant="outline" :disabled="isLoading" @click="simulate">
        Simulate Initial Load
      </Button>
    </div>
    <DataTable
      :columns="columns"
      :data="isLoading ? [] : data"
      :is-loading="isLoading"
      container-class-name="h-[180px]"
    />
  </div>
</template>

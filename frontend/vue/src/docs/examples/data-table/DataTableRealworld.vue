<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { createColumnHelper } from '@tanstack/vue-table'
import { CreditCard, Pencil, Trash2, X } from 'lucide-vue-next'
import { h } from 'vue'
import { Button } from '@/ui/components/button'
import { DataTable } from '@/ui/components/datatable'
import { SearchField } from '@/ui/components/searchfield'
import { Select, type SelectOption } from '@/ui/components/select'
import { Pagination, PaginationPageSizeSelector } from '@/ui/components/pagination'
import { cn } from '@/ui/lib/utils'
import { confirm } from '@/ui/components/confirm-dialog'
import { toast } from '@/ui/components/sonner'
import type { DemoPayment } from '@/docs/examples/data-table/paymentsDemoData'
import { demoPayments as allPayments } from '@/docs/examples/data-table/paymentsDemoData'

const methodOptions: (SelectOption & { color: string })[] = [
  { id: 'debit_card', name: 'Debit Card', color: 'text-green-500 bg-green-500/10' },
  { id: 'credit_card', name: 'Credit Card', color: 'text-blue-500 bg-blue-500/10' },
  { id: 'bank_transfer', name: 'Bank Transfer', color: 'text-yellow-500 bg-yellow-500/10' },
  { id: 'paypal', name: 'Paypal', color: 'text-purple-500 bg-purple-500/10' },
]

const columnHelper = createColumnHelper<DemoPayment>()
const columns = [
  columnHelper.accessor('paymentMethod', {
    header: 'Payment Method',
    cell: ({ getValue }) => {
      const value = getValue()
      const method = methodOptions.find((o) => o.id === value)
      return h('div', { class: 'flex items-center gap-2' }, [
        h(
          'div',
          { class: cn(method?.color, 'rounded-sm p-1.5') },
          h(CreditCard, { class: 'size-4' }),
        ),
        h('span', { class: 'font-medium' }, method?.name ?? ''),
      ])
    },
  }),
  columnHelper.accessor('email', { header: 'Email', size: 270 }),
  columnHelper.accessor('transactionDate', { header: 'Transaction Date' }),
  columnHelper.accessor('paymentReference', { header: 'Payment Reference' }),
  columnHelper.display({
    id: 'actions',
    header: 'Actions',
    cell: () =>
      h('div', { class: 'space-x-1' }, [
        h(
          Button,
          { variant: 'ghost', size: 'icon', 'aria-label': 'edit', class: 'h-8 w-8' },
          { default: () => h(Pencil, { class: 'text-primary-foreground' }) },
        ),
        h(
          Button,
          { variant: 'ghost', size: 'icon', 'aria-label': 'delete', class: 'h-8 w-8' },
          { default: () => h(Trash2, { class: 'text-destructive-foreground' }) },
        ),
      ]),
    size: 100,
    enableSorting: false,
    meta: { className: 'text-center' },
  }),
]

const rowSelection = ref<Record<string, boolean>>({
  '34caaea9-44ee-4519-a6e8-4061f916d4fe': true,
})
const search = ref('')
const page = ref(1)
const pageSize = ref(5)
const paymentMethod = ref<string | undefined>(undefined)

const selectedCount = computed(() => Object.keys(rowSelection.value).length)
const isFiltering = computed(() => !!search.value || !!paymentMethod.value)

const filtered = computed(() => {
  let rows = allPayments
  const q = search.value.trim().toLowerCase()
  if (q) rows = rows.filter((p) => p.email.toLowerCase().includes(q))
  if (paymentMethod.value) rows = rows.filter((p) => p.paymentMethod === paymentMethod.value)
  return rows
})

const totalPages = computed(() => Math.max(1, Math.ceil(filtered.value.length / pageSize.value)))

const pageData = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return filtered.value.slice(start, start + pageSize.value)
})

watch([search, paymentMethod], () => {
  page.value = 1
})

watch(totalPages, (tp) => {
  if (page.value > tp) page.value = tp
})

function clearFilters() {
  search.value = ''
  paymentMethod.value = undefined
  page.value = 1
}

function deleteSelected() {
  confirm({
    variant: 'destructive',
    title: 'Delete Users',
    description: 'Are you sure you want to delete these users?',
    action: {
      label: 'Delete',
      onClick: () => {
        toast.success('Users Deleted Successfully', {
          description: `Deleted ${selectedCount.value} selected users`,
        })
      },
    },
  })
}
</script>

<template>
  <div class="w-full space-y-3">
    <div class="flex flex-wrap gap-2">
      <SearchField
        v-model="search"
        placeholder="Search email…"
        class="max-sm:min-w-0 max-sm:flex-1 rounded-sm border border-input px-2"
      />
      <Select
        v-model="paymentMethod"
        class="w-[155px] max-sm:hidden"
        placeholder="Payment Method"
        :options="methodOptions"
      />
      <Button v-if="isFiltering" class="max-sm:hidden" variant="outline" @click="clearFilters">
        <X class="size-4" />
        Clear
      </Button>
      <Button
        v-if="selectedCount"
        class="ml-auto max-sm:hidden"
        variant="destructive"
        @click="deleteSelected"
      >
        Delete Selected
      </Button>
    </div>
    <DataTable
      enable-sorting
      enable-row-selection
      :row-selection="rowSelection"
      :columns="columns"
      :data="pageData"
      container-class-name="h-[335px]"
      @update:row-selection="rowSelection = $event"
    />
    <div class="flex justify-between gap-4">
      <PaginationPageSizeSelector
        v-model="pageSize"
        @update:model-value="page = 1"
      />
      <Pagination v-model="page" :page-count="totalPages" />
    </div>
  </div>
</template>

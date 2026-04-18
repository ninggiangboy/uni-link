<script setup lang="ts" generic="TData extends { id: string | number }">
import type {
  ColumnDef,
  ColumnPinningState,
  RowSelectionState,
  SortingState,
} from '@tanstack/vue-table'
import { FlexRender, getCoreRowModel, useVueTable } from '@tanstack/vue-table'
import { ArrowDown, ArrowUp, FileSearch } from 'lucide-vue-next'
import { computed, h, toRef } from 'vue'
import { Checkbox } from '@/ui/components/checkbox'
import { Spinner } from '@/ui/components/spinner'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/ui/components/table'
import { cn } from '@/ui/lib/utils'
import type { DataTableSorting } from './dataTableUtils'
import {
  getCommonPinningStyles,
  getDataTableColumnMetaClassName,
  sortRowsByDataTableSorting,
} from './dataTableUtils'

const props = withDefaults(
  defineProps<{
    data: TData[]
    // eslint-disable-next-line @typescript-eslint/no-explicit-any -- TValue must accept heterogeneous accessor columns
    columns: ColumnDef<TData, any>[]
    containerClassName?: string
    enableSorting?: boolean
    isLoading?: boolean
    enableRowSelection?: boolean
    rowSelection?: RowSelectionState
    columnPinning?: ColumnPinningState
  }>(),
  {
    enableSorting: false,
    isLoading: false,
    enableRowSelection: false,
    rowSelection: undefined,
    columnPinning: undefined,
  },
)

const emit = defineEmits<{
  'update:rowSelection': [value: RowSelectionState]
}>()

const sorting = defineModel<DataTableSorting | null>('sorting', { default: null })

const dataRef = toRef(props, 'data')

const sortedData = computed(() => {
  if (!props.enableSorting || !sorting.value) return dataRef.value
  return sortRowsByDataTableSorting(dataRef.value, sorting.value)
})

const tanstackSorting = computed<SortingState>(() => {
  if (!props.enableSorting) return []
  return sorting.value
    ? [{ id: sorting.value.sortBy, desc: sorting.value.sortDirection === 'desc' }]
    : []
})

const selectCheckboxClass = 'gap-0 justify-center'

const selectColumn: ColumnDef<TData, unknown> = {
  id: 'select',
  header: ({ table }) =>
    h(
      'div',
      { class: 'flex h-10 w-full items-center justify-center' },
      h(Checkbox, {
        class: selectCheckboxClass,
        'aria-label': 'Select all',
        modelValue: table.getIsAllRowsSelected()
          ? true
          : table.getIsSomePageRowsSelected()
            ? 'indeterminate'
            : false,
        'onUpdate:modelValue': (v: boolean | 'indeterminate') => {
          table.toggleAllRowsSelected(v === true)
        },
      }),
    ),
  cell: ({ row }) =>
    h(
      'div',
      { class: 'flex w-full items-center justify-center py-3' },
      h(Checkbox, {
        class: selectCheckboxClass,
        'aria-label': 'Select row',
        modelValue: row.getIsSelected(),
        disabled: !row.getCanSelect(),
        'onUpdate:modelValue': (v: boolean | 'indeterminate') => {
          row.toggleSelected(v === true)
        },
      }),
    ),
  enableSorting: false,
  size: 48,
  meta: {
    className: 'w-12 min-w-12 max-w-12 !p-0 text-center align-middle',
  },
}

const displayColumns = computed(() =>
  props.enableRowSelection ? [selectColumn, ...props.columns] : props.columns,
)

const resolvedColumnPinning = computed<ColumnPinningState>(
  () =>
    props.columnPinning ??
    (props.enableRowSelection
      ? { left: ['select'], right: ['actions'] }
      : { left: [], right: [] }),
)

const table = useVueTable({
  get data() {
    return sortedData.value
  },
  get columns() {
    return displayColumns.value
  },
  getRowId: (row) => String(row.id),
  enableRowSelection: props.enableRowSelection,
  enableSorting: props.enableSorting,
  manualSorting: true,
  getCoreRowModel: getCoreRowModel(),
  defaultColumn: {
    size: 180,
  },
  state: {
    get sorting() {
      return tanstackSorting.value
    },
    get rowSelection() {
      return props.rowSelection ?? {}
    },
    get columnPinning() {
      return resolvedColumnPinning.value
    },
  },
  onSortingChange: (updater) => {
    if (!props.enableSorting) return
    const oldTanstack: SortingState = sorting.value
      ? [{ id: sorting.value.sortBy, desc: sorting.value.sortDirection === 'desc' }]
      : []
    const next = typeof updater === 'function' ? updater(oldTanstack) : updater
    if (next[0]?.id) {
      sorting.value = {
        sortBy: next[0].id,
        sortDirection: next[0].desc ? 'desc' : 'asc',
      }
    } else {
      sorting.value = null
    }
  },
  onRowSelectionChange: (updater) => {
    const base = props.rowSelection ?? {}
    const next = typeof updater === 'function' ? updater(base) : updater
    emit('update:rowSelection', next)
  },
})

const leafColumnCount = computed(() => table.getAllLeafColumns().length)

function sortTitle(header: {
  column: { getCanSort: () => boolean; getNextSortingOrder: () => false | 'asc' | 'desc' }
}) {
  if (!header.column.getCanSort()) return undefined
  const next = header.column.getNextSortingOrder()
  if (next === 'asc') return 'Sort ascending'
  if (next === 'desc') return 'Sort descending'
  return 'Clear sort'
}
</script>

<template>
  <Table :container-class-name="cn('relative', containerClassName)">
    <TableHeader>
      <TableRow v-for="headerGroup in table.getHeaderGroups()" :key="headerGroup.id">
        <TableHead
          v-for="header in headerGroup.headers"
          :key="header.id"
          :style="{
            minWidth: header.column.columnDef.size
              ? `${header.column.columnDef.size}px`
              : undefined,
            maxWidth: header.column.columnDef.size
              ? `${header.column.columnDef.size}px`
              : undefined,
          }"
          :class="
            cn(
              getCommonPinningStyles(header.column),
              getDataTableColumnMetaClassName(header.column.columnDef.meta),
            )
          "
        >
          <div
            v-if="!header.isPlaceholder"
            :class="header.column.getCanSort() ? 'cursor-pointer select-none' : ''"
            :title="sortTitle(header)"
            @click="header.column.getToggleSortingHandler()?.($event)"
          >
            <FlexRender :render="header.column.columnDef.header" :props="header.getContext()" />
            <span
              v-if="header.column.getCanSort()"
              class="-translate-y-px ml-0.5 inline-block"
            >
              <ArrowUp
                v-if="header.column.getIsSorted() === 'asc'"
                class="inline-block size-4!"
              />
              <ArrowDown
                v-else-if="header.column.getIsSorted() === 'desc'"
                class="inline-block size-4!"
              />
              <ArrowUp v-else class="inline-block size-4! opacity-0" />
            </span>
          </div>
        </TableHead>
      </TableRow>
    </TableHeader>
    <TableBody>
      <template v-if="table.getRowModel().rows.length">
        <TableRow
          v-for="row in table.getRowModel().rows"
          :key="row.id"
          :data-state="row.getIsSelected() ? 'selected' : undefined"
        >
          <TableCell
            v-for="cell in row.getVisibleCells()"
            :key="cell.id"
            :style="{
              minWidth: cell.column.columnDef.size
                ? `${cell.column.columnDef.size}px`
                : undefined,
              maxWidth: cell.column.columnDef.size
                ? `${cell.column.columnDef.size}px`
                : undefined,
            }"
            :class="
              cn(
                getCommonPinningStyles(cell.column),
                getDataTableColumnMetaClassName(cell.column.columnDef.meta),
              )
            "
            :title="String(cell.getValue() ?? '')"
          >
            <FlexRender :render="cell.column.columnDef.cell" :props="cell.getContext()" />
          </TableCell>
        </TableRow>
      </template>
      <TableRow v-else class="h-20">
        <TableCell :colspan="Math.max(1, leafColumnCount)" class="relative p-0">
          <div
            class="absolute inset-0 top-10 flex flex-col items-center justify-center gap-2"
          >
            <template v-if="isLoading">
              <Spinner class="size-6 text-primary-foreground" />
            </template>
            <template v-else>
              <div class="rounded-lg bg-background-tertiary p-3 text-muted-foreground">
                <FileSearch class="size-6" />
              </div>
              <span class="text-base font-medium text-foreground">No results</span>
            </template>
          </div>
        </TableCell>
      </TableRow>
    </TableBody>
  </Table>
</template>

import type { Column } from '@tanstack/vue-table'
import { z } from 'zod'
import { cn } from '@/ui/lib/utils'

export const DataTableSortingSchema = z.object({
  sortBy: z.string(),
  sortDirection: z.enum(['asc', 'desc']),
})

export type DataTableSorting = z.infer<typeof DataTableSortingSchema>

/** Project-specific `columnDef.meta` shape (TanStack's `ColumnMeta` is an empty interface). */
export type DataTableColumnMeta = { className?: string }

export function getDataTableColumnMetaClassName(meta: unknown): string | undefined {
  return (meta as DataTableColumnMeta | undefined)?.className
}

/** Sticky + shadow classes for pinned columns (parity with React `DataTable.utils`). */
export function getCommonPinningStyles<TData>(column: Column<TData, unknown>): string {
  const isPinned = column.getIsPinned()
  const isLastLeftPinnedColumn = isPinned === 'left' && column.getIsLastColumn('left')
  const isFirstRightPinnedColumn = isPinned === 'right' && column.getIsFirstColumn('right')

  return cn(
    isPinned === 'left' && 'sticky left-0',
    isPinned === 'right' && 'sticky right-0',
    isPinned ? 'z-[1]' : 'z-0',
    isLastLeftPinnedColumn &&
      'group-data-[at-start="false"]:shadow-[-6px_0_6px_-6px_oklch(0_0_0_/_0.15)_inset] dark:group-data-[at-start="false"]:shadow-[-6px_0_6px_-6px_oklch(0_0_0_/_0.3)_inset]',
    isFirstRightPinnedColumn &&
      'group-data-[at-end="false"]:shadow-[6px_0_6px_-6px_oklch(0_0_0_/_0.15)_inset] dark:group-data-[at-end="false"]:shadow-[6px_0_6px_-6px_oklch(0_0_0_/_0.3)_inset]',
  )
}

export function sortRowsByDataTableSorting<T>(data: T[], sorting: DataTableSorting | null): T[] {
  if (!sorting) return data
  const key = sorting.sortBy
  const desc = sorting.sortDirection === 'desc'
  return [...data].sort((a, b) => {
    const av = (a as Record<string, unknown>)[key]
    const bv = (b as Record<string, unknown>)[key]
    if (av == null && bv == null) return 0
    if (av == null) return 1
    if (bv == null) return -1
    let cmp = 0
    if (typeof av === 'number' && typeof bv === 'number') cmp = av - bv
    else cmp = String(av).localeCompare(String(bv))
    return desc ? -cmp : cmp
  })
}

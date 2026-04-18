/**
 * Page list for pagination UI (1-based page indices).
 * Mirrors react-paginate-style `pageRangeDisplayed` + `marginPagesDisplayed`.
 */
export type PaginationItem = number | 'ellipsis'

export function getPaginationItems(
  currentPage: number,
  pageCount: number,
  pageRangeDisplayed: number,
  marginPagesDisplayed: number,
): PaginationItem[] {
  const total = Math.max(0, pageCount)
  if (total <= 0) return []

  if (pageRangeDisplayed === 0 && marginPagesDisplayed === 0) {
    return []
  }

  const current = Math.min(Math.max(1, currentPage), total)
  const pages = new Set<number>()

  for (let i = 1; i <= Math.min(marginPagesDisplayed, total); i++) {
    pages.add(i)
  }
  for (let i = Math.max(1, total - marginPagesDisplayed + 1); i <= total; i++) {
    pages.add(i)
  }
  const start = Math.max(1, current - pageRangeDisplayed)
  const end = Math.min(total, current + pageRangeDisplayed)
  for (let i = start; i <= end; i++) {
    pages.add(i)
  }

  const sorted = [...pages].sort((a, b) => a - b)
  const out: PaginationItem[] = []
  for (let i = 0; i < sorted.length; i++) {
    const p = sorted[i]!
    const prev = sorted[i - 1]
    if (i > 0 && prev !== undefined && p - prev > 1) {
      out.push('ellipsis')
    }
    out.push(p)
  }
  return out
}

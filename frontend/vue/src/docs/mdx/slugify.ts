/** Match markdown-it-anchor style slugs for TOC links. */
export function docSlugify(input: string): string {
  return String(input)
    .trim()
    .toLowerCase()
    .replace(/\s+/g, '-')
    .replace(/[^\w-]+/g, '')
}

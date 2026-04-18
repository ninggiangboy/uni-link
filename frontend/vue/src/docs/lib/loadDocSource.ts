const rawModules = import.meta.glob<string>('../content/**/*.md', {
  query: '?raw',
  import: 'default',
})

export async function loadDocSource(slug: string): Promise<string | null> {
  const suffix = `/${slug}.md`
  const entry = Object.entries(rawModules).find(([path]) => path.endsWith(suffix))
  if (!entry) return null
  const [, loader] = entry
  return loader()
}

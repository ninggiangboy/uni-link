export type DocFrontmatter = {
  title?: string
  description?: string
  originalDocs?: string
  sourceCode?: string
}

export function parseFrontmatter(raw: string): { frontmatter: DocFrontmatter; body: string } {
  if (!raw.startsWith('---')) {
    return { frontmatter: {}, body: raw }
  }
  const end = raw.indexOf('\n---', 3)
  if (end === -1) {
    return { frontmatter: {}, body: raw }
  }
  const yamlBlock = raw.slice(4, end).trim()
  const body = raw.slice(end + 4).replace(/^\r?\n/, '')

  const frontmatter: DocFrontmatter = {}
  for (const line of yamlBlock.split('\n')) {
    const m = line.match(/^([\w-]+):\s*(.*)$/)
    if (m) frontmatter[m[1] as keyof DocFrontmatter] = (m[2]?.trim() ?? '') as never
  }

  return { frontmatter, body }
}

import MarkdownIt from 'markdown-it'
import anchor from 'markdown-it-anchor'
import type { DocFrontmatter } from './parseFrontmatter'
import { parseFrontmatter } from './parseFrontmatter'
import { docSlugify } from './slugify'
import { getShikiHighlighter } from './shikiHighlighter'

export type TocItem = {
  level: 2 | 3
  text: string
  id: string
}

export type DocSegment =
  | { type: 'markdown'; html: string }
  | { type: 'component-preview'; name: string; previewClass?: string }

const COMPONENT_PREVIEW_RE =
  /<ComponentPreview\s+name="([^"]+)"(?:\s+(?:className|class)="([^"]*)")?\s*\/>/g

function extractTocFromMarkdown(mdBody: string): TocItem[] {
  const lines = mdBody.split(/\n/)
  const items: TocItem[] = []
  for (const line of lines) {
    const h3 = line.match(/^###\s+(.+)$/)
    const h2 = line.match(/^##\s+(.+)$/)
    if (h2) {
      const text = h2[1]?.replace(/\*+/g, '').trim() ?? ''
      if (text) items.push({ level: 2, text, id: docSlugify(text) })
    }
    if (h3) {
      const text = h3[1]?.replace(/\*+/g, '').trim() ?? ''
      if (text) items.push({ level: 3, text, id: docSlugify(text) })
    }
  }
  return items
}

function stripPreviewTagsForToc(body: string): string {
  return body.replace(COMPONENT_PREVIEW_RE, '\n')
}

let mdSingleton: MarkdownIt | null = null

async function getMarkdownIt(): Promise<MarkdownIt> {
  if (mdSingleton) return mdSingleton
  const highlighter = await getShikiHighlighter()

  const md = new MarkdownIt({
    html: false,
    linkify: true,
    typographer: true,
    highlight(code, lang) {
      return highlighter.codeToHtml(code, {
        lang: lang || 'text',
        themes: {
          light: 'github-light',
          dark: 'github-dark',
        },
        defaultColor: false,
      })
    },
  })

  md.use(anchor, {
    slugify: (s: string) => docSlugify(String(s)),
    permalink: false,
  })

  mdSingleton = md
  return md
}

export type ParsedDoc = {
  frontmatter: DocFrontmatter
  segments: DocSegment[]
  toc: TocItem[]
}

export async function renderDoc(raw: string): Promise<ParsedDoc> {
  const { frontmatter, body } = parseFrontmatter(raw)
  const md = await getMarkdownIt()
  const tocSource = stripPreviewTagsForToc(body)
  const toc = extractTocFromMarkdown(tocSource)

  const segments: DocSegment[] = []
  let last = 0
  let m: RegExpExecArray | null
  const re = new RegExp(COMPONENT_PREVIEW_RE.source, 'g')
  while ((m = re.exec(body)) !== null) {
    const before = body.slice(last, m.index).trimEnd()
    if (before.trim()) {
      segments.push({ type: 'markdown', html: md.render(before) })
    }
    segments.push({
      type: 'component-preview',
      name: m[1]!,
      previewClass: m[2] || undefined,
    })
    last = m.index + m[0].length
  }
  const rest = body.slice(last).trim()
  if (rest) {
    segments.push({ type: 'markdown', html: md.render(rest) })
  }

  return { frontmatter, segments, toc }
}

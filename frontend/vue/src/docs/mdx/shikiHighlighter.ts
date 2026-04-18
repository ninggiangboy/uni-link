import { createHighlighter, type Highlighter } from 'shiki'

let instance: Highlighter | null = null
let loading: Promise<Highlighter> | null = null

export async function getShikiHighlighter(): Promise<Highlighter> {
  if (instance) return instance
  if (!loading) {
    loading = createHighlighter({
      themes: ['github-light', 'github-dark'],
      langs: [
        'javascript',
        'typescript',
        'vue',
        'html',
        'css',
        'bash',
        'json',
        'tsx',
        'jsx',
        'md',
        'markdown',
      ],
    })
  }
  instance = await loading
  return instance
}

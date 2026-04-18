import type { Component } from 'vue'
import { defineAsyncComponent } from 'vue'

const loaderMap = import.meta.glob<{ default: Component }>('./**/*.vue')

function findModulePath(name: string): string | undefined {
  const suffix = `/${name}.vue`
  return Object.keys(loaderMap).find((p) => p.endsWith(suffix))
}

/** Async doc preview by component name (file `Name.vue` anywhere under examples/) */
export function getPreviewComponent(name: string) {
  const path = findModulePath(name)
  if (!path) return null
  return defineAsyncComponent(loaderMap[path]!)
}

const rawMap = import.meta.glob<string>('./**/*.vue', {
  query: '?raw',
  import: 'default',
})

export async function loadPreviewSource(name: string): Promise<string | null> {
  const suffix = `/${name}.vue`
  const path = Object.keys(rawMap).find((p) => p.endsWith(suffix))
  if (!path) return null
  return rawMap[path]!()
}

export const previewNames = [
  ...new Set(
    Object.keys(loaderMap).map((p) => {
      const seg = p.split('/').pop()!
      return seg.replace(/\.vue$/, '')
    }),
  ),
].sort()

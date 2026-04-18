import { formatFileSize, validateFileExtension, validateFileSize } from '@/ui/lib/file'
import { toast } from '@/ui/components/sonner'

export interface UploaderRules {
  acceptedFileExtensions?: string[]
  maxFileSize?: number
  maxFiles?: number
  allowMultiple: boolean
}

export function useValidateFiles(rules: UploaderRules) {
  const { acceptedFileExtensions, maxFileSize } = rules

  return (files: File[]) => {
    let maxFiles = Number.POSITIVE_INFINITY
    if (rules.allowMultiple === false) maxFiles = 1
    else if (rules.maxFiles != null) maxFiles = rules.maxFiles

    if (files.length > maxFiles) {
      toast.error('Maximum number of files exceeded', { duration: 8000 })
      return []
    }

    let acceptedFiles = [...files]
    const rejectedByExtension: File[] = []
    const rejectedBySize: File[] = []

    if (acceptedFileExtensions?.length) {
      acceptedFiles = acceptedFiles.filter((file) => {
        const ok = validateFileExtension(file, acceptedFileExtensions)
        if (!ok) rejectedByExtension.push(file)
        return ok
      })
    }

    if (maxFileSize) {
      acceptedFiles = acceptedFiles.filter((file) => {
        const ok = validateFileSize(file, maxFileSize)
        if (!ok) rejectedBySize.push(file)
        return ok
      })
    }

    if (rejectedByExtension.length || rejectedBySize.length) {
      const parts: string[] = []
      if (rejectedByExtension.length)
        parts.push(`${rejectedByExtension.length} rejected by extension`)
      if (rejectedBySize.length)
        parts.push(
          `${rejectedBySize.length} rejected by size (max ${formatFileSize(maxFileSize!)})`,
        )
      toast.warning(parts.join(' · '))
    }

    return acceptedFiles
  }
}

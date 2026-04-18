import type { AxiosRequestConfig } from 'axios'
import type { UploaderFile } from './uploaderTypes'

export class UploaderAction {
  constructor(protected readonly url: string) {}

  buildRequest(uploaderFile: UploaderFile): AxiosRequestConfig {
    const formData = new FormData()
    if (uploaderFile.file) formData.append('file', uploaderFile.file)
    return {
      url: this.url,
      method: 'POST',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' },
    }
  }

  formatResponse(response: { data: Record<string, unknown> }): Partial<UploaderFile> {
    const d = response.data
    return {
      id: String(d.id ?? ''),
      url: String(d.url ?? ''),
      name: String(d.name ?? ''),
      size: Number(d.size ?? 0),
      type: String(d.type ?? ''),
      extension: String(d.extension ?? ''),
    }
  }

  formatResponseError(error: unknown): string {
    const err = error as { response?: { data?: { error?: string } } }
    return err.response?.data?.error ?? 'Failed to upload file'
  }
}

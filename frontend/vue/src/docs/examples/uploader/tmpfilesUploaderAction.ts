import { UploaderAction } from '@/ui/components/uploader/uploaderAction'

interface TmpResponse {
  data: {
    url: string
  }
}

/** Demo upload target used by doc uploader examples (mirrors React UploaderDemo.utils). */
export class TmpfilesUploaderAction extends UploaderAction {
  constructor() {
    super('https://tmpfiles.org/api/v1/upload')
  }

  override formatResponse(response: { data: Record<string, unknown> }) {
    const body = response.data as unknown as TmpResponse
    const url = body?.data?.url
    const id = url.split('/').slice(-2).join('/')
    const downloadUrl = `https://tmpfiles.org/dl/${id}`

    return {
      id: url,
      url: downloadUrl,
    }
  }
}

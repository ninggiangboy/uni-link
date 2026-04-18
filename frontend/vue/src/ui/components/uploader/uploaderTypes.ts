export type UploaderFile = {
  id?: string
  url?: string
  name?: string
  size?: number
  type?: string
  extension?: string
  file?: File
  status?: 'idle' | 'uploading' | 'success' | 'error'
  progress?: number
  error?: string
}

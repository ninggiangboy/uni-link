<script setup lang="ts">
import { TmpfilesUploaderAction } from '@/docs/examples/uploader/tmpfilesUploaderAction'
import { Uploader } from '@/ui/components/uploader'
import type { UploaderFile } from '@/ui/components/uploader/uploaderTypes'

const exampleFileList: UploaderFile[] = [
  {
    id: '1',
    name: 'sample4.csv',
    status: 'success',
    size: Math.round(7.7 * 1024),
    extension: 'csv',
    type: 'text/csv',
    url: 'https://filesamples.com/samples/document/csv/sample4.csv',
    progress: 100,
  },
  {
    id: '2',
    name: 'sample2.doc',
    status: 'success',
    size: 32 * 1024,
    extension: 'doc',
    type: 'application/msword',
    url: 'https://filesamples.com/samples/document/doc/sample2.doc',
    progress: 100,
  },
]
</script>

<template>
  <Uploader
    :action="new TmpfilesUploaderAction()"
    :default-file-list="exampleFileList"
    :max-file-size="100 * 1024 * 1024"
    is-disabled
  />
</template>

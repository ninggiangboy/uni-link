<script setup lang="ts">
const exampleFileList = [
  {
    id: '1',
    name: 'sample4.csv',
    status: 'success',
    size: Math.round(7.7 * 1024),
    extension: 'csv',
    progress: 100,
    type: 'text/csv',
    url: 'https://filesamples.com/samples/document/csv/sample4.csv',
  },
  {
    id: '2',
    name: 'sample2.doc',
    status: 'success',
    size: 32 * 1024,
    extension: 'doc',
    progress: 100,
    type: 'application/msword',
    url: 'https://filesamples.com/samples/document/doc/sample2.doc',
  },
] as const
import { Uploader } from '@/ui/components/uploader'
import { TmpfilesUploaderAction } from '@/docs/examples/uploader/tmpfilesUploaderAction'
</script>
<template>
  <Uploader
    :action="new TmpfilesUploaderAction()"
    :max-file-size="100 * 1024 * 1024"
    :default-file-list="[...exampleFileList]"
    list-type="card"
  />
</template>

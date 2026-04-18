<script setup lang="ts">
import { Uploader } from '@/ui/components/uploader'
import { TmpfilesUploaderAction } from '@/docs/examples/uploader/tmpfilesUploaderAction'
</script>
<template>
  <Uploader
    :action="new TmpfilesUploaderAction()"
    :max-file-size="100 * 1024 * 1024"
    :accepted-file-extensions="['pdf', 'docx', 'png', 'csv']"
    :max-files="3"
  />
</template>

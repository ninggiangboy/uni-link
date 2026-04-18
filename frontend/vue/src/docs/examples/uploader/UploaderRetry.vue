<script setup lang="ts">
import { Uploader } from '@/ui/components/uploader'
import { UploaderAction } from '@/ui/components/uploader/uploaderAction'
</script>
<template>
  <Uploader
    :action="new UploaderAction('https://tmpfiles.org/api/v1/wrong-url')"
    :max-file-size="100 * 1024 * 1024"
  />
</template>

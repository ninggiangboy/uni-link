<script setup lang="ts">
import { AvatarImage as RekaAvatarImage } from 'reka-ui'
import { cn } from '@/ui/lib/utils'

defineOptions({ inheritAttrs: false })

const props = withDefaults(
  defineProps<{
    class?: string
    src?: string
  }>(),
  { src: '' },
)
</script>

<template>
  <RekaAvatarImage
    data-slot="avatar-image"
    :src="props.src"
    :class="cn('aspect-square size-full object-cover', props.class)"
    v-bind="$attrs"
  />
</template>

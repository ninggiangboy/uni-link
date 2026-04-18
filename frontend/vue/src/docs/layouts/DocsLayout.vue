<script setup lang="ts">
import CrossIcon from '@/docs/components/CrossIcon.vue'
import { useIsMobile } from '@/ui/composables/useIsMobile'
import { onUnmounted, ref, watchEffect } from 'vue'
import { useRoute } from 'vue-router'
import HeaderIconButtons from './HeaderIconButtons.vue'
import HamburgerMenu from './HamburgerMenu.vue'
import ModulePicker from './ModulePicker.vue'
import SidebarHeader from './SidebarHeader.vue'
import SidebarMenu from './SidebarMenu.vue'
import TopNavLinks from './TopNavLinks.vue'

const { isMobile } = useIsMobile({ breakpointPx: 1024 })
const mobileMenuOpen = ref(false)

const route = useRoute()
watchEffect(() => {
  if (typeof document === 'undefined') return
  document.documentElement.classList.toggle('docs-scrollbar-root', route.path.startsWith('/docs'))
})
onUnmounted(() => {
  if (typeof document !== 'undefined') {
    document.documentElement.classList.remove('docs-scrollbar-root')
  }
})
</script>

<template>
  <div class="container w-full max-w-350 mx-auto">
    <div class="fixed h-16 max-w-350 w-full z-20">
      <CrossIcon
        class="max-xl:hidden absolute top-16 left-[0.5px] -translate-x-1/2 -translate-y-1/2"
      />
      <CrossIcon
        class="max-xl:hidden absolute top-16 right-[0.5px] translate-x-1/2 -translate-y-1/2"
      />

      <div
        v-if="!isMobile"
        class="max-lg:hidden grid grid-cols-[260px_1fr] xl:grid-cols-[260px_1fr_260px] h-full"
      >
        <div class="border-l border-border h-full">
          <SidebarHeader />
        </div>
        <div class="flex items-center border-x border-border px-10">
          <TopNavLinks />
          <div class="flex-1" />
          <HeaderIconButtons />
        </div>
        <div class="border-r border-border h-full max-xl:hidden" />
      </div>

      <div v-else class="lg:hidden h-full flex items-center gap-4 pr-5 pl-3">
        <HamburgerMenu v-model="mobileMenuOpen" />
        <TopNavLinks />
        <div class="flex-1" />
        <HeaderIconButtons />
      </div>
    </div>

    <div class="fixed h-16 left-0 w-full border-b border-border z-19 bg-background" />

    <div class="min-h-screen grid lg:grid-cols-[260px_1fr] xl:grid-cols-[260px_1fr_260px]">
      <div class="h-full max-lg:hidden">
        <div class="w-65 h-full fixed border-l border-border pt-16 bg-background flex flex-col min-h-0">
          <ModulePicker />
          <SidebarMenu class="min-h-0 flex-1 overflow-hidden" />
        </div>
      </div>

      <div
        class="h-full pt-16 lg:border-x border-border grid lg:grid-cols-[40px_1fr_40px] bg-background-secondary/80"
      >
        <div
          class="bg-[repeating-linear-gradient(315deg,var(--border)_0,var(--border)_1px,transparent_0,transparent_50%)] bg-size-[10px_10px] bg-fixed max-lg:hidden"
        />
        <div class="lg:border-x border-border min-w-0">
          <slot />
        </div>
        <div
          class="bg-[repeating-linear-gradient(315deg,var(--border)_0,var(--border)_1px,transparent_0,transparent_50%)] bg-size-[10px_10px] bg-fixed max-lg:hidden"
        />
      </div>

      <div class="h-full py-16 max-xl:hidden">
        <div class="w-65 h-full fixed border-r border-border bg-background">
          <div id="toc">
            <slot name="toc" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

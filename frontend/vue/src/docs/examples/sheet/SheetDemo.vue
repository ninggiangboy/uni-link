<script setup lang="ts">
import { Button } from '@/ui/components/button'
import {
  DialogClose,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/ui/components/dialog'
import { Label } from '@/ui/components/field'
import { Sheet, SheetContent, SheetTrigger } from '@/ui/components/sheet'
import { Input } from '@/ui/components/textfield'
</script>

<template>
  <Sheet>
    <SheetTrigger as-child>
      <Button>Sign up</Button>
    </SheetTrigger>
    <SheetContent>
      <DialogHeader>
        <DialogTitle>Sign up</DialogTitle>
        <DialogDescription>Please fill out the form below to sign up.</DialogDescription>
      </DialogHeader>
      <div class="flex-1 space-y-4 px-4">
        <div class="flex flex-col gap-2">
          <Label for="sfn">First Name</Label>
          <Input id="sfn" placeholder="First Name" autofocus />
        </div>
        <div class="flex flex-col gap-2">
          <Label for="sln">Last Name</Label>
          <Input id="sln" placeholder="Last Name" />
        </div>
      </div>
      <DialogFooter>
        <DialogClose as-child>
          <Button variant="outline"> Cancel </Button>
        </DialogClose>
        <DialogClose as-child>
          <Button type="button"> Save changes </Button>
        </DialogClose>
      </DialogFooter>
    </SheetContent>
  </Sheet>
</template>

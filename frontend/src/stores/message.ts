import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUnreadCount } from '@/api/message'

export const useMessageStore = defineStore('message', () => {
  const unreadCount = ref(0)

  async function refreshUnreadCount() {
    try {
      unreadCount.value = await getUnreadCount()
    } catch {
      unreadCount.value = 0
    }
  }

  function decrementUnreadCount() {
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }

  function reset() {
    unreadCount.value = 0
  }

  return { unreadCount, refreshUnreadCount, decrementUnreadCount, reset }
})

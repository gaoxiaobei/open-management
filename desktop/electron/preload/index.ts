import { contextBridge, ipcRenderer } from 'electron'

contextBridge.exposeInMainWorld('electronAPI', {
  onUpdateAvailable: (callback: () => void) => {
    ipcRenderer.on('update-available', callback)
  },
  onUpdateDownloaded: (callback: () => void) => {
    ipcRenderer.on('update-downloaded', callback)
  },
  installUpdate: () => {
    ipcRenderer.send('install-update')
  },
  getAppVersion: () => ipcRenderer.invoke('get-app-version'),
})

interface ElectronAPI {
  onUpdateAvailable: (callback: () => void) => void
  onUpdateDownloaded: (callback: () => void) => void
  installUpdate: () => void
  getAppVersion: () => Promise<string>
}

declare interface Window {
  electronAPI?: ElectronAPI
}

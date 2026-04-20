# Open Management Desktop

基于 Electron + Vue 3 的 Windows 桌面客户端。

## 技术栈

- Electron 39
- Vue 3 + TypeScript
- Element Plus
- Pinia
- Vue Router（Hash 模式）
- electron-builder（NSIS 打包）
- electron-updater（自动更新）

## 开发

```bash
npm install
npm run electron:dev
```

## 构建 Windows 安装包

```bash
npm run build
```

输出目录：`release/`

## 目录结构

```
desktop/
├── electron/
│   ├── main/index.ts       # 主进程（BrowserWindow、自动更新、IPC）
│   └── preload/index.ts    # 预加载脚本（contextBridge）
├── src/
│   ├── main.ts             # 渲染进程入口
│   ├── App.vue
│   ├── router/             # Hash 模式路由
│   ├── layout/             # 主布局
│   ├── views/              # 页面
│   ├── components/         # 公共组件（UpdateNotifier）
│   └── types/              # electron.d.ts 类型声明
├── index.html
├── vite.config.ts
├── electron-builder.yml
└── package.json
```

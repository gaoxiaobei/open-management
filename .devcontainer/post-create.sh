#!/usr/bin/env bash

set -uo pipefail

cd /workspaces/open-management

ensure_writable_dir() {
  local path="$1"

  if [ ! -d "$path" ]; then
    sudo mkdir -p "$path"
  fi

  sudo chown -R "$(id -u):$(id -g)" "$path"
}

ensure_writable_dir /workspaces/open-management/frontend/node_modules
ensure_writable_dir /workspaces/open-management/desktop/node_modules
ensure_writable_dir /home/vscode/.npm
ensure_writable_dir /home/vscode/.m2

if [ -f frontend/package-lock.json ]; then
  npm ci --prefix frontend
else
  npm install --prefix frontend
fi

ELECTRON_MIRROR=https://npmmirror.com/mirrors/electron/ npm install --prefix desktop || {
  echo "⚠  desktop npm install failed (Electron may be unavailable in this network)."
  echo "   Desktop client is optional for development; frontend + backend will work fine."
}

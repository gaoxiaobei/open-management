#!/usr/bin/env bash

set -euo pipefail

cd /workspaces/open-management

if [ -f frontend/package-lock.json ]; then
  npm ci --prefix frontend
else
  npm install --prefix frontend
fi

npm install --prefix desktop

#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/../.." && pwd)"
ORCHESTRATOR="$REPO_ROOT/devops/automation/jetbrains-agent-orchestrator.ps1"
MODE="${1:?Usage: call-jetbrains-agent.bash <mode> [PowerShell args...]}"
shift || true

case "$(uname -s 2>/dev/null || echo unknown)" in
  MINGW*|MSYS*|CYGWIN*|UCRT*)
    ;;
  *)
    echo "This launcher is currently supported from Git Bash / MSYS on Windows. The current bash environment cannot reliably invoke the Windows UI automation helper." >&2
    exit 1
    ;;
esac

if command -v pwsh >/dev/null 2>&1; then
  exec pwsh -NoLogo -NoProfile -File "$ORCHESTRATOR" -Mode "$MODE" "$@"
elif command -v powershell.exe >/dev/null 2>&1; then
  exec powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "$ORCHESTRATOR" -Mode "$MODE" "$@"
else
  echo "Neither pwsh nor powershell.exe was found on PATH. Install PowerShell to run the Dark Factory launcher automation." >&2
  exit 1
fi


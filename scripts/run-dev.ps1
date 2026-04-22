# ============================================================
# DEV Launcher — Task API Local with Hot Reload
# Usage: pwsh -File scripts/run-dev.ps1
# ============================================================

$ErrorActionPreference = "Stop"

Write-Host "[DEV] Starting Task API..." -ForegroundColor Cyan
Write-Host "[DEV] Profile: dev" -ForegroundColor Cyan
Write-Host "[DEV] Port: 8082" -ForegroundColor Cyan
Write-Host "[DEV] Hot Reload: ACTIVE" -ForegroundColor Cyan
Write-Host ""

# Navigate to project root (parent of scripts/)
$PROJECT_ROOT = Split-Path -Parent $PSScriptRoot
Push-Location $PROJECT_ROOT

try {
    # Check if Maven is available
    $mvn = Get-Command mvn -ErrorAction SilentlyContinue
    if (-not $mvn) {
        Write-Host "[DEV] ERROR: Maven not found. Please install Maven." -ForegroundColor Red
        exit 1
    }

    # Free port 8082 (may require admin)
    try {
        $process = Get-NetTCPConnection -LocalPort 8082 -ErrorAction SilentlyContinue |
                   Where-Object { $_.State -eq "Listen" } |
                   Select-Object -First 1

        if ($process) {
            Write-Host "[DEV] Freeing port 8082 (PID $($process.OwningProcess))..." -ForegroundColor Yellow
            Stop-Process -Id $process.OwningProcess -Force -ErrorAction SilentlyContinue
            Start-Sleep -Milliseconds 500
        }
    } catch {
        Write-Host "[DEV] Warning: Could not free port 8082 (run as admin if needed)" -ForegroundColor Yellow
    }

    # Check MongoDB is running (optional warning)
    try {
        $mongo = Get-NetTCPConnection -LocalPort 27017 -ErrorAction SilentlyContinue |
                 Where-Object { $_.State -eq "Listen" }
        if (-not $mongo) {
            Write-Host "[DEV] Warning: MongoDB not running on port 27017" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "[DEV] Warning: Could not check MongoDB" -ForegroundColor Yellow
    }

    Write-Host "[DEV] Executing mvn spring-boot:run -Dspring-boot.run.profiles=dev..." -ForegroundColor Green
    Write-Host ""

    # Run with Maven (dev profile)
    & mvn 'spring-boot:run' '-Dspring-boot.run.profiles=dev'

    if ($LASTEXITCODE -ne 0) {
        Write-Host "[DEV] ERROR: Maven exited with code $LASTEXITCODE" -ForegroundColor Red
        exit 1
    }

} catch {
    Write-Host "[DEV] ERROR: $_" -ForegroundColor Red
    exit 1
} finally {
    Pop-Location
}

Write-Host ""
Write-Host "[DEV] Task API stopped." -ForegroundColor Cyan
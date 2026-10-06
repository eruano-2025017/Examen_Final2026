# ==============================================================================
# Script para iniciar los 3 microservicios simultaneamente en Windows PowerShell
# ==============================================================================

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  Iniciando Microservicios Veterinaria (8081, 8082, 8083)  " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$baseDir = $PSScriptRoot

# 1. Iniciar Auth Service (Puerto 8081)
Write-Host "Lanzando auth-service en puerto 8081..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$baseDir'; Write-Host 'Iniciando Auth Service (Puerto 8081)...' -ForegroundColor Cyan; .\mvnw.cmd spring-boot:run -pl auth-service"

Start-Sleep -Seconds 3

# 2. Iniciar Citas & Mascotas Service (Puerto 8082)
Write-Host "Lanzando citas-mascotas-service en puerto 8082..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$baseDir'; Write-Host 'Iniciando Citas & Mascotas Service (Puerto 8082)...' -ForegroundColor Cyan; .\mvnw.cmd spring-boot:run -pl citas-mascotas-service"

Start-Sleep -Seconds 3

# 3. Iniciar Expedientes Service (Puerto 8083)
Write-Host "Lanzando expedientes-service en puerto 8083..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$baseDir'; Write-Host 'Iniciando Expedientes Service (Puerto 8083)...' -ForegroundColor Cyan; .\mvnw.cmd spring-boot:run -pl expedientes-service"

Write-Host "==========================================================" -ForegroundColor Yellow
Write-Host "Los 3 microservicios estan iniciando en ventanas separadas." -ForegroundColor Yellow
Write-Host "  - Auth Service:        http://localhost:8081" -ForegroundColor White
Write-Host "  - Citas Service:       http://localhost:8082" -ForegroundColor White
Write-Host "  - Expedientes Service: http://localhost:8083" -ForegroundColor White
Write-Host "==========================================================" -ForegroundColor Yellow


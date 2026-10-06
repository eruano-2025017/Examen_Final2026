# ==============================================================================
# Script de Pruebas Funcionales: test-veterinaria.ps1 (PowerShell)
# Sistema de Control de Citas para Veterinaria
# ==============================================================================

$authUrl = "http://localhost:8081"
$citasUrl = "http://localhost:8082"
$expedientesUrl = "http://localhost:8083"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  INICIANDO SUITE DE PRUEBAS FUNCIONALES - VETERINARIA    " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Login ADMIN
Write-Host "`n[1/7] Probando Login de ADMIN en auth-service (8081)..." -ForegroundColor Yellow
$adminResp = Invoke-RestMethod -Uri "$authUrl/api/v1/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"admin@veterinaria.com","password":"admin123"}'
$adminToken = $adminResp.token
Write-Host "Token ADMIN obtenido exitosamente: $($adminToken.Substring(0, 20))..." -ForegroundColor Green

# 2. Login VET
Write-Host "`n[2/7] Probando Login de VET en auth-service (8081)..." -ForegroundColor Yellow
$vetResp = Invoke-RestMethod -Uri "$authUrl/api/v1/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"veterinario@veterinaria.com","password":"vet123"}'
$vetToken = $vetResp.token
Write-Host "Token VET obtenido exitosamente: $($vetToken.Substring(0, 20))..." -ForegroundColor Green

# 3. Login CLIENTE
Write-Host "`n[3/7] Probando Login de CLIENTE en auth-service (8081)..." -ForegroundColor Yellow
$clienteResp = Invoke-RestMethod -Uri "$authUrl/api/v1/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"cliente@correo.com","password":"cliente123"}'
$clienteToken = $clienteResp.token
Write-Host "Token CLIENTE obtenido exitosamente: $($clienteToken.Substring(0, 20))..." -ForegroundColor Green

# 4. Consultar mis mascotas (CLIENTE)
Write-Host "`n[4/7] Probando GET /api/v1/mascotas/mis-mascotas con token CLIENTE (8082)..." -ForegroundColor Yellow
$mascotas = Invoke-RestMethod -Uri "$citasUrl/api/v1/mascotas/mis-mascotas" -Method Get -Headers @{ Authorization = "Bearer $clienteToken" }
$mascotas | ConvertTo-Json -Depth 3

# 5. Consultar agenda de citas (VET)
Write-Host "`n[5/7] Probando GET /api/v1/citas/agenda con token VET (8082)..." -ForegroundColor Yellow
$agenda = Invoke-RestMethod -Uri "$citasUrl/api/v1/citas/agenda" -Method Get -Headers @{ Authorization = "Bearer $vetToken" }
$agenda | ConvertTo-Json -Depth 3

# 6. Agendar nueva cita (CLIENTE)
Write-Host "`n[6/7] Probando POST /api/v1/citas con token CLIENTE (8082)..." -ForegroundColor Yellow
$nuevaCitaBody = @{
    mascotaId = 1
    veterinarioId = 2
    fechaHora = "2026-12-01T15:00:00"
    motivo = "Revision dental y limpieza"
} | ConvertTo-Json
$nuevaCita = Invoke-RestMethod -Uri "$citasUrl/api/v1/citas" -Method Post -ContentType "application/json" -Headers @{ Authorization = "Bearer $clienteToken" } -Body $nuevaCitaBody
$nuevaCita | ConvertTo-Json -Depth 3

# 7. Consultar expedientes por mascota (VET)
Write-Host "`n[7/7] Probando GET /api/v1/expedientes/mascota/1 con token VET (8083)..." -ForegroundColor Yellow
$expedientes = Invoke-RestMethod -Uri "$expedientesUrl/api/v1/expedientes/mascota/1" -Method Get -Headers @{ Authorization = "Bearer $vetToken" }
$expedientes | ConvertTo-Json -Depth 3

Write-Host "`n==========================================================" -ForegroundColor Cyan
Write-Host "            PRUEBAS FINALIZADAS SATISFACTORIAMENTE        " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan


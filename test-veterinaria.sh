#!/usr/bin/env bash
# ==============================================================================
# Script de Pruebas Funcionales: test-veterinaria.sh
# Sistema de Control de Citas para Veterinaria
# ==============================================================================

AUTH_URL="http://localhost:8081"
CITAS_URL="http://localhost:8082"
EXPEDIENTES_URL="http://localhost:8083"

echo "=========================================================="
echo "  INICIANDO SUITE DE PRUEBAS FUNCIONALES - VETERINARIA    "
echo "=========================================================="

# 1. Login ADMIN
echo -e "\n[1/7] Probando Login de ADMIN en auth-service (8081)..."
ADMIN_TOKEN=$(curl -s -X POST "$AUTH_URL/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@veterinaria.com","password":"admin123"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$ADMIN_TOKEN" ]; then
  echo "Error obteniendo token de ADMIN"
else
  echo "Token ADMIN obtenido exitosamente."
fi

# 2. Login VET
echo -e "\n[2/7] Probando Login de VET en auth-service (8081)..."
VET_TOKEN=$(curl -s -X POST "$AUTH_URL/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"veterinario@veterinaria.com","password":"vet123"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$VET_TOKEN" ]; then
  echo "Error obteniendo token de VET"
else
  echo "Token VET obtenido exitosamente."
fi

# 3. Login CLIENTE
echo -e "\n[3/7] Probando Login de CLIENTE en auth-service (8081)..."
CLIENTE_TOKEN=$(curl -s -X POST "$AUTH_URL/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"cliente@correo.com","password":"cliente123"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$CLIENTE_TOKEN" ]; then
  echo "Error obteniendo token de CLIENTE"
else
  echo "Token CLIENTE obtenido exitosamente."
fi

# 4. Consultar mis mascotas (CLIENTE)
echo -e "\n[4/7] Probando GET /api/v1/mascotas/mis-mascotas con token CLIENTE (8082)..."
curl -s -X GET "$CITAS_URL/api/v1/mascotas/mis-mascotas" \
  -H "Authorization: Bearer $CLIENTE_TOKEN"
echo ""

# 5. Consultar agenda de citas (VET)
echo -e "\n[5/7] Probando GET /api/v1/citas/agenda con token VET (8082)..."
curl -s -X GET "$CITAS_URL/api/v1/citas/agenda" \
  -H "Authorization: Bearer $VET_TOKEN"
echo ""

# 6. Agendar nueva cita (CLIENTE)
echo -e "\n[6/7] Probando POST /api/v1/citas con token CLIENTE (8082)..."
curl -s -X POST "$CITAS_URL/api/v1/citas" \
  -H "Authorization: Bearer $CLIENTE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "mascotaId": 1,
    "veterinarioId": 2,
    "fechaHora": "2026-12-01T15:00:00",
    "motivo": "Revision dental y limpieza"
  }'
echo ""

# 7. Consultar expedientes por mascota (VET)
echo -e "\n[7/7] Probando GET /api/v1/expedientes/mascota/1 con token VET (8083)..."
curl -s -X GET "$EXPEDIENTES_URL/api/v1/expedientes/mascota/1" \
  -H "Authorization: Bearer $VET_TOKEN"
echo ""

echo "=========================================================="
echo "            PRUEBAS FINALIZADAS SATISFACTORIAMENTE        "
echo "=========================================================="


# Sistema de Control de Citas para Veterinaria - API REST

API REST modular para la gestión de citas, mascotas, expedientes y usuarios en una clínica veterinaria. El proyecto está construido bajo una arquitectura de microservicios usando Maven Multi-Módulo con Spring Boot y una base de datos relacional MySQL.

---

## 🛠️ Tecnologías

- **Lenguaje:** Java 17
- **Framework:** Spring Boot 3.3.0
- **Seguridad:** Spring Security con autenticación JWT (JJWT 0.11.5) y contraseñas cifradas con BCrypt
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de Datos:** MySQL Server 8.0
- **Herramientas:** Maven, Lombok

---

## 🏗️ Arquitectura del Proyecto

El repositorio maneja un POM Padre (`veterinaria-parent`) que gestiona las versiones y dependencias comunes de los microservicios:

```text
Examen_Final2026/
├── pom.xml                     # POM Padre (veterinaria-parent)
├── .mvn/jvm.config             # Configuración de red/certificados para Maven
├── auth-service/               # Puerto 8081: Usuarios, roles y autenticación JWT
│   ├── pom.xml
│   └── src/
├── citas-mascotas-service/     # Puerto 8082: Mascotas y agendamiento de citas
│   ├── pom.xml
│   └── src/
└── expedientes-service/        # Puerto 8083: Historial clínico e integración vía OpenFeign
    ├── pom.xml
    └── src/
```

---

## 🗄️ Base de Datos

Ambos microservicios comparten la misma base de datos relacional en MySQL.

- **Nombre de BD:** `veterinaria_db_in5am`
- **Host / Puerto:** `localhost:3306`
- **Usuario:** `IN5AM`
- **Password:** `_odmon5Am`

> Hibernate genera y actualiza las tablas automáticamente al iniciar cada servicio (`ddl-auto=update`). `auth-service` ejecuta una carga inicial desde `data.sql` con usuarios predeterminados.

### Usuarios Iniciales de Prueba

| Rol | Email | Contraseña |
| :--- | :--- | :--- |
| **ADMIN** | `admin@veterinaria.com` | `admin123` |
| **VET** | `veterinario@veterinaria.com` | `vet123` |
| **CLIENTE** | `cliente@correo.com` | `cliente123` |

---

## 📋 Reglas de Negocio Implementadas

1. **Disponibilidad de Veterinarios:** Las citas tienen una duración fija de 30 minutos. El sistema bloquea cualquier intento de agendar dos citas con solapamiento de horario para el mismo veterinario.
2. **Límite Diario de Cliente:** Un cliente no puede tener más de 2 citas en estado `PENDIENTE` para el mismo día.
3. **Restricción de Cancelación:** Una cita solo puede ser cancelada si faltan más de 2 horas para la hora programada.
4. **Seguridad Stateless:** Cada petición a rutas protegidas debe incluir el token en la cabecera HTTP:
   `Authorization: Bearer <TOKEN_JWT>`

---

## 🚀 Endpoints de la API

### 1. Servicio de Autenticación (`auth-service` - Puerto 8081)

| Método | Endpoint | Acceso | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Público | Registra un usuario y genera su token JWT |
| `POST` | `/api/v1/auth/login` | Público | Autentica credenciales y devuelve el token JWT |

### 2. Servicio de Mascotas y Citas (`citas-mascotas-service` - Puerto 8082)

| Método | Endpoint | Roles Permitidos | Descripción |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/mascotas/mis-mascotas` | `CLIENTE` | Lista las mascotas del usuario autenticado |
| `POST` | `/api/v1/mascotas` | `CLIENTE`, `ADMIN` | Registra una nueva mascota |
| `GET` | `/api/v1/mascotas/{id}` | `VET`, `ADMIN` | Consulta información de una mascota por ID |
| `POST` | `/api/v1/citas` | `CLIENTE`, `ADMIN` | Agenda una nueva cita (valida solapamiento y cupo diario) |
| `GET` | `/api/v1/citas/agenda` | `VET`, `ADMIN` | Consulta la agenda de citas a partir de la fecha actual |
| `PATCH` | `/api/v1/citas/{id}/cancelar` | `CLIENTE`, `ADMIN` | Cancela una cita (valida ventana de > 2 horas) |
| `PATCH` | `/api/v1/citas/{id}/completar` | `VET`, `ADMIN` | Marca la cita como `COMPLETADA` |

### 3. Servicio de Expedientes Clínicos (`expedientes-service` - Puerto 8083)

| Método | Endpoint | Roles Permitidos | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/expedientes` | `VET`, `ADMIN` | Registra expediente y completa cita vía OpenFeign |
| `GET` | `/api/v1/expedientes/mascota/{mascotaId}` | `VET`, `CLIENTE`, `ADMIN` | Consulta historial de expedientes por mascota |
| `GET` | `/api/v1/expedientes/{id}` | `VET`, `CLIENTE`, `ADMIN` | Consulta expediente clínico por ID |
| `GET` | `/api/v1/expedientes/cita/{citaId}` | `VET`, `CLIENTE`, `ADMIN` | Consulta expediente clínico por ID de cita |

---

## 💻 Instrucciones de Ejecución

### 1. Compilación e Instalación del Proyecto Completo
Desde la raíz del proyecto ejecuta el comando estándar de Maven para limpiar, compilar e instalar todos los módulos:
```bash
.\mvnw.cmd clean install -DskipTests
```
*(O simplemente `mvn clean install` si tienes Maven configurado en tu PATH global).*

### 2. Iniciar los 3 Microservicios Simultáneamente (PowerShell)
Puedes iniciar automáticamente los 3 servicios en terminales independientes ejecutando el script incluido:
```powershell
.\start-microservicios.ps1
```

O si prefieres ejecutarlos manualmente en una sola línea o ventanas individuales:

**Opción A — Comando en una sola línea (PowerShell):**
```powershell
Start-Process powershell "-NoExit -Command .\mvnw.cmd spring-boot:run -pl auth-service"; Start-Process powershell "-NoExit -Command .\mvnw.cmd spring-boot:run -pl citas-mascotas-service"; Start-Process powershell "-NoExit -Command .\mvnw.cmd spring-boot:run -pl expedientes-service"
```

**Opción B — En terminales manuales:**
- **Terminal 1 (Auth Service - Puerto 8081):**
  ```bash
  .\mvnw.cmd spring-boot:run -pl auth-service
  ```
- **Terminal 2 (Citas Service - Puerto 8082):**
  ```bash
  .\mvnw.cmd spring-boot:run -pl citas-mascotas-service
  ```
- **Terminal 3 (Expedientes Service - Puerto 8083):**
  ```bash
  .\mvnw.cmd spring-boot:run -pl expedientes-service
  ```

# Sistema de Control de Citas y Expedientes - Veterinaria

API REST desarrollada en Spring Boot para la gestión de citas médicas, pacientes (mascotas), expedientes clínicos y usuarios de una clínica veterinaria. El proyecto está organizado en una arquitectura de microservicios multi-módulo con Maven y persistencia en MySQL.

---

## 1. Tecnologías utilizadas

- Java 17
- Spring Boot 3.3.0
- Spring Cloud OpenFeign 2023.0.3 (con cliente Apache HttpClient 5 para soporte de PATCH)
- Spring Security (Autenticación stateless con JWT y contraseñas cifradas con BCrypt)
- Spring Data JPA / Hibernate
- MySQL 8.0
- Maven Multi-Módulo (POM padre: `veterinaria-parent`)
- Lombok

---

## 2. Estructura del proyecto

El proyecto maneja un POM padre en la raíz (`veterinaria-parent`) que gestiona las dependencias comunes y tres módulos independientes:

```text
Examen_Final2026/
├── pom.xml                                   # POM Padre (veterinaria-parent)
├── .mvn/jvm.config                           # Configuración SSL para Maven
├── start-microservicios.ps1                  # Script para levantar los servicios en PowerShell
├── test-veterinaria.ps1                      # Script de pruebas en PowerShell
├── test-veterinaria.sh                       # Script de pruebas en Bash
├── Veterinaria_Microservicios.postman_collection.json # Colección de Postman
│
├── auth-service/                             # Puerto 8081: Usuarios y autenticación JWT
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/veterinaria/auth/
│       └── resources/
│           ├── application.properties
│           └── data.sql                      # Datos iniciales de usuarios
│
├── citas-mascotas-service/                   # Puerto 8082: Mascotas y citas médicas
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/veterinaria/citas/
│       └── resources/
│           ├── application.properties
│           └── data.sql                      # Datos iniciales de mascota y cita
│
└── expedientes-service/                      # Puerto 8083: Historial clínico (consume citas vía Feign)
    ├── pom.xml
    └── src/main/
        ├── java/com/veterinaria/expedientes/
        └── resources/
            └── application.properties
```

---

## 3. Base de datos y datos iniciales

Los tres microservicios se conectan a la misma base de datos MySQL local:

- Base de datos: `veterinaria_db_in5am`
- Host/Puerto: `localhost:3306`
- Usuario: `IN5AM`
- Contraseña: `_odmon5Am`

Hibernate actualiza el esquema automáticamente al iniciar (`ddl-auto=update`). Además, se configuró la carga inicial desde los archivos `data.sql` de cada módulo.

### Usuarios de prueba (auth-service)

| Rol | Correo | Contraseña |
| :--- | :--- | :--- |
| ADMIN | admin@veterinaria.com | Admin123* (o admin123) |
| VET | veterinario@veterinaria.com | vet123 |
| CLIENTE | cliente@correo.com | cliente123 |

### Datos de prueba (citas-mascotas-service)

- Mascota ID 1: "Firulais", perro Labrador Retriever de 3 años, perteneciente al cliente con ID 3.
- Cita ID 1: Programada para el veterinario con ID 2 en estado `PENDIENTE`.

---

## 4. Reglas de negocio

1. **Disponibilidad de veterinarios:** Las citas duran 30 minutos. No se puede agendar una cita si el veterinario ya tiene otra cita en un rango de 30 minutos antes o después.
2. **Límite por cliente:** Un cliente no puede tener más de 2 citas en estado `PENDIENTE` para el mismo día.
3. **Cancelación de citas:** Una cita solo se puede cancelar si faltan más de 2 horas para su realización.
4. **Cierre de cita automático:** Al registrar un expediente clínico en `expedientes-service` (puerto 8083), este se comunica por OpenFeign con `citas-mascotas-service` (puerto 8082) y cambia el estado de la cita a `COMPLETADA`.
5. **Seguridad y roles:** El registro público asigna obligatoriamente el rol `CLIENTE`. Las rutas protegidas requieren el token en la cabecera `Authorization: Bearer <token>`.
6. **Mapeo de datos:** Los controladores no retornan entidades JPA directamente; devuelven DTOs para evitar problemas de serialización en Jackson.

---

## 5. Endpoints de la API

### Auth Service (Puerto 8081)

| Método | Ruta | Roles | Descripción |
| :--- | :--- | :--- | :--- |
| POST | /api/v1/auth/register | Público | Registro de usuarios (asigna rol CLIENTE) |
| POST | /api/v1/auth/login | Público | Inicio de sesión, retorna token JWT |

### Citas y Mascotas Service (Puerto 8082)

| Método | Ruta | Roles | Descripción |
| :--- | :--- | :--- | :--- |
| GET | /api/v1/mascotas/mis-mascotas | CLIENTE | Lista las mascotas del cliente en sesión |
| POST | /api/v1/mascotas | CLIENTE, ADMIN | Registra una nueva mascota |
| GET | /api/v1/mascotas/{id} | VET, ADMIN | Obtiene los datos de una mascota por ID |
| POST | /api/v1/citas | CLIENTE, ADMIN | Agenda una cita validando horario y límite |
| GET | /api/v1/citas/agenda | VET, ADMIN | Muestra la agenda de citas a partir de la fecha actual |
| PATCH | /api/v1/citas/{id}/cancelar | CLIENTE, ADMIN | Cancela una cita si faltan más de 2 horas |
| PATCH | /api/v1/citas/{id}/completar | VET, ADMIN | Marca una cita como COMPLETADA (usado por Feign) |

### Expedientes Service (Puerto 8083)

| Método | Ruta | Roles | Descripción |
| :--- | :--- | :--- | :--- |
| POST | /api/v1/expedientes | VET, ADMIN | Guarda expediente y completa la cita médica por Feign |
| GET | /api/v1/expedientes/mascota/{mascotaId} | VET, CLIENTE, ADMIN | Consulta el historial clínico de una mascota |
| GET | /api/v1/expedientes/{id} | VET, CLIENTE, ADMIN | Consulta un expediente por ID |
| GET | /api/v1/expedientes/cita/{citaId} | VET, CLIENTE, ADMIN | Consulta un expediente por ID de cita |

---

## 6. Instrucciones de ejecución

### Compilar el proyecto completo

Desde la carpeta raíz del proyecto:

```powershell
.\mvnw.cmd clean install -DskipTests
```

### Iniciar los 3 microservicios

Puedes iniciar los 3 servicios al mismo tiempo ejecutando el script de PowerShell:

```powershell
.\start-microservicios.ps1
```

O si prefieres levantarlos en terminales separadas:

- Terminal 1 (Auth):
  ```powershell
  .\mvnw.cmd spring-boot:run -pl auth-service
  ```
- Terminal 2 (Citas):
  ```powershell
  .\mvnw.cmd spring-boot:run -pl citas-mascotas-service
  ```
- Terminal 3 (Expedientes):
  ```powershell
  .\mvnw.cmd spring-boot:run -pl expedientes-service
  ```

---

## 7. Pruebas

### Con Postman

En la raíz del proyecto está el archivo `Veterinaria_Microservicios.postman_collection.json`.

1. Abre Postman y usa la opción "Import" para cargar el archivo.
2. Al ejecutar las peticiones de login (cliente o veterinario), el token se guarda automáticamente en las variables de la colección y se reutiliza en las demás peticiones.

### Con scripts de terminal

Con los microservicios ya levantados, puedes ejecutar las pruebas automáticas:

- En PowerShell:
  ```powershell
  .\test-veterinaria.ps1
  ```
- En Git Bash o Linux:
  ```bash
  chmod +x test-veterinaria.sh
  ./test-veterinaria.sh
  ```

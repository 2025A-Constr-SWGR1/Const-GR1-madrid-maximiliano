# Documentación Técnica - Práctica 4 (Versión 1.3.0)

Esta documentación técnica describe la evolución y las funcionalidades implementadas en la Práctica 4 del proyecto *ToDo List*, centrada en el despliegue en producción y las mejoras técnicas para la versión 1.3.0.

## Cambios introducidos en la aplicación (Versión 1.3.0)

### Nuevas funcionalidades implementadas

#### 1. Exportación de tareas a CSV
- **Endpoint:** `GET /usuarios/{id}/tareas/exportar`
- **Funcionalidad:** Permite exportar todas las tareas de un usuario a formato CSV
- **Implementación:** 
  - Método `generarCSV()` en `TareaService`
  - Endpoint en `TareaController` que configura respuesta HTTP como archivo CSV
  - Botón de exportación en la vista `listaTareas.html`

#### 2. Borrado múltiple de tareas
- **Endpoints:** 
  - `POST /usuarios/{id}/tareas/borrar-multiples` (formulario)
  - `DELETE /tareas/multiple` (JSON)
- **Funcionalidad:** Permite borrar varias tareas simultáneamente
- **Implementación:** Nuevos métodos en `TareaController` con validación de permisos

#### 3. Mejoras en la gestión de fechas
- **Adición del campo:** `fechaVencimiento` en las tareas
- **Soporte:** Para tareas con fechas de vencimiento opcionales

#### 4. Sistema de equipos (preparado para futuras versiones)
- **Modelo:** Tabla `equipos` y `usuario_equipo` en esquema 1.3.0
- **Relaciones:** Usuarios pueden pertenecer a múltiples equipos
- **Administración:** Cada equipo tiene un administrador

### Mejoras técnicas y de infraestructura

#### 1. Dockerización completa
- **Dockerfile multi-etapa:** Optimización del build y ejecución
- **Docker Compose:** Orquestación de aplicación + PostgreSQL
- **Persistencia:** Volúmenes para datos de PostgreSQL
- **Health checks:** Verificación de salud de servicios

#### 2. Migraciones de base de datos
- **Flyway:** Sistema de migraciones versionadas
- **Esquemas:** Migración de 1.2.0 a 1.3.0 con nuevas tablas
- **Compatibilidad:** Soporte para H2 (desarrollo) y PostgreSQL (producción)

#### 3. Testing mejorado
- **Cobertura expandida:** Tests para nuevos endpoints
- **MockMvc:** Tests de integración para controladores
- **Validación:** Tests para borrado múltiple y exportación CSV

### Controladores modificados/añadidos

#### `TareaController`
- **Nuevos endpoints:**
  - `GET /usuarios/{id}/tareas/exportar` - Exportar tareas a CSV
  - `POST /usuarios/{id}/tareas/borrar-multiples` - Borrado múltiple por formulario
  - `DELETE /tareas/multiple` - Borrado múltiple por JSON
  - `POST /tareas/test-multiple` - Endpoint de prueba para borrado múltiple

### Servicios extendidos

#### `TareaService`
- **Nuevos métodos:**
  - `generarCSV(List<TareaData> tareas)`: Genera contenido CSV de las tareas
  - Soporte para fechas de vencimiento en creación y modificación de tareas


### Plantillas Thymeleaf modificadas

#### `listaTareas.html`
- **Funcionalidad añadida:** Botón "Exportar a CSV" que permite descargar las tareas del usuario
- **Implementación:** Enlace que redirige al endpoint de exportación

#### `fragments.html`
- **Mejoras:** Inclusión de librerías JavaScript actualizadas para funcionalidades adicionales

## Detalles del despliegue de producción

### Arquitectura de despliegue

La aplicación se despliega utilizando **Docker Compose** con los siguientes componentes:

#### 1. Aplicación Spring Boot
- **Imagen:** `mads-todolist-equipo04:1.3.0-snapshot`
- **Puerto:** 8080
- **Perfil:** `postgres` (producción)
- **Dependencias:** PostgreSQL (healthcheck)

#### 2. Base de datos PostgreSQL
- **Imagen:** `postgres:16`
- **Puerto:** 5432
- **Credenciales:** 
  - Usuario: `mads`
  - Contraseña: `mads`
  - Base de datos: `mads`
- **Persistencia:** `./docker-data/postgres`
- **Health check:** Verificación cada 10 segundos

#### 3. Red y volúmenes
- **Red:** `network-equipo` (bridge)
- **Volúmen:** Persistencia de datos PostgreSQL en directorio local

### Proceso de despliegue

#### Paso 1: Compilación
```bash
./mvnw clean package -DskipTests
```

#### Paso 2: Construcción de imagen Docker
```bash
docker build -t mads-todolist-equipo04:1.3.0-snapshot .
```

#### Paso 3: Despliegue con Docker Compose
```bash
mkdir -p docker-data
docker-compose up -d
```

#### Paso 4: Verificación
```bash
docker-compose ps
curl http://localhost:8080
```

### Variables de entorno

#### Aplicación
- `SPRING_PROFILES_ACTIVE=postgres`
- `POSTGRES_HOST=postgres`

#### PostgreSQL
- `POSTGRES_USER=mads`
- `POSTGRES_PASSWORD=mads`
- `POSTGRES_DB=mads`

## Esquemas de datos

### Esquema versión 1.2.0

```sql
-- Tabla usuarios
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE,
    es_admin BOOLEAN DEFAULT false,
    bloqueado BOOLEAN DEFAULT false,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla tareas
CREATE TABLE tareas (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Índices para optimización
CREATE INDEX idx_tareas_usuario_id ON tareas(usuario_id);
CREATE INDEX idx_usuarios_email ON usuarios(email);
```

### Esquema versión 1.3.0

**Cambios añadidos:**

```sql
-- Añadir descripción a tareas
ALTER TABLE tareas ADD COLUMN descripcion TEXT;

-- Nueva tabla equipos
CREATE TABLE equipos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    administrador_id BIGINT NOT NULL,
    FOREIGN KEY (administrador_id) REFERENCES usuarios(id)
);

-- Tabla de relación usuario-equipo (muchos a muchos)
CREATE TABLE usuario_equipo (
    usuario_id BIGINT NOT NULL,
    equipo_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, equipo_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (equipo_id) REFERENCES equipos(id) ON DELETE CASCADE
);
```

## Script de migración de base de datos

### Migración automática con Flyway

El proyecto utiliza **Flyway** para gestionar las migraciones de base de datos de forma automática:

#### Archivos de migración:
- `src/main/resources/db/migration/schema-1.2.0.sql` - Esquema inicial
- `src/main/resources/db/migration/schema-1.2.0-1.3.0.sql` - Migración a versión 1.3.0
- `src/main/resources/db/migration/schema-1.3.0.sql` - Esquema completo 1.3.0

#### Configuración Flyway:
```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```

### Script manual de migración (1.2.0 → 1.3.0)

```sql
-- Migración manual de versión 1.2.0 a 1.3.0
-- Ejecutar en PostgreSQL

-- 1. Añadir descripción a tareas existentes
ALTER TABLE tareas ADD COLUMN descripcion TEXT;

-- 2. Crear tabla equipos
CREATE TABLE equipos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    administrador_id BIGINT NOT NULL,
    FOREIGN KEY (administrador_id) REFERENCES usuarios(id)
);

-- 3. Crear tabla de relación usuario-equipo
CREATE TABLE usuario_equipo (
    usuario_id BIGINT NOT NULL,
    equipo_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, equipo_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (equipo_id) REFERENCES equipos(id) ON DELETE CASCADE
);

-- 4. Crear índices para optimización
CREATE INDEX idx_equipos_administrador ON equipos(administrador_id);
CREATE INDEX idx_usuario_equipo_usuario ON usuario_equipo(usuario_id);
CREATE INDEX idx_usuario_equipo_equipo ON usuario_equipo(equipo_id);

-- 5. Verificar migración
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public' 
ORDER BY table_name;
```

## URL de la imagen Docker

### Docker Hub
- **Repositorio:** [kealpetu/todolist-app-equipo-5](https://hub.docker.com/repository/docker/kealpetu/todolist-app-equipo-5/general)
- **Tag latest:** `kealpetu/todolist-app-equipo-5:latest`
- **Tag versión:** `kealpetu/todolist-app-equipo-5:1.3.0`

### Comandos para usar la imagen:

```bash
# Descargar imagen desde Docker Hub
docker pull kealpetu/todolist-app-equipo-5:latest

# Ejecutar contenedor desde Docker Hub
docker run -p 8080:8080 kealpetu/todolist-app-equipo-5:latest

# Usar en docker-compose.yml
services:
  app:
    image: kealpetu/todolist-app-equipo-5:1.3.0
    ports:
      - "8080:8080"
```

### Imagen local
- **Nombre:** `mads-todolist-equipo04:1.3.0-snapshot`
- **Construcción:** `docker build -t mads-todolist-equipo04:1.3.0-snapshot .`

## Tests implementados

### Tests de servicio (`TareaServiceTest`)
- `testNuevaTareaUsuario()` - Crear nueva tarea
- `testBuscarTarea()` - Buscar tarea por ID
- `testModificarTarea()` - Modificar tarea existente
- `testBorrarTarea()` - Eliminar tarea
- `testGenerarCSV()` - Generar archivo CSV
- `asignarEtiquetaATarea()` - Verificar relación usuario-tarea

### Tests de controlador (`TareaWebTest`)
- `listaTareas()` - Listar tareas de usuario
- `getNuevaTareaDevuelveForm()` - Formulario nueva tarea
- `postNuevaTareaDevuelveRedirectYAñadeTarea()` - Crear tarea
- `deleteTareaDevuelveOKyBorraTarea()` - Eliminar tarea
- `editarTareaActualizaLaTarea()` - Editar tarea
- `exportarTareasDevuelveCSV()` - Exportar a CSV
- `borrarMultiplesTareasPorFormulario()` - Borrado múltiple (formulario)
- `borrarMultiplesTareasPorJson()` - Borrado múltiple (JSON)
- `testEndpointTestMultiple()` - Endpoint de prueba

## Ejemplo de código relevante

### Método de exportación CSV en `TareaService`

```java
public String generarCSV(List<TareaData> tareas) {
    StringBuilder csv = new StringBuilder();
    csv.append("ID,Título,Fecha Vencimiento\n");
    
    for (TareaData tarea : tareas) {
        csv.append(String.format("%d,%s,%s\n",
            tarea.getId(),
            tarea.getTitulo(),
            tarea.getFechaVencimiento() != null ? 
                tarea.getFechaVencimiento().toString() : ""));
    }
    
    return csv.toString();
}
```

### Endpoint de exportación en `TareaController`

```java
@GetMapping("/usuarios/{id}/tareas/exportar")
public void exportarTareas(@PathVariable("id") Long idUsuario,
                          HttpServletResponse response) throws IOException {
    List<TareaData> tareas = tareaService.allTareasUsuario(idUsuario);
    
    response.setContentType("text/csv");
    response.setHeader("Content-Disposition", 
        "attachment; filename=\"tareas_usuario_" + idUsuario + ".csv\"");
    
    try (PrintWriter writer = response.getWriter()) {
        writer.println("ID,Título,Fecha Vencimiento");
        
        for (TareaData tarea : tareas) {
            writer.println(String.format("%d,%s,%s",
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getFechaVencimiento() != null ? 
                    tarea.getFechaVencimiento().toString() : ""));
        }
    }
}
```

---

**Nota:** Con esta implementación, la aplicación versión 1.3.0 incluye funcionalidades avanzadas de exportación, gestión masiva de tareas, y un sistema completo de despliegue con Docker, preparando el terreno para futuras funcionalidades como la gestión de equipos.


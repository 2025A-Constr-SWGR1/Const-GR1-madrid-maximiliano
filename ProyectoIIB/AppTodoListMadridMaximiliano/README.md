   # ToDoList - Aplicación de Gestión de Tareas

   [![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/zs7YQjvj)

   Una aplicación web completa para la gestión de tareas desarrollada con Spring Boot y Thymeleaf como parte de la asignatura Metodologías Ágiles 2025-A de la Escuela Politécnica Nacional (EPN).

## 📋 Descripción del Proyecto

Este proyecto es una aplicación ToDoList que permite a los usuarios crear, gestionar y organizar sus tareas de manera eficiente. La aplicación incluye un sistema de autenticación, gestión de usuarios con roles administrativos, y funcionalidades avanzadas como exportación de datos. Soporta tanto base de datos H2 (desarrollo) como PostgreSQL (producción) con despliegue completo usando Docker Compose.


## 👥 Equipo de Desarrollo - Grupo 5
- **Kevin Peñafiel** - Desarrollo Docker y funcionalidad CSV
- **Nick Valverde** - Desarrollo core y gestión de proyecto
- **Karina Arichavala** - Desarrollo frontend y testing
- **Fernando Aldaz** - Desarrollo backend y servicios
- **Álvaro Zumbana** - Desarrollo y documentación
- **Maximiliano Madrid** - Configuración y optimización del entorno de despliegue (Docker + PostgreSQL + Spring Boot)

**Versión:** 1.3.0  
**Organización:** Escuela Politécnica Nacional (EPN)  
**Asignatura:** Metodologías Ágiles 2025-A

## ✨ Características Principales

### Gestión de Usuarios
- **Registro y autenticación** de usuarios
- **Roles administrativos** con permisos especiales
- **Bloqueo y desbloqueo** de usuarios por administradores
- **Perfil de usuario** con información personal
- **Listado de usuarios** (solo para administradores)

### Gestión de Tareas
- **Crear, editar y eliminar** tareas
- **Marcar tareas como completadas**
- **Organización por usuario**
- **Fechas de creación y vencimiento** automáticas
- **Exportación a CSV** de las tareas de un usuario
- **Borrado múltiple** de tareas seleccionadas

### Características Técnicas
- **Interfaz web responsive** con Bootstrap
- **Base de datos H2** (desarrollo) y **PostgreSQL** (producción)
- **Migraciones con Flyway**
- **Contenedorización con Docker** y **Docker Compose**
- **Testing automatizado** con JUnit y MockMvc
- **Arquitectura MVC** con Spring Boot
- **Perfiles de configuración** para diferentes entornos

## 🛠️ Stack Tecnológico

### Backend
- **Java 17** - Lenguaje de programación
- **Spring Boot 2.7.14** - Framework principal
- **Spring Data JPA** - Persistencia de datos
- **Spring Security** - Autenticación y autorización
- **H2 Database** - Base de datos en memoria (desarrollo)
- **PostgreSQL** - Base de datos relacional (producción)
- **Flyway** - Migraciones de base de datos
- **Maven** - Gestión de dependencias y build

### Frontend
- **Thymeleaf** - Motor de plantillas
- **Bootstrap** - Framework CSS
- **HTML5 y CSS3** - Estructura y estilos
- **JavaScript** - Funcionalidad del lado del cliente

### DevOps y Testing
- **Docker** - Contenedorización
- **Docker Compose** - Orquestación de contenedores
- **JUnit 5** - Testing unitario
- **MockMvc** - Testing de controladores
- **Maven Surefire** - Ejecución de tests
- **GitHub Actions** - CI/CD (configurado)

## 📋 Requisitos del Sistema

### Requisitos Mínimos
- **Java 17** o superior
- **Maven 3.6+** (incluido wrapper)
- **4GB RAM** mínimo
- **50MB** espacio en disco

### Requisitos para Docker
- **Docker** (versión 20.10 o superior)
- **Docker Compose** (versión 2.0 o superior)
- **Git** (para clonar el repositorio)

### Verificar Instalaciones
```bash
# Verificar Docker
docker --version
docker-compose --version

# Verificar Git
git --version

# Verificar Java
java -version
```

## 🚀 Guías de Instalación y Ejecución

### Opción 1: Despliegue Completo con Docker Compose (Recomendado)

#### Paso 1: Obtener el Código Fuente
```bash
# Clonar el repositorio
git clone https://github.com/Nick09V/Practica_4_Metodologias.git
cd Practica_4_Metodologias
```

#### Paso 2: Verificar Estructura del Proyecto
Tu proyecto debe tener esta estructura:
```
Practica_4_Metodologias/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── src/
│   └── main/
│       └── resources/
│           ├── application.properties
│           ├── application-postgres.properties
│           └── application-postgres-prod.properties
└── docker-data/ (se crea automáticamente)
```

#### Paso 3: Compilar la Aplicación
```bash
# Compilar el proyecto
./mvnw clean package -DskipTests

# En Windows usar:
# mvnw.cmd clean package -DskipTests

# Verificar que se generó el JAR
ls -la target/*.jar
```

#### Paso 4: Construir la Imagen Docker
```bash
# Construir la imagen de la aplicación
docker build -t kealpetu/todolist-app-equipo-5:1.3.0 .

# Verificar que la imagen se creó
docker images | grep kealpetu/todolist-app-equipo-5
```

#### Paso 5: Crear Directorio de Datos y Ejecutar
```bash
# Crear directorio para datos de PostgreSQL
mkdir -p docker-data

# Inicializar todos los servicios
docker-compose up -d

# Ver el estado de los servicios
docker-compose ps
```

#### Paso 6: Verificar el Despliegue
```bash
# Ver logs de la aplicación
docker-compose logs app

# Probar la aplicación
curl http://localhost:8080
# O abrir en navegador: http://localhost:8080/login
```

### Opción 2: Ejecución Local con Maven (Desarrollo)

#### Paso 1: Clonar y Navegar
```bash
git clone https://github.com/Nick09V/Practica_4_Metodologias.git
cd Practica_4_Metodologias
```

#### Paso 2: Ejecutar con Maven
```bash
# Ejecución directa
./mvnw spring-boot:run

# O compilar y ejecutar JAR
./mvnw clean package
java -jar target/todolist-equipo-grupo5-1.3.0.jar
```

### Opción 3: Solo Docker (Sin Compose)
```bash
# Construir imagen
docker build -t todolist-app .

# Ejecutar contenedor (solo con H2)
docker run -p 8080:8080 todolist-app
```

## 🔧 Comandos de Gestión Docker Compose

### Comandos Básicos
```bash
# Iniciar servicios
docker-compose up -d

# Parar servicios (mantiene datos)
docker-compose stop

# Iniciar servicios parados
docker-compose start

# Parar y eliminar contenedores (mantiene datos)
docker-compose down

# Ver estado de servicios
docker-compose ps

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs app
docker-compose logs postgres
```

### Comandos de Mantenimiento
```bash
# Acceder a la base de datos PostgreSQL
docker-compose exec postgres psql -U mads -d mads

# Ejecutar comandos SQL directamente
docker-compose exec postgres psql -U mads -d mads -c "SELECT * FROM usuarios;"

# Hacer backup de la base de datos
docker-compose exec postgres pg_dump -U mads --clean mads > backup_$(date +%Y%m%d).sql

# Restaurar backup
docker-compose exec -T postgres psql -U mads mads < backup_20250727.sql
```

### Comandos de Desarrollo
```bash
# Reconstruir aplicación después de cambios
./mvnw clean package -DskipTests
docker build -t kealpetu/todolist-app-equipo-5:1.3.0 .
docker-compose up -d --force-recreate app

# Ver variables de entorno de un contenedor
docker-compose exec app env

# Acceder al contenedor de la aplicación
docker-compose exec app sh
```

## 🔄 Actualización de la Aplicación

Cuando hagas cambios en el código:

```bash
# 1. Parar la aplicación (mantener DB)
docker-compose stop app

# 2. Recompilar
./mvnw clean package -DskipTests

# 3. Reconstruir imagen
docker build -t kealpetu/todolist-app-equipo-5:1.3.0 .

# 4. Reiniciar aplicación
docker-compose up -d app
```

## 📖 Guía de Uso

### Registro de Usuarios
1. Acceder a la página de login: [http://localhost:8080/login](http://localhost:8080/login)
2. Hacer clic en "Registrarse"
3. Completar el formulario con:
   - Email válido
   - Nombre completo
   - Contraseña
   - Fecha de nacimiento
   - Opción de registro como administrador (si es el primer usuario)

### Gestión de Tareas
1. **Crear Nueva Tarea:**
   - Ir a "Mis Tareas"
   - Hacer clic en "Nueva Tarea"
   - Introducir el título y guardar

2. **Editar Tarea:**
   - En la lista de tareas, hacer clic en el botón "Editar"
   - Modificar el título
   - Guardar cambios

3. **Eliminar Tarea:**
   - Hacer clic en el botón "Borrar" junto a la tarea
   - Confirmar la eliminación

4. **Exportar Tareas:**
   - En la lista de tareas, hacer clic en "Exportar a CSV"
   - El archivo se descargará automáticamente

5. **Borrar Múltiples Tareas:**
   - Seleccionar varias tareas usando los checkboxes
   - Hacer clic en "Borrar Seleccionadas"
   - Confirmar la eliminación múltiple

### Administración (Solo Administradores)
1. **Ver Usuarios Registrados:**
   - Acceder a "Usuarios Registrados" en el menú
   - Ver lista completa de usuarios

2. **Gestionar Estados de Usuario:**
   - Hacer clic en "Habilitar/Bloquear" junto a un usuario
   - Los usuarios bloqueados no pueden iniciar sesión

## 🔧 API y Endpoints

### Endpoints Públicos
- `GET /` - Página de inicio
- `GET /login` - Página de login
- `POST /login` - Procesar login
- `GET /registro` - Página de registro
- `POST /registro` - Procesar registro

### Endpoints de Usuario Autenticado
- `GET /usuarios/{id}/tareas` - Listar tareas del usuario
- `GET /usuarios/{id}/tareas/nueva` - Formulario nueva tarea
- `POST /usuarios/{id}/tareas/nueva` - Crear nueva tarea
- `GET /tareas/{id}/editar` - Formulario editar tarea
- `POST /tareas/{id}/editar` - Actualizar tarea
- `DELETE /tareas/{id}` - Eliminar tarea
- `GET /usuarios/{id}/tareas/exportar` - Exportar tareas a CSV
- `POST /usuarios/{id}/tareas/borrar-multiples` - Borrar múltiples tareas (formulario)
- `DELETE /tareas/multiple` - Borrar múltiples tareas (JSON)
- `POST /tareas/test-multiple` - Endpoint de prueba para borrado múltiple

### Endpoints de Administrador
- `GET /registrados` - Listar todos los usuarios
- `GET /registrados/{id}` - Ver detalles de usuario
- `POST /registrados/{id}/toggle-bloqueo` - Cambiar estado de bloqueo

### URLs de Monitoreo
- **Aplicación**: http://localhost:8080
- **Actuator Health**: http://localhost:8080/actuator/health

## 🧪 Testing

### Ejecutar Tests
```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar tests específicos
./mvnw test -Dtest=TareaServiceTest

# Ejecutar tests con reporte detallado
./mvnw test -Dsurefire.printSummary=true
```

### Cobertura de Tests
El proyecto incluye tests para:
- **Servicios:** UsuarioService, TareaService (incluyendo exportación CSV)
- **Controladores:** LoginController, TareaController (incluyendo borrado múltiple), UsuarioController
- **Integración:** Tests end-to-end con MockMvc
- **Modelos:** Tests de entidades y DTOs
- **Funcionalidades avanzadas:** Tests para exportación CSV y borrado múltiple

## 🗃️ Configuración de Base de Datos

### Base de Datos H2 (Desarrollo)
- **Tipo:** Base de datos en memoria
- **Consola H2:** Disponible en `/h2-console` (en desarrollo)
- **URL:** `jdbc:h2:mem:testdb`
- **Usuario:** `sa`
- **Contraseña:** (vacía)

### Base de Datos PostgreSQL (Producción con Docker)
- **Host:** postgres (interno), localhost:5432 (externo)
- **Base de datos:** mads
- **Usuario:** mads
- **Contraseña:** password
- **Persistencia:** `./docker-data/postgres`

### Migraciones Flyway
Las migraciones se encuentran en `src/main/resources/db/migration/`:
- Creación de tablas de usuarios y tareas
- Configuración de claves foráneas
- Datos iniciales (si es necesario)

## 🐳 Configuración Docker

### Dockerfile Multi-etapa
El proyecto utiliza un Dockerfile optimizado con build multi-etapa:

```dockerfile
# Etapa 1: Construcción
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/urandom", "-jar", "app.jar"]
```

### Docker Compose
Orquesta los siguientes servicios:
- **app**: Aplicación Spring Boot
- **postgres**: Base de datos PostgreSQL
- **Volúmenes**: Persistencia de datos
- **Redes**: Comunicación entre contenedores

### Imagen Docker Hub
La aplicación está disponible en Docker Hub:
- **Repositorio:** [kealpetu/todolist-app-equipo-5](https://hub.docker.com/repository/docker/kealpetu/todolist-app-equipo-5/general)
- **Comando:** `docker pull kealpetu/todolist-app-equipo-5:latest`

## 🔧 Configuración Avanzada

### Perfiles de Spring
- **default:** Desarrollo local con H2
- **postgres:** Desarrollo con PostgreSQL
- **postgres-prod:** Producción con PostgreSQL
- **test:** Ejecución de tests

### Variables de Entorno Personalizadas
Puedes crear un archivo `.env` en el directorio raíz:

```bash
# .env
POSTGRES_USER=mi_usuario
POSTGRES_PASSWORD=mi_password_seguro
POSTGRES_DB=mi_base_datos
APP_PORT=8080
DB_PORT=5432
```

### Propiedades de Aplicación
Los archivos de configuración incluyen:
- `application.properties` - Configuración base
- `application-postgres.properties` - PostgreSQL desarrollo
- `application-postgres-prod.properties` - PostgreSQL producción

## 💾 Gestión de Datos

### Backup de Base de Datos
```bash
# Crear backup con timestamp
docker-compose exec postgres pg_dump -U mads --clean mads > backup_$(date +%Y%m%d_%H%M%S).sql

# Comprimir backup
gzip backup_*.sql
```

### Restaurar Backup
```bash
# Restaurar desde backup
docker-compose exec -T postgres psql -U mads mads < backup_20250727.sql
```

### Limpieza Completa (⚠️ ELIMINA TODOS LOS DATOS)
```bash
# Parar y eliminar todo
docker-compose down -v

# Eliminar directorio de datos (CUIDADO!)
rm -rf docker-data/

# Eliminar imagen de la aplicación
docker rmi kealpetu/todolist-app-equipo-5:1.3.0
```

## 🐛 Solución de Problemas

### Problema: "No se puede conectar a la base de datos"
```bash
# Verificar que PostgreSQL esté corriendo
docker-compose ps

# Ver logs de PostgreSQL
docker-compose logs postgres

# Reiniciar servicios
docker-compose restart
```

### Problema: "Puerto 8080 ya está en uso"
```bash
# Ver qué está usando el puerto
lsof -i :8080  # En Linux/Mac
netstat -ano | findstr :8080  # En Windows

# Cambiar puerto en docker-compose.yml
ports:
  - "8081:8080"  # Usar puerto 8081 en su lugar
```

### Problema: "Error al compilar"
```bash
# Verificar versión de Java
java -version

# Limpiar completamente y recompilar
./mvnw clean
./mvnw compile
./mvnw package -DskipTests
```

### Problema: "Imagen Docker no se encuentra"
```bash
# Verificar que la imagen existe
docker images | grep kealpetu/todolist-app-equipo-5

# Si no existe, reconstruir
docker build -t kealpetu/todolist-app-equipo-5:1.3.0 .
```

## 📊 Verificación del Despliegue

### Lista de Verificación
- [ ] Los contenedores están corriendo: `docker-compose ps`
- [ ] La aplicación responde: `curl http://localhost:8080`
- [ ] PostgreSQL acepta conexiones: `docker-compose exec postgres pg_isready -U mads`
- [ ] Los logs no muestran errores: `docker-compose logs`
- [ ] Se puede acceder al login: http://localhost:8080/login

### Puertos Utilizados
- **8080**: Aplicación Spring Boot
- **5432**: Base de datos PostgreSQL

### Salida Esperada de `docker-compose ps`
```
NAME              COMMAND                  SERVICE      STATUS        PORTS
db-equipo         "docker-entrypoint.s…"   postgres     Up (healthy)  0.0.0.0:5432->543
```

   ## 📚 Documentación Adicional

   ### Documentación Técnica
   - **Práctica 4:** [doc/practica4.md](doc/practica4.md) - Documentación completa versión 1.3.0
   - **Práctica 2:** [doc/practica2.md](doc/practica2.md) - Funcionalidades avanzadas
   - **Feature Docker:** [doc/feature/docker-kevin.md](doc/feature/docker-kevin.md) - Implementación Docker
   - **Feature CSV:** [doc/feature/export-csv-kevin.md](doc/feature/export-csv-kevin.md) - Exportación CSV

   ### Gestión de Proyecto
   - **Trello:** [Tablero del Proyecto](f)
   - **GitHub:** [Repositorio](https://github.com/Nick09V/Practica_4_Metodologias)

   ## 🤝 Contribución

   ### Flujo de Trabajo
   1. **Fork** del repositorio
   2. **Crear rama** para la feature: `git checkout -b feature/nueva-funcionalidad`
   3. **Commit** cambios: `git commit -m 'Añadir nueva funcionalidad'`
   4. **Push** a la rama: `git push origin feature/nueva-funcionalidad`
   5. **Abrir Pull Request**

   ### Estándares de Código
   - **Java:** Seguir convenciones de Oracle
   - **Commits:** Mensajes descriptivos en español
   - **Tests:** Cobertura mínima del 80%
   - **Documentación:** Actualizar README si es necesario

   ## 📝 Changelog

   ### Versión 1.3.0
   - ✅ Implementación completa de gestión de usuarios
   - ✅ Sistema de roles administrativos
   - ✅ Funcionalidad de exportación CSV
   - ✅ Borrado múltiple de tareas
   - ✅ Dockerización con multi-stage build
   - ✅ Testing automatizado completo
   - ✅ Migraciones de base de datos con Flyway
   - ✅ Soporte para fechas de vencimiento en tareas

   ### Versiones Anteriores
   - **1.2.0:** Gestión básica de tareas
   - **1.1.0:** Sistema de autenticación
   - **1.0.0:** Estructura inicial del proyecto
   ## 📄 Licencia

   Este proyecto es desarrollado con fines académicos para la asignatura Metodologías Ágiles de la Escuela Politécnica Nacional.

   ## 🆘 Soporte

   Para preguntas o soporte técnico:
   - **Issues:** [GitHub Issues](https://github.com/Nick09V/Practica_4_Metodologias/issues)
   - **Documentación:** Ver carpeta `doc/` para detalles técnicos
   - **Equipo:** Contactar a cualquier miembro del Grupo 5

   ---

   **Desarrollado con ❤️ por el Grupo 5 - EPN 2025**

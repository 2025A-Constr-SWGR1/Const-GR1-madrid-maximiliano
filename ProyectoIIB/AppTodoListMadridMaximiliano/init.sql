-- Eliminar tablas si ya existen (en orden para evitar errores por claves foráneas)
DROP TABLE IF EXISTS tareas CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- Crear tabla de usuarios
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE
);

-- Crear tabla de tareas
CREATE TABLE tareas (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completada BOOLEAN DEFAULT FALSE,
    prioridad VARCHAR(20) DEFAULT 'MEDIA',  -- Asumiendo uso de enum: BAJA, MEDIA, ALTA
    fecha_vencimiento DATE,
    usuario_id INTEGER NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);


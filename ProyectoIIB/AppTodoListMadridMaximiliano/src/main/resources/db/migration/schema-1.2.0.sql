-- Versión 1.2.0 - Esquema inicial
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
CREATE TABLE tareas (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);
-- Índices para mejorar rendimiento
CREATE INDEX idx_tareas_usuario_id ON tareas(usuario_id);
CREATE INDEX idx_usuarios_email ON usuarios(email);
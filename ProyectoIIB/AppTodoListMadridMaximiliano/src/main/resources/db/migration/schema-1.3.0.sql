-- Versión 1.3.0 - Añadir descripción a Equipo
ALTER TABLE tareas
ADD COLUMN descripcion TEXT;
-- Tabla equipos (nueva funcionalidad)
CREATE TABLE equipos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    administrador_id BIGINT NOT NULL,
    FOREIGN KEY (administrador_id) REFERENCES usuarios(id)
);
CREATE TABLE usuario_equipo (
    usuario_id BIGINT NOT NULL,
    equipo_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, equipo_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (equipo_id) REFERENCES equipos(id) ON DELETE CASCADE
);
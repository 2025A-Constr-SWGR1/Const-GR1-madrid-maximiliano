-- Migración de base de datos versión 1.3.0 a 1.4.0
-- Añade la funcionalidad de marcar tareas como completadas

-- Añadir columna 'completada' a la tabla tareas
ALTER TABLE tareas ADD COLUMN completada BOOLEAN DEFAULT FALSE;

-- Actualizar tareas existentes para que no estén marcadas como completadas por defecto
UPDATE tareas SET completada = FALSE WHERE completada IS NULL;

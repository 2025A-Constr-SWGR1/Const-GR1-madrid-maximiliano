# Copia de seguridad
pg_dump -U mads --clean mads > /mi-host/backup-DDMMYYYY.sql

# Restauración
psql -U mads mads < /mi-host/backup-DDMMYYYY.sql

# Aplicar migración
psql -U mads mads < /mi-host/schema-1.2.0-1.3.0.sql

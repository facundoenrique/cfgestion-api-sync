-- Crear tabla para emails de alerta de empresas
CREATE TABLE empresa_email_alertas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    nombre_contacto VARCHAR(100),
    tipo_alerta VARCHAR(50) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_ultima_activacion DATETIME,
    descripcion VARCHAR(500),
    
    -- Foreign key a empresas
    CONSTRAINT fk_empresa_email_alerta_empresa 
        FOREIGN KEY (empresa_id) REFERENCES empresas(id) ON DELETE CASCADE,
    
    -- Índices para mejorar performance
    INDEX idx_empresa_email_alerta_empresa (empresa_id),
    INDEX idx_empresa_email_alerta_tipo (tipo_alerta),
    INDEX idx_empresa_email_alerta_activo (activo),
    INDEX idx_empresa_email_alerta_email (email),
    
    -- Índice compuesto para búsquedas frecuentes
    INDEX idx_empresa_email_alerta_empresa_tipo_activo (empresa_id, tipo_alerta, activo),
    
    -- Constraint para evitar duplicados activos
    CONSTRAINT uk_empresa_email_tipo_activo 
        UNIQUE (empresa_id, email, tipo_alerta, activo)
);

-- Comentarios para documentar la tabla
ALTER TABLE empresa_email_alertas 
COMMENT = 'Tabla para gestionar emails de alerta asociados a empresas';

-- Insertar algunos tipos de alerta de ejemplo (opcional)
-- Estos valores deben coincidir con el enum TipoAlerta en Java
-- INSERT INTO empresa_email_alertas (empresa_id, email, nombre_contacto, tipo_alerta, descripcion) 
-- VALUES (1, 'admin@empresa.com', 'Administrador', 'ERROR_CAE', 'Alerta para errores de CAE'); 
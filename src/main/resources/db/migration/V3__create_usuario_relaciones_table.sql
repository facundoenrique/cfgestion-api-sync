CREATE TABLE usuario_relaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo_relacion VARCHAR(10) NOT NULL,
    entidad_id BIGINT NOT NULL,
    CONSTRAINT fk_usuario_relacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT chk_tipo_relacion CHECK (tipo_relacion IN ('CLIENTE', 'EMPRESA'))
);

CREATE INDEX idx_usuario_relacion_usuario ON usuario_relaciones(usuario_id);
CREATE INDEX idx_usuario_relacion_entidad ON usuario_relaciones(entidad_id);
CREATE UNIQUE INDEX idx_usuario_relacion_unique ON usuario_relaciones(usuario_id, tipo_relacion, entidad_id); 
-- Insertar empresa Super Ideal
INSERT INTO empresas (uuid, nombre, cuit, razon_social, condicion_iva)
VALUES ('6d2fd451-d36c-44c5-bae2-22a6d6b7d094', 'Supermercado Ideal', '27166080091', 'Caffaro Miriam Leticia', 1);

-- Insertar empresa La Nueva Estrella
INSERT INTO empresas (uuid, nombre, cuit, razon_social, condicion_iva)
VALUES ('da7fd6f4-1d81-4f33-b848-b7be5bca4752', 'La Nueva Estrella', '20252155857', 'Dube Ariel Alejandro', 1);

-- Insertar usuario facu (password: admin123)
INSERT INTO usuarios (nombre, password, empresa_id, eliminado)
VALUES ('gabriel', 'kEAQfs/kL1UxPIYMw71FhBA+wfSmKsVoZ+mKiENrmmU=', 1, 0);

-- Insertar usuario facu (password: admin123)
--INSERT INTO usuarios (nombre, password, empresa_id, eliminado)
--VALUES ('facu', 'tQNqdql5ImmbZBsw+hTFsxlKQF6xiMSZ0sBYN4Pxy5Y=', 2, false);
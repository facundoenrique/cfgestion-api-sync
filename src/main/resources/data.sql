-- Insertar empresa Super Ideal
INSERT INTO empresas (uuid, nombre, cuit, razon_social)
VALUES ('6d2fd451-d36c-44c5-bae2-22a6d6b7d094', 'Supermercado Ideal', '27166080091', 'Caffaro Miriam Leticia');

-- Insertar usuario facu (password: admin123)
INSERT INTO usuarios (nombre, password, empresa, eliminado)
VALUES ('facu', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 0, false);

-- Insertar empresa La Nueva Estrella
INSERT INTO empresas (uuid, nombre, cuit, razon_social)
VALUES ('da7fd6f4-1d81-4f33-b848-b7be5bca4752', 'La Nueva Estrella', '20252155857', 'Dube Ariel Alejandro');

-- Insertar usuario facu (password: admin123)
INSERT INTO usuarios (nombre, password, empresa, eliminado)
VALUES ('facu', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW', 1, false);
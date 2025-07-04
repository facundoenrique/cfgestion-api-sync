-- Eliminar las foreign keys existentes
ALTER TABLE items_lista_precios
    DROP FOREIGN KEY FKjg2ay4uquouks7qp20uos3ri4;

ALTER TABLE precios
    DROP FOREIGN KEY FKdn0lgdxrv8qjcgljm7ltdt9ew;

-- Agregar las nuevas foreign keys apuntando a red_articulos
ALTER TABLE items_lista_precios
    ADD CONSTRAINT FK_items_lista_precios_red_articulo
    FOREIGN KEY (articulo_id) REFERENCES red_articulos(id);

ALTER TABLE precios
    ADD CONSTRAINT FK_precios_red_articulo
    FOREIGN KEY (articulo_id) REFERENCES red_articulos(id); 
--
-- Estructura de tabla para la tabla `red_articulos`
--

CREATE TABLE `red_articulos` (
  `cantidad` double NOT NULL,
  `cod_unidad_medida` int(11) NOT NULL,
  `comision` int(11) NOT NULL,
  `compuesto` int(11) NOT NULL,
  `defecto` int(11) NOT NULL,
  `descuento` double NOT NULL,Add commentMore actions
  `eliminado` int(11) NOT NULL,
  `enviado` int(11) NOT NULL,
  `familia` int(11) NOT NULL,
  `gan1` double NOT NULL,
  `imagen` int(11) NOT NULL,
  `iva` decimal(38,2) NOT NULL,
  `maximo` int(11) NOT NULL,
  `minimo` int(11) NOT NULL,
  `moneda` int(11) NOT NULL,
  `no_stock` int(11) NOT NULL,
  `redondeo` int(11) NOT NULL,
  `subfamilia` int(11) NOT NULL,
  `tipo` int(11) NOT NULL,
  `fecha_creado` datetime(6) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `codigo` bigint(20),
  `empresa` bigint(20),
  `descripcion` varchar(255) DEFAULT NULL,
  `marca` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `numero` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `articulos_seq`
--

CREATE TABLE `red_articulos_seq` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


--
-- Volcado de datos para la tabla `articulos_seq`
--

INSERT INTO `red_articulos_seq` (`next_val`) VALUES
(1);
Add comment
-- Estructura de tabla para la tabla `pedidos`
--

CREATE TABLE `pedidos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha_creacion` datetime NOT NULL,
  `fecha_confirmacion` datetime DEFAULT NULL,
  `preventa_id` bigint(20) NOT NULL,
  `usuario_id` bigint(20) NOT NULL,
  `estado_participacion` varchar(20) NOT NULL DEFAULT 'PENDIENTE',
  PRIMARY KEY (`id`),
  KEY `FK_pedidos_preventa` (`preventa_id`),
  KEY `FK_pedidos_usuario` (`usuario_id`),
  CONSTRAINT `FK_pedidos_preventa` FOREIGN KEY (`preventa_id`) REFERENCES `preventas` (`id`),
  CONSTRAINT `FK_pedidos_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedido_items`
--

CREATE TABLE `pedido_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` double NOT NULL,
  `subtotal` double NOT NULL,
  `pedido_id` bigint(20) NOT NULL,
  `preventa_articulo_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pedido_items_pedido` (`pedido_id`),
  KEY `FK_pedido_items_preventa_articulo` (`preventa_articulo_id`),
  CONSTRAINT `FK_pedido_items_pedido` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`),
  CONSTRAINT `FK_pedido_items_preventa_articulo` FOREIGN KEY (`preventa_articulo_id`) REFERENCES `preventas_articulos` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Estructura de tabla para la tabla `pedidos_proveedor`
--

CREATE TABLE `pedidos_proveedor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha_creacion` datetime NOT NULL,
  `fecha_envio` datetime DEFAULT NULL,
  `estado` varchar(20) NOT NULL,
  `preventa_id` bigint(20) NOT NULL,
  `proveedor_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pedidos_proveedor_preventa` (`preventa_id`),
  KEY `FK_pedidos_proveedor_proveedor` (`proveedor_id`),
  CONSTRAINT `FK_pedidos_proveedor_preventa` FOREIGN KEY (`preventa_id`) REFERENCES `preventas` (`id`),
  CONSTRAINT `FK_pedidos_proveedor_proveedor` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedores` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos_proveedor_items`
--

CREATE TABLE `pedidos_proveedor_items` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` double NOT NULL,
  `subtotal` double NOT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `articulo_id` bigint(20) NOT NULL,
  `pedido_proveedor_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pedidos_proveedor_items_articulo` (`articulo_id`),
  KEY `FK_pedidos_proveedor_items_pedido` (`pedido_proveedor_id`),
  CONSTRAINT `FK_pedidos_proveedor_items_articulo` FOREIGN KEY (`articulo_id`) REFERENCES `articulos` (`id`),
  CONSTRAINT `FK_pedidos_proveedor_items_pedido` FOREIGN KEY (`pedido_proveedor_id`) REFERENCES `pedidos_proveedor` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

ALTER TABLE `items_lista_precios`
  ADD CONSTRAINT `FK4ivx35mi6lnmeihbhkkk1l97k` FOREIGN KEY (`lista_de_precios_id`) REFERENCES `listas_precios` (`id`),
  ADD CONSTRAINT `FKc3bmm245rbggaxu4mx8yt10rq` FOREIGN KEY (`precio_id`) REFERENCES `precios` (`id`),
  ADD CONSTRAINT `FKjg2ay4uquouks7qp20uos3ri4` FOREIGN KEY (`articulo_id`) REFERENCES `articulos` (`id`);
  ADD CONSTRAINT `FKjg2ay4uquouks7qp20uos3ri4` FOREIGN KEY (`articulo_id`) REFERENCES `red_articulos` (`id`);

--
-- Filtros para la tabla `listas_precios`Add commentMore actions
@@ -883,7 +934,7 @@ ALTER TABLE `localidades`
-- Filtros para la tabla `precios`
--
ALTER TABLE `precios`
  ADD CONSTRAINT `FKdn0lgdxrv8qjcgljm7ltdt9ew` FOREIGN KEY (`articulo_id`) REFERENCES `articulos` (`id`);
  ADD CONSTRAINT `FKdn0lgdxrv8qjcgljm7ltdt9ew` FOREIGN KEY (`articulo_id`) REFERENCES `red_articulos` (`id`);



ALTER TABLE preventas ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTA';
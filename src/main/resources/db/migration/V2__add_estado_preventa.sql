
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

ALTER TABLE preventas ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'ABIERTA';
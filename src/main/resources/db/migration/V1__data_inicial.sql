-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 26-05-2025 a las 02:22:02
-- Versión del servidor: 10.4.27-MariaDB
-- Versión de PHP: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `datos-red`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `articulos`
--

CREATE TABLE `articulos` (
  `cantidad` double NOT NULL,
  `cod_unidad_medida` int(11) NOT NULL,
  `comision` int(11) NOT NULL,
  `compuesto` int(11) NOT NULL,
  `defecto` int(11) NOT NULL,
  `descuento` double NOT NULL,
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
  `codigo` bigint(20) NOT NULL,
  `empresa` bigint(20) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `marca` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `numero` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `articulos_seq`
--

CREATE TABLE `articulos_seq` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


--
-- Volcado de datos para la tabla `articulos_seq`
--

INSERT INTO `articulos_seq` (`next_val`) VALUES
(1);

--
-- Estructura de tabla para la tabla `red_articulos`
--

CREATE TABLE `red_articulos` (
  `cantidad` double NOT NULL,
  `cod_unidad_medida` int(11) NOT NULL,
  `comision` int(11) NOT NULL,
  `compuesto` int(11) NOT NULL,
  `defecto` int(11) NOT NULL,
  `descuento` double NOT NULL,
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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `authentication`
--

CREATE TABLE `authentication` (
  `punto_venta` int(11) NOT NULL,
  `id` bigint(20) NOT NULL,
  `cuit` varchar(20) NOT NULL,
  `expiration_time` varchar(255) NOT NULL,
  `sign` text NOT NULL,
  `token` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `authentication_seq`
--

CREATE TABLE `authentication_seq` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


--
-- Volcado de datos para la tabla `authentication_seq`
--

INSERT INTO `authentication_seq` (`next_val`) VALUES
(1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `certificados`
--

CREATE TABLE `certificados` (
  `punto_venta` int(11) NOT NULL,
  `fecha_creado` datetime(6) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `cuit` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `archivo` blob NOT NULL,
  `origen` enum('GESTION','RED') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `certificados_seq`
--

CREATE TABLE `certificados_seq` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


--
-- Volcado de datos para la tabla `certificados_seq`
--

INSERT INTO `certificados_seq` (`next_val`) VALUES
(1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `clientes`
--

CREATE TABLE `clientes` (
  `campania` int(11) DEFAULT NULL,
  `categoria` int(11) DEFAULT NULL,
  `condicion_iva` smallint(6) NOT NULL,
  `cuenta_limite` int(11) DEFAULT NULL,
  `descuento` float DEFAULT NULL,
  `dni` int(11) DEFAULT NULL,
  `eliminado` smallint(6) DEFAULT NULL,
  `empleado` int(11) DEFAULT NULL,
  `enviado` smallint(6) DEFAULT NULL,
  `forma_pago` smallint(6) DEFAULT NULL,
  `lista_precio` smallint(6) DEFAULT NULL,
  `localidad` int(11) DEFAULT NULL,
  `no_vender` smallint(6) DEFAULT NULL,
  `pais` smallint(6) DEFAULT NULL,
  `provincia` smallint(6) DEFAULT NULL,
  `saldo_cuenta` double DEFAULT NULL,
  `sucursal` int(11) DEFAULT NULL,
  `sueldo` double DEFAULT NULL,
  `tipo_dni` smallint(6) DEFAULT NULL,
  `transporte` int(11) DEFAULT NULL,
  `verificado` smallint(6) DEFAULT NULL,
  `zona` int(11) DEFAULT NULL,
  `empresa` bigint(20) NOT NULL,
  `fecha_ingreso` datetime(6) DEFAULT NULL,
  `fecha_nacimiento` datetime(6) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `celular` varchar(255) DEFAULT NULL,
  `codigo_postal` varchar(255) DEFAULT NULL,
  `cuit` varchar(255) DEFAULT NULL,
  `domicilio` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `empleador` varchar(255) DEFAULT NULL,
  `empleador_cuit` varchar(255) DEFAULT NULL,
  `empleador_domicilio` varchar(255) DEFAULT NULL,
  `fax` varchar(255) DEFAULT NULL,
  `imagen` varchar(255) DEFAULT NULL,
  `ingresos_brutos` varchar(255) DEFAULT NULL,
  `otros` varchar(255) DEFAULT NULL,
  `razon_social` varchar(255) NOT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `telefono2` varchar(255) DEFAULT NULL,
  `web` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comprobantes`
--

CREATE TABLE `comprobantes` (
  `bonif_recargo` decimal(38,2) NOT NULL,
  `caja` int(11) NOT NULL,
  `cod_domicilio` int(11) NOT NULL,
  `cod_empleado` int(11) NOT NULL,
  `codigo` int(11) NOT NULL,
  `estado` int(11) NOT NULL,
  `importe_neto` decimal(38,2) NOT NULL,
  `importe_total` decimal(38,2) NOT NULL,
  `lista_precios` int(11) NOT NULL,
  `numero` int(11) NOT NULL,
  `punto_venta` int(11) NOT NULL,
  `redondeo` double NOT NULL,
  `tipo_comprobante` int(11) NOT NULL,
  `usuario` int(11) NOT NULL,
  `vuelto` double NOT NULL,
  `cae` bigint(20) NOT NULL,
  `cliente_id` bigint(20) NOT NULL,
  `empresa_id` bigint(20) NOT NULL,
  `fecha` datetime(6) NOT NULL,
  `fecha_comprobante` datetime(6) NOT NULL,
  `fecha_iva` datetime(6) NOT NULL,
  `fecha_vto` datetime(6) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `sucursal` bigint(20) NOT NULL,
  `comentario` varchar(255) DEFAULT NULL,
  `numero_trans` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comprobantes_detalles`
--

CREATE TABLE `comprobantes_detalles` (
  `cantidad` double NOT NULL,
  `compuesto` int(11) NOT NULL,
  `costo` decimal(38,2) DEFAULT NULL,
  `descuento` double NOT NULL,
  `importe_bruto` decimal(38,2) NOT NULL,
  `importe_neto` decimal(38,2) NOT NULL,
  `importe_neto_r` decimal(38,2) DEFAULT NULL,
  `impuesto_interno` double NOT NULL,
  `iva` double NOT NULL,
  `tasa_iva` float NOT NULL,
  `tipo` int(11) NOT NULL,
  `articulo` bigint(20) NOT NULL,
  `comprobante_id` bigint(20) NOT NULL,
  `id` bigint(20) NOT NULL,
  `detalle` varchar(255) DEFAULT NULL,
  `numero` varchar(255) NOT NULL,
  `serie` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comprobantes_forma_pago`
--

CREATE TABLE `comprobantes_forma_pago` (
  `codigo_forma_pago` int(11) NOT NULL,
  `importe` decimal(12,2) NOT NULL,
  `tipo` int(11) NOT NULL,
  `comprobante_id` bigint(20) NOT NULL,
  `id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `comprobantes_impuestos`
--

CREATE TABLE `comprobantes_impuestos` (
  `cod_impuesto` smallint(6) NOT NULL,
  `importe` double NOT NULL,
  `neto` double NOT NULL,
  `comprobante_id` bigint(20) NOT NULL,
  `id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empresas`
--

CREATE TABLE `empresas` (
  `condicion_iva` smallint(6) NOT NULL,
  `inicio_actividades` date DEFAULT NULL,
  `localidad` int(11) DEFAULT NULL,
  `provincia` int(11) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `codigo_postal` varchar(255) DEFAULT NULL,
  `cuit` varchar(255) NOT NULL,
  `domicilio` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `ingresos_brutos` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) NOT NULL,
  `razon_social` varchar(255) NOT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `web` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `impuestos`
--

CREATE TABLE `impuestos` (
  `alicuota` decimal(8,4) DEFAULT NULL,
  `codigo` smallint(6) NOT NULL,
  `importe_bruto` double DEFAULT NULL,
  `importe_neto` double DEFAULT NULL,
  `por_cliente` smallint(6) DEFAULT NULL,
  `provincia` int(11) DEFAULT NULL,
  `recibo` smallint(6) DEFAULT NULL,
  `tipo` smallint(6) DEFAULT NULL,
  `tipo_iva` smallint(6) DEFAULT NULL,
  `nombre` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `items_lista_precios`
--

CREATE TABLE `items_lista_precios` (
  `articulo_id` bigint(20) NOT NULL,
  `id` bigint(20) NOT NULL,
  `lista_de_precios_id` bigint(20) NOT NULL,
  `precio_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `listas_precios`
--

CREATE TABLE `listas_precios` (
  `fecha_creacion` date NOT NULL,
  `fecha_modificacion` date NOT NULL,
  `id` bigint(20) NOT NULL,
  `proveedor_id` bigint(20) NOT NULL,
  `nombre` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `localidades`
--

CREATE TABLE `localidades` (
  `id` int(11) NOT NULL,
  `provincia_id` int(11) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `paises`
--

CREATE TABLE `paises` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `precios`
--

CREATE TABLE `precios` (
  `fecha_vigencia` date NOT NULL,
  `importe` decimal(38,2) NOT NULL,
  `articulo_id` bigint(20) NOT NULL,
  `id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `preventas`
--

CREATE TABLE `preventas` (
  `fecha_creacion` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `fecha_inicio` date NOT NULL,
  `id` bigint(20) NOT NULL,
  `lista_base_id` bigint(20) DEFAULT NULL,
  `nombre` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `preventas_articulos`
--

CREATE TABLE `preventas_articulos` (
  `defecto` int(11) DEFAULT NULL,
  `importe` decimal(38,2) NOT NULL,
  `iva` decimal(38,2) DEFAULT NULL,
  `multiplicador` int(11) DEFAULT NULL,
  `unidades_por_vulto` int(11) DEFAULT NULL,
  `articulo_id` bigint(20) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `preventa_id` bigint(20) DEFAULT NULL,
  `nombre` varchar(255) NOT NULL,
  `numero` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `proveedores`
--

CREATE TABLE `proveedores` (
  `fecha_creado` date NOT NULL,
  `id` bigint(20) NOT NULL,
  `vendedor_id` bigint(20) DEFAULT NULL,
  `cuit` varchar(11) DEFAULT NULL,
  `codigo_postal` varchar(255) DEFAULT NULL,
  `condicion_iva` varchar(255) DEFAULT NULL,
  `domicilio` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `razon_social` varchar(255) NOT NULL,
  `telefono` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `provincias`
--

CREATE TABLE `provincias` (
  `codigo` int(11) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `red_clientes`
--

CREATE TABLE `red_clientes` (
  `campania` int(11) DEFAULT NULL,
  `categoria` int(11) DEFAULT NULL,
  `condicion_iva` smallint(6) NOT NULL,
  `cuenta_limite` int(11) DEFAULT NULL,
  `descuento` float DEFAULT NULL,
  `dni` int(11) DEFAULT NULL,
  `eliminado` smallint(6) DEFAULT NULL,
  `empleado` int(11) DEFAULT NULL,
  `empresa` smallint(6) DEFAULT NULL,
  `enviado` smallint(6) DEFAULT NULL,
  `forma_pago` smallint(6) DEFAULT NULL,
  `lista_precio` smallint(6) DEFAULT NULL,
  `localidad` int(11) DEFAULT NULL,
  `no_vender` smallint(6) DEFAULT NULL,
  `pais` smallint(6) DEFAULT NULL,
  `provincia` smallint(6) DEFAULT NULL,
  `saldo_cuenta` double DEFAULT NULL,
  `sucursal` int(11) DEFAULT NULL,
  `sueldo` double DEFAULT NULL,
  `tipo_dni` smallint(6) DEFAULT NULL,
  `transporte` int(11) DEFAULT NULL,
  `verificado` smallint(6) DEFAULT NULL,
  `zona` int(11) DEFAULT NULL,
  `fecha_ingreso` datetime(6) DEFAULT NULL,
  `fecha_nacimiento` datetime(6) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  `celular` varchar(255) DEFAULT NULL,
  `codigo_postal` varchar(255) DEFAULT NULL,
  `cuit` varchar(255) DEFAULT NULL,
  `domicilio` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `empleador` varchar(255) DEFAULT NULL,
  `empleador_cuit` varchar(255) DEFAULT NULL,
  `empleador_domicilio` varchar(255) DEFAULT NULL,
  `fax` varchar(255) DEFAULT NULL,
  `imagen` varchar(255) DEFAULT NULL,
  `ingresos_brutos` varchar(255) DEFAULT NULL,
  `otros` varchar(255) DEFAULT NULL,
  `razon_social` varchar(255) NOT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `telefono2` varchar(255) DEFAULT NULL,
  `web` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `codigo` int(11) DEFAULT NULL,
  `eliminado` smallint(6) NOT NULL,
  `empresa_id` bigint(20) NOT NULL,
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vendedores`
--

CREATE TABLE `vendedores` (
  `id` bigint(20) NOT NULL,
  `apellido` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `articulos`
--
ALTER TABLE `articulos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK185jdebvyn4rf2j1m62eic9e1` (`numero`);


--
-- Indices de la tabla `authentication`
--
ALTER TABLE `authentication`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `certificados`
--
ALTER TABLE `certificados`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `clientes`
--
ALTER TABLE `clientes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKq9do5cs8qivs9xkansiqt0h4d` (`empresa`),
  ADD UNIQUE KEY `UKgyd3a0j31wia5uooyjtn1tx2n` (`cuit`),
  ADD UNIQUE KEY `UK1c96wv36rk2hwui7qhjks3mvg` (`email`);

--
-- Indices de la tabla `comprobantes`
--
ALTER TABLE `comprobantes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK91bhhp01vm5iuarqgouaxyq3r` (`cliente_id`),
  ADD KEY `FK4wnjgs3jmm1hrpok0545yydrk` (`empresa_id`);

--
-- Indices de la tabla `comprobantes_detalles`
--
ALTER TABLE `comprobantes_detalles`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKlr53d985dm3fxqkwj7cmi000d` (`articulo`),
  ADD KEY `FK8mwdw2adcugh81j7i21poherm` (`comprobante_id`);

--
-- Indices de la tabla `comprobantes_forma_pago`
--
ALTER TABLE `comprobantes_forma_pago`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKoi35qjgtnjnasvliw2q2xvyg8` (`comprobante_id`);

--
-- Indices de la tabla `comprobantes_impuestos`
--
ALTER TABLE `comprobantes_impuestos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKfejqeajt93j2maqrhwn9awajc` (`comprobante_id`),
  ADD KEY `FK11cmvcon8w6t1gc56gpnwrrfu` (`cod_impuesto`);

--
-- Indices de la tabla `empresas`
--
ALTER TABLE `empresas`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKqu43i6uhv578s3laic80pdy6p` (`uuid`);

--
-- Indices de la tabla `impuestos`
--
ALTER TABLE `impuestos`
  ADD PRIMARY KEY (`codigo`);

--
-- Indices de la tabla `items_lista_precios`
--
ALTER TABLE `items_lista_precios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKfkw8xw6ky14tf2u6mtsm2qew9` (`precio_id`),
  ADD KEY `FKjg2ay4uquouks7qp20uos3ri4` (`articulo_id`),
  ADD KEY `FK4ivx35mi6lnmeihbhkkk1l97k` (`lista_de_precios_id`);

--
-- Indices de la tabla `listas_precios`
--
ALTER TABLE `listas_precios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKtdfrir76k56hufyfpgtpf3g9l` (`proveedor_id`);

--
-- Indices de la tabla `localidades`
--
ALTER TABLE `localidades`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKpsu514uguuo384j979ub9dp4y` (`provincia_id`);

--
-- Indices de la tabla `paises`
--
ALTER TABLE `paises`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `precios`
--
ALTER TABLE `precios`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKdn0lgdxrv8qjcgljm7ltdt9ew` (`articulo_id`);

--
-- Indices de la tabla `preventas`
--
ALTER TABLE `preventas`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `preventas_articulos`
--
ALTER TABLE `preventas_articulos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKikfcbqlv2162t55e3u8d71yor` (`preventa_id`);

--
-- Indices de la tabla `proveedores`
--
ALTER TABLE `proveedores`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKercly2jrt4i29dmat88guxoxw` (`cuit`),
  ADD KEY `FK8lqk2rqju07c8x5sb6vu1jucv` (`vendedor_id`);

--
-- Indices de la tabla `provincias`
--
ALTER TABLE `provincias`
  ADD PRIMARY KEY (`codigo`);

--
-- Indices de la tabla `red_clientes`
--
ALTER TABLE `red_clientes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKi5kysmktdgbnm17jrr3ks87kb` (`cuit`),
  ADD UNIQUE KEY `UK8wcg3mjfphcxludd9t3mk45d3` (`email`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKfmc72ayvo3m68236a265u7sra` (`empresa_id`,`nombre`);

--
-- Indices de la tabla `vendedores`
--
ALTER TABLE `vendedores`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `clientes`
--
ALTER TABLE `clientes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `comprobantes`
--
ALTER TABLE `comprobantes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `comprobantes_detalles`
--
ALTER TABLE `comprobantes_detalles`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `comprobantes_forma_pago`
--
ALTER TABLE `comprobantes_forma_pago`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `comprobantes_impuestos`
--
ALTER TABLE `comprobantes_impuestos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `empresas`
--
ALTER TABLE `empresas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `items_lista_precios`
--
ALTER TABLE `items_lista_precios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `listas_precios`
--
ALTER TABLE `listas_precios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `localidades`
--
ALTER TABLE `localidades`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `paises`
--
ALTER TABLE `paises`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `precios`
--
ALTER TABLE `precios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `preventas`
--
ALTER TABLE `preventas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `preventas_articulos`
--
ALTER TABLE `preventas_articulos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `proveedores`
--
ALTER TABLE `proveedores`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `provincias`
--
ALTER TABLE `provincias`
  MODIFY `codigo` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `red_clientes`
--
ALTER TABLE `red_clientes`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `clientes`
--
ALTER TABLE `clientes`
  ADD CONSTRAINT `FK6c8icu44kk8uaiixpd5t0ga0k` FOREIGN KEY (`empresa`) REFERENCES `empresas` (`id`);

--
-- Filtros para la tabla `comprobantes`
--
ALTER TABLE `comprobantes`
  ADD CONSTRAINT `FK4wnjgs3jmm1hrpok0545yydrk` FOREIGN KEY (`empresa_id`) REFERENCES `empresas` (`id`),
  ADD CONSTRAINT `FK91bhhp01vm5iuarqgouaxyq3r` FOREIGN KEY (`cliente_id`) REFERENCES `clientes` (`id`);

--
-- Filtros para la tabla `comprobantes_detalles`
--
ALTER TABLE `comprobantes_detalles`
  ADD CONSTRAINT `FK8mwdw2adcugh81j7i21poherm` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes` (`id`),
  ADD CONSTRAINT `FKlr53d985dm3fxqkwj7cmi000d` FOREIGN KEY (`articulo`) REFERENCES `articulos` (`id`);

--
-- Filtros para la tabla `comprobantes_forma_pago`
--
ALTER TABLE `comprobantes_forma_pago`
  ADD CONSTRAINT `FKoi35qjgtnjnasvliw2q2xvyg8` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes` (`id`);

--
-- Filtros para la tabla `comprobantes_impuestos`
--
ALTER TABLE `comprobantes_impuestos`
  ADD CONSTRAINT `FK11cmvcon8w6t1gc56gpnwrrfu` FOREIGN KEY (`cod_impuesto`) REFERENCES `impuestos` (`codigo`),
  ADD CONSTRAINT `FKfejqeajt93j2maqrhwn9awajc` FOREIGN KEY (`comprobante_id`) REFERENCES `comprobantes` (`id`);

--
-- Filtros para la tabla `listas_precios`
--
ALTER TABLE `listas_precios`
  ADD CONSTRAINT `FKtdfrir76k56hufyfpgtpf3g9l` FOREIGN KEY (`proveedor_id`) REFERENCES `proveedores` (`id`);

--
-- Filtros para la tabla `localidades`
--
ALTER TABLE `localidades`
  ADD CONSTRAINT `FKpsu514uguuo384j979ub9dp4y` FOREIGN KEY (`provincia_id`) REFERENCES `provincias` (`codigo`);

--
-- Filtros para la tabla `preventas_articulos`
--
ALTER TABLE `preventas_articulos`
  ADD CONSTRAINT `FKikfcbqlv2162t55e3u8d71yor` FOREIGN KEY (`preventa_id`) REFERENCES `preventas` (`id`);

--
-- Filtros para la tabla `proveedores`
--
ALTER TABLE `proveedores`
  ADD CONSTRAINT `FK8lqk2rqju07c8x5sb6vu1jucv` FOREIGN KEY (`vendedor_id`) REFERENCES `vendedores` (`id`);


ALTER TABLE comprobantes ADD CONSTRAINT unique_numero_empresa UNIQUE (numero, empresa_id);
ALTER TABLE comprobantes ADD CONSTRAINT unique_codigo_empresa UNIQUE (codigo, empresa_id);


--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `FK9v93lqnass5yqhhsyprr9fdv2` FOREIGN KEY (`empresa_id`) REFERENCES `empresas` (`id`);

-- Insertar empresa Super Ideal
INSERT INTO empresas (uuid, nombre, cuit, razon_social, condicion_iva)
VALUES ('6d2fd451-d36c-44c5-bae2-22a6d6b7d094', 'Supermercado Ideal', '27166080091', 'Caffaro Miriam Leticia', 1);

-- Insertar empresa La Nueva Estrella
INSERT INTO empresas (uuid, nombre, cuit, razon_social, condicion_iva)
VALUES ('da7fd6f4-1d81-4f33-b848-b7be5bca4752', 'La Nueva Estrella', '20252155857', 'Dube Ariel Alejandro', 1);

-- Insertar usuario facu (password: admin123)
INSERT INTO usuarios (nombre, password, empresa_id, eliminado, codigo)
VALUES ('gabriel', 'kEAQfs/kL1UxPIYMw71FhBA+wfSmKsVoZ+mKiENrmmU=', 1, 0, 1);

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

COMMIT;


/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

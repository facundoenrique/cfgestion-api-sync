# Sistema de Reportes de Errores CAE

Este documento describe el sistema mejorado de reportes de errores CAE que envía notificaciones específicas por empresa y reportes generales.

## 🎯 **Objetivo**

El sistema ahora:
1. **Asocia errores a empresas específicas** para poder enviar notificaciones personalizadas
2. **Envía emails específicos** a cada empresa con sus errores correspondientes
3. **Envía un reporte general** con todos los errores al email de configuración
4. **Mantiene trazabilidad** de qué empresa generó cada error

## 🔧 **Componentes Modificados**

### 1. CaeErrorMemory
**Ubicación**: `src/main/java/org/api_sync/services/afip/CaeErrorMemory.java`

**Cambios**:
- Agregado `empresaId` y `empresaNombre` a la clase `ErrorInfo`
- Nuevos métodos para agrupar errores por empresa
- Métodos para limpiar errores por empresa específica

**Nuevos métodos**:
```java
// Agrupar errores por empresa
Map<Long, List<ErrorInfo>> getErrorsByEmpresa()

// Obtener errores de una empresa específica
List<ErrorInfo> getErrorsByEmpresa(Long empresaId)

// Limpiar errores de una empresa específica
void clearErrorsByEmpresa(Long empresaId)
```

### 2. AfipGenerarCaeService
**Ubicación**: `src/main/java/org/api_sync/services/afip/AfipGenerarCaeService.java`

**Cambios**:
- Ahora incluye información de la empresa al registrar errores
- Los errores se asocian automáticamente con la empresa que los generó

### 3. CaeErrorReportCron
**Ubicación**: `src/main/java/org/api_sync/services/afip/CaeErrorReportCron.java`

**Funcionalidad**:
- **Reportes específicos por empresa**: Envía emails personalizados a cada empresa
- **Reporte general**: Envía un resumen de todos los errores al email de configuración
- **Limpieza automática**: Limpia todos los errores después de enviar los reportes

## 📧 **Tipos de Reportes**

### 1. Reporte Específico por Empresa
**Destinatarios**: Emails configurados para `ERROR_CAE` en cada empresa
**Contenido**:
- Solo errores de esa empresa específica
- Información detallada de cada error
- Mensaje personalizado para la empresa

**Ejemplo**:
```
Asunto: Errores CAE - Empresa ABC S.A.

Se han detectado los siguientes errores en su empresa:
• Punto de Venta: 1 | Tipo: Factura A | Error: [1001] Error de validación
• Punto de Venta: 2 | Tipo: Factura B | Error: [2001] Error de conexión
```

### 2. Reporte General
**Destinatarios**: Email de configuración (`cfgestion.mail.main-mail`)
**Contenido**:
- Resumen de todos los errores de todas las empresas
- Agrupado por empresa para mejor organización
- Estadísticas generales

**Ejemplo**:
```
Asunto: Reporte General de Errores CAE - 5 errores

Se han detectado 5 errores en total:

Empresa ABC S.A. (2 errores)
• Punto de Venta: 1 | Tipo: Factura A | Error: [1001] Error de validación
• Punto de Venta: 2 | Tipo: Factura B | Error: [2001] Error de conexión

Empresa XYZ Ltda. (3 errores)
• Punto de Venta: 1 | Tipo: Factura A | Error: [1001] Error de validación
• Punto de Venta: 3 | Tipo: Nota de Crédito A | Error: [3001] Error de formato
• Punto de Venta: 4 | Tipo: Factura C | Error: [4001] Error de certificado
```

## ⏰ **Programación**

El sistema se ejecuta automáticamente:
- **Horario**: 11:30 y 19:30 hora de Argentina
- **Frecuencia**: Diario
- **Zona horaria**: `America/Argentina/Buenos_Aires` (maneja automáticamente horario de verano)

## 🔄 **Flujo de Proceso**

1. **Generación de errores**: Los errores se registran con información de la empresa
2. **Agrupación**: Los errores se agrupan por empresa
3. **Envío específico**: Se envían emails específicos a cada empresa
4. **Envío general**: Se envía reporte general al email de configuración
5. **Limpieza**: Se limpian todos los errores después del envío

## 📊 **Configuración**

### Email de Configuración
```properties
cfgestion.mail.main-mail=admin@empresa.com
```

### Alertas por Empresa
El sistema de alertas por email se configura automáticamente a través de la entidad `EmpresaEmailAlerta`. No se requieren endpoints manuales ya que el cron job lee directamente la configuración de la base de datos.

Para configurar alertas, insertar directamente en la tabla `empresa_email_alerta`:
```sql
INSERT INTO empresa_email_alerta (empresa_id, email, nombre_contacto, tipo_alerta, activo, descripcion)
VALUES (1, 'admin@empresa.com', 'Administrador', 'ERROR_CAE', true, 'Alertas para errores de CAE');
```

## 🧪 **Testing**

Los tests verifican:
- Envío correcto de reportes específicos por empresa
- Envío correcto del reporte general
- Manejo de errores en el envío de emails
- Limpieza correcta de errores después del reporte

## 📈 **Beneficios**

1. **Notificaciones personalizadas**: Cada empresa recibe solo sus errores
2. **Visibilidad completa**: El administrador recibe todos los errores
3. **Trazabilidad**: Se puede rastrear qué empresa generó cada error
4. **Escalabilidad**: Funciona con múltiples empresas
5. **Flexibilidad**: Cada empresa puede configurar múltiples emails de alerta

## 🔍 **Monitoreo**

Los logs incluyen:
- Cantidad de errores por empresa
- Emails enviados exitosamente
- Errores en el envío de emails
- Limpieza de errores

## 🚨 **Consideraciones**

- Los errores se limpian automáticamente después del reporte
- Si no hay errores, no se envían reportes
- Los errores de envío de email no interrumpen el proceso
- Cada empresa puede tener múltiples emails configurados para alertas 
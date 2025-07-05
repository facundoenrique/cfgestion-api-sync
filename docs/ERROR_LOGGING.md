# Sistema de Logging de Errores

Este documento describe el sistema implementado para capturar y loggear información detallada **SOLO** de las requests que fallan en la aplicación.

## 🎯 **Objetivo**

El sistema está diseñado para **NO loggear requests exitosas**, evitando spam en los logs y enfocándose únicamente en requests que terminan con errores (status >= 400) o excepciones.

## Componentes del Sistema

### 1. RestControllerExceptionHandler
**Ubicación**: `src/main/java/org/api_sync/adapter/inbound/RestControllerExceptionHandler.java`

Captura todas las excepciones que ocurren en los controladores REST y loggea información detallada de la request que causó el error.

**Información capturada**:
- Tipo de error
- Método HTTP
- URL completa
- URI de la request
- Query string
- Dirección IP del cliente
- User Agent
- Content Type
- Content Length
- Headers (excluyendo información sensible)

### 2. RequestLoggingFilter
**Ubicación**: `src/main/java/org/api_sync/config/RequestLoggingFilter.java`

Filtro que captura información **SOLO** de requests que fallan (status >= 400) o que lanzan excepciones.

**Información capturada**:
- Método HTTP
- URL completa
- URI
- Query string
- Dirección IP y puerto del cliente
- User Agent
- Content Type y Length
- Headers (con redacción de información sensible)
- **Body de la request** (para JSON y texto)
- Status de la response
- Información de excepciones

### 3. RequestErrorInterceptor
**Ubicación**: `src/main/java/org/api_sync/config/RequestErrorInterceptor.java`

Interceptor que captura **timing** de requests que fallan.

**Información capturada**:
- Tiempo de procesamiento de requests fallidas

## Configuración

### WebConfig
**Ubicación**: `src/main/java/org/api_sync/config/WebConfig.java`

Registra el `RequestErrorInterceptor` para que se ejecute en todas las rutas excepto `/error`.

## Información Sensible

El sistema automáticamente redacta (oculta) información sensible en los logs:

**Headers redactados**:
- `Authorization`
- `Cookie`
- `X-API-Key`
- `Password`
- `Token`

## Ejemplo de Log

Cuando ocurre un error, verás logs como este:

```
=== FAILED REQUEST DETAILS ===
Method: POST
URL: https://api.example.com/api/v1/users
URI: /api/v1/users
Query: N/A
Response Status: 500
Response Content Type: application/json
Remote: 192.168.1.100:54321
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36
Content-Type: application/json
Content-Length: 156
Headers:
  Accept: application/json
  Content-Type: application/json
  X-Forwarded-For: 203.0.113.1
Exception: RuntimeException
Exception Message: Error processing user data
=== END FAILED REQUEST ===

Request body for failed request: {"nombre":"Juan Pérez","email":"juan@example.com"}

Request processing time: 1250ms for failed request: POST /api/v1/users (Status: 500)
```

## Niveles de Log

- **ERROR**: Información detallada de requests fallidas
- **DEBUG**: Body de requests fallidas y timing de procesamiento
- **WARN**: Situaciones donde no se puede obtener información de la request

## Configuración de Logging

Para ajustar el nivel de logging, modifica `application.properties`:

```properties
# Logging para el sistema de requests (solo errores)
logging.level.org.api_sync.config.RequestLoggingFilter=ERROR
logging.level.org.api_sync.config.RequestErrorInterceptor=ERROR
logging.level.org.api_sync.adapter.inbound.RestControllerExceptionHandler=ERROR

# Para ver el body de requests fallidas
logging.level.org.api_sync.config.RequestLoggingFilter=DEBUG
```

## ¿Qué se loggea y qué no?

### ✅ **SÍ se loggea**:
- Requests con status >= 400 (4xx, 5xx)
- Requests que lanzan excepciones
- Body de requests fallidas (nivel DEBUG)
- Timing de requests fallidas
- Headers (excluyendo información sensible)

### ❌ **NO se loggea**:
- Requests exitosas (status 200, 201, 204, etc.)
- Requests de redirección (status 3xx)
- Body de requests exitosas

## Testing

Los tests se encuentran en:
- `src/test/java/org/api_sync/adapter/inbound/RestControllerExceptionHandlerTest.java`

## Beneficios

1. **Logs limpios**: Solo información relevante de errores
2. **Debugging mejorado**: Información completa de requests que fallan
3. **Auditoría**: Registro de requests problemáticas para análisis
4. **Monitoreo**: Métricas de timing y patrones de error
5. **Seguridad**: Redacción automática de información sensible
6. **Performance**: No impacto en requests exitosas

## Consideraciones

- Los logs solo contienen información de errores, manteniendo logs limpios
- El body se loggea solo para requests fallidas en nivel DEBUG
- En entornos de alta carga, el sistema no impacta el rendimiento de requests exitosas
- Considera configurar rotación de logs para mantener el tamaño bajo 
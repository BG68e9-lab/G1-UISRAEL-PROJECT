# Documento de Requisitos — Licorería Drink House

## Introducción

La Licorería Drink House requiere una aplicación web construida sobre Spring Boot 4.1 / Java 21
que permita gestionar el inventario de la tienda de licores con capacidades de inteligencia
artificial. El sistema debe soportar cuatro sprints de desarrollo (81 puntos de historia) cubriendo:
autenticación y control de tokens IA, identificación de productos vía fotografía (Claude Vision API),
registro de órdenes de compra con extracción IA, gestión de lotes con estrategia LIFO,
cálculo automático de precio de venta, registro de ventas con deducción LIFO, panel
administrativo con alertas de bajo stock, historial de movimientos con soporte RIMPE,
y catálogo público de productos.

La arquitectura sigue el patrón hexagonal con cuatro capas: `dominio`, `aplicacion`,
`infraestructura` y `presentacion`. Las entidades de dominio ya existentes son:
`Producto`, `Categoria`, `Proveedor`, `OrdenCompra`, `DetalleOrdenCompra`, `LoteProducto`,
`MovimientoInventario`, `TipoMovimiento`, `EstadoOc`, `EstadoRespaldo`, `Usuario`, `Rol`,
`CodigoAcceso`, `Negocio`, `LogAuditoria`, `SecuenciaCodigo`, `Alerta`, `ConsumoIaMensual`,
`IdentificacionIa`, `TokensIaNegocio`.

---

## Glosario

- **Sistema**: La aplicación web Licorería Drink House (Spring Boot 4.1 / Java 21).
- **Administrador**: Usuario con rol ADMIN que gestiona el negocio, activa cuentas y configura parámetros.
- **Vendedor**: Usuario con rol VENDEDOR que registra ventas y consulta inventario.
- **Visitante**: Usuario sin sesión activa que accede al catálogo público.
- **Producto**: Artículo de licorería identificado por nombre, marca, tipo y categoría.
- **Lote**: Unidad de ingreso de mercancía (entidad `LoteProducto`) con código ENT-XXXX, precio costo, cantidad y fecha de vencimiento.
- **Movimiento**: Registro de entrada o salida de inventario (entidad `MovimientoInventario`) con código único ENT-XXXX o SAL-XXXX.
- **OrdenCompra**: Documento de compra (entidad `OrdenCompra`) que respalda el ingreso de mercancía.
- **LIFO**: Estrategia de gestión de inventario donde el lote ingresado más recientemente se consume primero en ventas.
- **RIMPE**: Régimen ecuatoriano de tributación simplificado; exige log de auditoría por cada operación de inventario.
- **LogAuditoria**: Registro inmutable de cada operación de inventario (entidad `LogAuditoria`) con entidad, ID, acción, detalle y timestamp.
- **EstadoRespaldo**: Indicador de respaldo documental de un lote: `CON_OC` (✅), `PENDIENTE` (⚠️), o `SIN_RESPALDO`.
- **Claude_Vision_API**: API de Anthropic para identificación de productos mediante imágenes.
- **Servicio_IA**: Componente interno que encapsula llamadas a Claude_Vision_API.
- **TokensIaNegocio**: Configuración de tokens API de IA aislada por negocio (entidad `TokensIaNegocio`).
- **ConsumoIaMensual**: Registro mensual de tokens consumidos y costo estimado por negocio (entidad `ConsumoIaMensual`).
- **IdentificacionIa**: Registro del resultado de cada llamada al Servicio_IA (entidad `IdentificacionIa`).
- **CodigoAcceso**: Código de un solo uso, almacenado como hash, para activación de cuenta o recuperación de contraseña (entidad `CodigoAcceso`).
- **JWT**: JSON Web Token emitido por el Sistema tras autenticación exitosa.
- **SSO**: Inicio de sesión único mediante OAuth 2.0 con proveedores Google o Microsoft.
- **BCrypt**: Algoritmo de hashing de contraseñas utilizado por el Sistema.
- **Alerta**: Notificación interna del Sistema sobre bajo stock o vencimiento próximo (entidad `Alerta`).
- **SecuenciaCodigo**: Generador de secuencias numéricas para códigos ENT-XXXX y SAL-XXXX (entidad `SecuenciaCodigo`).
- **MargenGanancia**: Porcentaje de ganancia configurable por categoría, expresado como decimal (ej. 0.30 = 30 %).
- **PrecioVenta**: Precio de venta calculado como `costoPromedio × (1 + MargenGanancia)`.
- **PrecioPersonalizado**: Indicador booleano en `Producto` que señala que el precio fue establecido manualmente.
- **CostoPromedio**: Costo ponderado acumulado de un `Producto`, recalculado al vincular una `OrdenCompra` con precio diferente.
- **Catalogo_Publico**: Vista de productos sin autenticación requerida.
- **Dashboard**: Panel administrativo con resumen de inventario y alertas activas.

---

## Requisitos

---

### Requisito 1 — Activación de cuenta de administrador (DH-AUTH-0009)

**Historia de usuario:** Como administrador autorizado, quiero activar mi cuenta mediante un código único enviado a mi correo electrónico, para que el acceso inicial al Sistema quede protegido contra usos no autorizados.

#### Criterios de Aceptación

1. WHEN el Administrador envía un código de activación válido y no usado, THE Sistema SHALL activar la cuenta del Usuario, establecer `estadoCuenta = ACTIVO`, registrar `activadoEn` con la marca de tiempo actual y marcar el CodigoAcceso como `usado = true`.
2. WHEN el Administrador envía un código de activación que ya fue usado, THE Sistema SHALL rechazar la solicitud con el código de error `CODIGO_YA_USADO` y no modificar el estado de la cuenta.
3. WHEN el Administrador envía un código de activación cuya `expiraEn` es anterior a la fecha y hora actual, THE Sistema SHALL rechazar la solicitud con el código de error `CODIGO_EXPIRADO` y no modificar el estado de la cuenta.
4. WHEN el Administrador activa su cuenta exitosamente, THE Sistema SHALL emitir un JWT con el rol correspondiente y un tiempo de expiración de 8 horas.
5. THE Sistema SHALL almacenar cada CodigoAcceso de activación únicamente como hash BCrypt, sin exponer el valor original en ninguna respuesta ni log.
6. THE Sistema SHALL generar el código de activación con una longitud mínima de 32 caracteres alfanuméricos y enviarlo al correo electrónico autorizado del Administrador antes de que el Usuario intente activar la cuenta.
7. WHEN el Administrador intenta iniciar sesión con `estadoCuenta != ACTIVO`, THE Sistema SHALL rechazar la solicitud con el código de error `CUENTA_INACTIVA`.

---

### Requisito 2 — Autenticación con credenciales y SSO (DH-AUTH-0008)

**Historia de usuario:** Como usuario registrado, quiero iniciar sesión con mi correo y contraseña o mediante SSO (Google/Microsoft), para que pueda acceder al Sistema de forma segura y sin tener que recordar múltiples credenciales.

#### Criterios de Aceptación

1. WHEN un Usuario envía credenciales de correo y contraseña correctas con `estadoCuenta = ACTIVO`, THE Sistema SHALL validar la contraseña contra el hash BCrypt almacenado, emitir un JWT con los roles del Usuario y establecer expiración de 8 horas.
2. WHEN un Usuario envía credenciales con contraseña incorrecta, THE Sistema SHALL rechazar la solicitud con el código de error `CREDENCIALES_INVALIDAS` sin revelar si el correo existe en el Sistema.
3. WHEN un Usuario completa el flujo OAuth 2.0 con proveedor Google o Microsoft, THE Sistema SHALL crear o actualizar el registro de Usuario con `proveedorSso` y `ssoSubjectId` correspondientes, y emitir un JWT con los roles del Usuario.
4. WHEN un Usuario solicita recuperación de contraseña proporcionando su correo registrado, THE Sistema SHALL generar un CodigoAcceso de tipo `RESET_PASSWORD`, establecer `expiraEn` en 30 minutos desde la creación, almacenarlo como hash BCrypt, y enviar el enlace de restablecimiento al correo del Usuario.
5. WHEN un Usuario utiliza un enlace de restablecimiento de contraseña válido, no expirado y no usado, THE Sistema SHALL permitir el cambio de contraseña, almacenarla como nuevo hash BCrypt y marcar el CodigoAcceso como `usado = true`.
6. WHEN un Usuario intenta usar un enlace de restablecimiento ya utilizado o expirado, THE Sistema SHALL rechazar la solicitud con el código de error correspondiente (`CODIGO_YA_USADO` o `CODIGO_EXPIRADO`) y no modificar la contraseña.
7. THE Sistema SHALL rechazar contraseñas nuevas con menos de 8 caracteres durante el restablecimiento y retornar el código de error `CONTRASENA_INVALIDA`.

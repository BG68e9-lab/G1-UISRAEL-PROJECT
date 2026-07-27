# Requirements Document

## Introduction

DrinkHouse App es una API REST backend para la gestión integral de una licorería / tienda de bebidas (Drink House). El sistema cubre el ciclo de vida completo del negocio: catálogo de productos y categorías, proveedores, gestión de inventario por lotes, órdenes de compra, movimientos de inventario con trazabilidad, usuarios con roles y códigos de acceso, configuración multi-negocio, registro de auditoría, alertas automáticas e identificación de productos mediante inteligencia artificial.

El backend se implementa sobre Spring Boot 4.1.0 con Java 21, siguiendo Arquitectura Hexagonal (Clean Architecture) con las capas `dominio`, `aplicacion`, `infraestructura` y `presentacion`. La persistencia se realiza con PostgreSQL y Spring Data JPA. El mapeo entre capas utiliza MapStruct y la validación Jakarta Validation.

---

## Glossary

- **API**: Interfaz de programación de aplicaciones REST expuesta en la capa `presentacion`.
- **Sistema**: La API REST DrinkHouse App en su totalidad.
- **GestorProductos**: Componente de casos de uso responsable de las operaciones sobre `Producto` y `Categoria`.
- **GestorInventario**: Componente de casos de uso responsable de `LoteProducto` y `MovimientoInventario`.
- **GestorOrdenes**: Componente de casos de uso responsable de `OrdenCompra` y `DetalleOrdenCompra`.
- **GestorUsuarios**: Componente de casos de uso responsable de `Usuario`, `Rol` y `CodigoAcceso`.
- **GestorNegocio**: Componente de casos de uso responsable de `Negocio`.
- **GestorAlertas**: Componente de casos de uso responsable de `Alerta`.
- **GestorAuditoria**: Componente de casos de uso responsable de `LogAuditoria`.
- **GestorIa**: Componente de casos de uso responsable de `IdentificacionIa`, `ConsumoIaMensual` y `TokensIaNegocio`.
- **Producto**: Entidad de dominio que representa un artículo comercializable (bebida/licor) con atributos de precio, stock y origen de identificación.
- **Categoria**: Clasificación de productos con margen de ganancia configurable.
- **Proveedor**: Empresa o persona jurídica que abastece productos al negocio.
- **LoteProducto**: Unidad de entrada de mercadería con cantidad, costo y fecha de vencimiento.
- **MovimientoInventario**: Registro de entrada, salida o ajuste de stock con código único y trazabilidad.
- **TipoMovimiento**: Clasificador de movimientos de inventario (entrada, salida, ajuste) con prefijo de código.
- **OrdenCompra**: Documento de compra al proveedor con estado y total.
- **DetalleOrdenCompra**: Línea de detalle de una `OrdenCompra` con producto, cantidad y precio unitario.
- **EstadoOc**: Catálogo de estados posibles de una `OrdenCompra` (BORRADOR, ENVIADA, RECIBIDA, ANULADA).
- **Usuario**: Persona con acceso al sistema, identificada por UUID, con soporte SSO.
- **Rol**: Conjunto de permisos asignado a un `Usuario` (ADMIN, EMPLEADO, etc.).
- **CodigoAcceso**: Token de un solo uso para invitación o recuperación de contraseña, con expiración.
- **Negocio**: Configuración de la empresa/tenant (nombre, RUC, estado activo).
- **SecuenciaCodigo**: Generador de códigos secuenciales únicos para movimientos y órdenes.
- **Alerta**: Notificación interna del sistema (stock bajo, vencimiento próximo, etc.).
- **LogAuditoria**: Registro inmutable de acciones realizadas sobre entidades del sistema.
- **IdentificacionIa**: Resultado de un proceso de identificación de producto mediante IA.
- **ConsumoIaMensual**: Registro del consumo mensual de tokens de IA por negocio.
- **TokensIaNegocio**: Configuración del límite de tokens de IA asignados al negocio.
- **RUC**: Registro Único de Contribuyentes (identificador tributario ecuatoriano).
- **UUID**: Identificador único universal utilizado para `Usuario` y `CodigoAcceso`.
- **SSO**: Inicio de sesión único (Single Sign-On) mediante proveedor externo.
- **FIFO**: First-In First-Out, estrategia de consumo de lotes por fecha de ingreso.

---

## Requirements

### Requirement 1: Gestión de Categorías

**User Story:** Como administrador, quiero gestionar las categorías de productos, para que pueda clasificar las bebidas y asignarles un margen de ganancia predeterminado.

#### Acceptance Criteria

1. WHEN se recibe una solicitud de creación de categoría con nombre y margenGananciaPct válidos, THE GestorProductos SHALL persistir la nueva `Categoria` y retornar su representación con el categoriaId asignado.
2. WHEN se recibe una solicitud de actualización de categoría con un categoriaId existente, THE GestorProductos SHALL actualizar los campos nombre, margenGananciaPct y activo, y retornar la `Categoria` actualizada.
3. WHEN se recibe una solicitud de consulta por categoriaId, THE GestorProductos SHALL retornar la `Categoria` correspondiente.
4. WHEN se recibe una solicitud de listado de categorías, THE GestorProductos SHALL retornar la lista completa de categorías registradas.
5. WHEN se recibe una solicitud de eliminación de una categoría con categoriaId existente que no tenga productos asociados, THE GestorProductos SHALL eliminar la `Categoria` y retornar confirmación.
6. IF se recibe una solicitud de creación de categoría con nombre duplicado, THEN THE GestorProductos SHALL retornar un error con código HTTP 409 y mensaje descriptivo.
7. IF se recibe una solicitud de eliminación de una categoría que tenga productos asociados, THEN THE GestorProductos SHALL retornar un error con código HTTP 422 y mensaje indicando la restricción.
8. THE GestorProductos SHALL registrar en el `LogAuditoria` toda operación de creación, actualización y eliminación sobre `Categoria`.

---

### Requirement 2: Gestión de Productos

**User Story:** Como administrador, quiero gestionar el catálogo de productos, para que el inventario refleje fielmente los artículos disponibles en la tienda.

#### Acceptance Criteria

1. WHEN se recibe una solicitud de creación de producto con nombre, marca, tipo, costoPromedio y margenGanancia válidos, THE GestorProductos SHALL persistir el nuevo `Producto`, calcular precioVenta como `costoPromedio * (1 + margenGanancia / 100)` cuando precioPersonalizado sea false, y retornar la representación con productoId asignado.
2. WHEN precioPersonalizado es true en la solicitud de creación o actualización, THE GestorProductos SHALL almacenar el precioVenta provisto sin recalcularlo.
3. WHEN se recibe una solicitud de actualización de producto con productoId existente, THE GestorProductos SHALL actualizar los campos provistos y retornar el `Producto` actualizado.
4. WHEN se recibe una solicitud de consulta por productoId, THE GestorProductos SHALL retornar el `Producto` correspondiente con todos sus campos.
5. WHEN se recibe una solicitud de listado de productos, THE GestorProductos SHALL retornar la lista de todos los productos registrados.
6. WHEN se recibe una solicitud de búsqueda de productos con parámetros de filtro (nombre, marca, tipo, categoriaId), THE GestorProductos SHALL retornar únicamente los productos que coincidan con todos los filtros provistos.
7. WHEN se recibe una solicitud de eliminación de producto con productoId existente, THE GestorProductos SHALL eliminar el `Producto` y retornar confirmación.
8. IF se recibe una solicitud de creación de producto con nombre duplicado dentro del mismo negocio, THEN THE GestorProductos SHALL retornar un error con código HTTP 409.
9. IF se recibe una solicitud con costoPromedio menor o igual a cero, THEN THE GestorProductos SHALL retornar un error con código HTTP 400 y mensaje descriptivo.
10. THE GestorProductos SHALL registrar en el `LogAuditoria` toda operación de creación, actualización y eliminación sobre `Producto`.

---

### Requirement 3: Gestión de Proveedores

**User Story:** Como administrador, quiero gestionar los proveedores, para que las órdenes de compra estén asociadas a empresas debidamente registradas.

#### Acceptance Criteria

1. WHEN se recibe una solicitud de creación de proveedor con ruc, razonSocial y email válidos, THE GestorOrdenes SHALL persistir el nuevo `Proveedor` y retornar su representación con proveedorId asignado.
2. WHEN se recibe una solicitud de actualización de proveedor con proveedorId existente, THE GestorOrdenes SHALL actualizar los campos razonSocial, direccion, telefono y email, y retornar el `Proveedor` actualizado.
3. WHEN se recibe una solicitud de consulta por proveedorId, THE GestorOrdenes SHALL retornar el `Proveedor` correspondiente.
4. WHEN se recibe una solicitud de listado de proveedores, THE GestorOrdenes SHALL retornar la lista completa de proveedores registrados.
5. IF se recibe una solicitud de creación de proveedor con RUC duplicado, THEN THE GestorOrdenes SHALL retornar un error con código HTTP 409 y mensaje descriptivo.
6. IF se recibe una solicitud de creación de proveedor con formato de RUC inválido (distinto de 13 dígitos numéricos), THEN THE GestorOrdenes SHALL retornar un error con código HTTP 400 y mensaje descriptivo.
7. THE GestorOrdenes SHALL registrar en el `LogAuditoria` toda operación de creación y actualización sobre `Proveedor`.

---

### Requirement 4: Gestión de Órdenes de Compra

**User Story:** Como administrador, quiero gestionar las órdenes de compra a proveedores, para que el flujo de abastecimiento quede documentado y trazado.

#### Acceptance Criteria

1. WHEN se recibe una solicitud de creación de orden de compra con proveedorId válido y al menos un `DetalleOrdenCompra`, THE GestorOrdenes SHALL persistir la `OrdenCompra` con estado BORRADOR, calcular el total como la suma de (cantidad × precioUnitario) de todos los detalles, generar un codigoReferencia único usando `SecuenciaCodigo`, y retornar la representación completa con ordenCompraId asignado.
2. WHEN se recibe una solicitud de actualización de una orden en estado BORRADOR, THE GestorOrdenes SHALL permitir modificar los detalles y recalcular el total.
3. WHEN se recibe una solicitud de envío de orden con ordenCompraId en estado BORRADOR, THE GestorOrdenes SHALL transicionar el estado a ENVIADA.
4. WHEN se recibe una solicitud de recepción de orden con ordenCompraId en estado ENVIADA, THE GestorOrdenes SHALL transicionar el estado a RECIBIDA, crear un `LoteProducto` por cada `DetalleOrdenCompra` con la cantidad y precio de costo correspondientes, e incrementar el stockActual de cada `Producto` involucrado en la cantidad recibida.
5. WHEN se recibe una solicitud de anulación de orden con ordenCompraId en estado BORRADOR o ENVIADA, THE GestorOrdenes SHALL transicionar el estado a ANULADA.
6. WHEN se recibe una solicitud de consulta por ordenCompraId, THE GestorOrdenes SHALL retornar la `OrdenCompra` con todos sus `DetalleOrdenCompra`.
7. WHEN se recibe una solicitud de listado de órdenes con filtros opcionales de estado y rango de fechas, THE GestorOrdenes SHALL retornar únicamente las órdenes que coincidan con los filtros provistos.
8. IF se recibe una solicitud de modificación de una orden que no esté en estado BORRADOR, THEN THE GestorOrdenes SHALL retornar un error con código HTTP 422 y mensaje indicando el estado actual.
9. IF se recibe una solicitud de recepción de una orden que no esté en estado ENVIADA, THEN THE GestorOrdenes SHALL retornar un error con código HTTP 422.
10. IF se recibe una solicitud de creación de orden con un productoId inexistente en algún detalle, THEN THE GestorOrdenes SHALL retornar un error con código HTTP 404.
11. THE GestorOrdenes SHALL registrar en el `LogAuditoria` cada cambio de estado de `OrdenCompra`.

---

### Requirement 5: Gestión de Lotes de Producto

**User Story:** Como administrador, quiero registrar y consultar los lotes de cada producto, para que la trazabilidad de la mercadería ingresada sea completa y verificable.

#### Acceptance Criteria

1. WHEN se recibe una solicitud de creación de lote con productoId válido, cantidadInicial mayor a cero y precioCosto mayor a cero, THE GestorInventario SHALL persistir el `LoteProducto` con cantidadDisponible igual a cantidadInicial, asignar un codigoEntrada único usando `SecuenciaCodigo`, registrar la fechaIngreso con la fecha-hora actual, y retornar la representación con loteId asignado.
2. WHEN se recibe una solicitud de consulta de lotes por productoId, THE GestorInventario SHALL retornar todos los lotes del producto ordenados por fechaIngreso ascendente (FIFO).
3. WHEN se recibe una solicitud de consulta por loteId, THE GestorInventario SHALL retornar el `LoteProducto` correspondiente.
4. WHEN se recibe una solicitud de listado de lotes próximos a vencer en los siguientes N días, THE GestorInventario SHALL retornar todos los lotes cuya fechaVencimiento sea menor o igual a la fecha actual más N días y cuya cantidadDisponible sea mayor a cero.
5. IF se recibe una solicitud de creación de lote con cantidadInicial menor o igual a cero, THEN THE GestorInventario SHALL retornar un error con código HTTP 400.
6. IF se recibe una solicitud de creación de lote con productoId inexistente, THEN THE GestorInventario SHALL retornar un error con código HTTP 404.

---

### Requirement 6: Movimientos de Inventario

**User Story:** Como empleado, quiero registrar los movimientos de inventario (entradas, salidas y ajustes), para que el stock siempre refleje el estado real del almacén.

#### Acceptance Criteria

1. WHEN se recibe una solicitud de registro de movimiento de tipo ENTRADA con productoId, loteId, cantidad y precioUnitario válidos, THE GestorInventario SHALL persistir el `MovimientoInventario`, generar un codigoMovimiento único usando el prefijoCodigo del `TipoMovimiento` concatenado con el número de `SecuenciaCodigo`, e incrementar el stockActual del `Producto` en la cantidad indicada.
2. WHEN se recibe una solicitud de registro de movimiento de tipo SALIDA con productoId, loteId y cantidad válidos, THE GestorInventario SHALL persistir el `MovimientoInventario`, decrementar la cantidadDisponible del `LoteProducto` en la cantidad indicada, y decrementar el stockActual del `Producto` en la misma cantidad.
3. WHEN se recibe una solicitud de registro de movimiento de tipo AJUSTE con productoId y cantidad (positiva o negativa) válidos, THE GestorInventario SHALL persistir el `MovimientoInventario` y actualizar el stockActual del `Producto` sumando la cantidad indicada (puede ser negativa para disminución).
4. WHEN se recibe una solicitud de consulta de movimientos por productoId con filtros opcionales de tipo y rango de fechas, THE GestorInventario SHALL retornar los movimientos que coincidan con los filtros provistos, ordenados por fecha descendente.
5. WHILE el stockActual de un `Producto` sea menor o igual a su stockMinimo, THE GestorAlertas SHALL generar una `Alerta` de tipo STOCK_BAJO con mensaje que indique el nombre del producto y el stock actual.
6. IF se recibe una solicitud de registro de movimiento de tipo SALIDA con cantidad mayor a la cantidadDisponible del lote indicado, THEN THE GestorInventario SHALL retornar un error con código HTTP 422 y mensaje indicando la disponibilidad actual.
7. IF se recibe una solicitud de movimiento con productoId o loteId inexistente, THEN THE GestorInventario SHALL retornar un error con código HTTP 404.
8. THE GestorInventario SHALL registrar en el `LogAuditoria` cada `MovimientoInventario` creado, incluyendo el tipo, cantidad y producto afectado.

---

### Requirement 7: Tipos de Movimiento

**User Story:** Como administrador, quiero gestionar los tipos de movimiento de inventario, para que los movimientos queden clasificados con un código y prefijo estandarizado.

#### Acceptance Criteria

1. WHEN se recibe una solicitud de creación de tipo de movimiento con codigo, prefijoCodigo y descripcion válidos, THE GestorInventario SHALL persistir el nuevo `TipoMovimiento` y retornar su representación con tipoMovimientoId asignado.
2. WHEN se recibe una solicitud de listado de tipos de movimiento, THE GestorInventario SHALL retornar todos los `TipoMovimiento` registrados.
3. WHEN se recibe una solicitud de consulta por tipoMovimientoId, THE GestorInventario SHALL retornar el `TipoMovimiento` correspondiente.
4. IF se recibe una solicitud de creación con codigo duplicado, THEN THE GestorInventario SHALL retornar un error con código HTTP 409.

---

### Requirement 8: Gestión de Usuarios

**User Story:** Como administrador, quiero gestionar los usuarios del sistema, para que solo las personas autorizadas puedan operar la aplicación.

#### Acceptance Criteria

1. WHEN se recibe una solicitud de creación de usuario con nombres, apellidos, email y passwordHash válidos, THE GestorUsuarios SHALL persistir el nuevo `Usuario` con UUID generado automáticamente, estadoCuenta igual a PENDIENTE, y retornar la representación con usuarioId asignado.
2. WHEN se recibe una solicitud de activación de usuario con usuarioId válido y estadoCuenta igual a PENDIENTE, THE GestorUsuarios SHALL actualizar estadoCuenta a ACTIVO y registrar la fecha-hora en activadoEn.
3. WHEN se recibe una solicitud de desactivación de usuario con usuarioId válido y estadoCuenta igual a ACTIVO, THE GestorUsuarios SHALL actualizar estadoCuenta a INACTIVO.
4. WHEN se recibe una solicitud de consulta de usuario por usuarioId, THE GestorUsuarios SHALL retornar el `Usuario` correspondiente sin exponer el campo passwordHash.
5. WHEN se recibe una solicitud de listado de usuarios con filtro opcional de estadoCuenta, THE GestorUsuarios SHALL retornar los usuarios que coincidan con el filtro provisto.
6. WHEN se recibe una solicitud de actualización de usuario con usuarioId existente, THE GestorUsuarios SHALL actualizar los campos nombres, apellidos y email provistos.
7. IF se recibe una solicitud de creación de usuario con email duplicado, THEN THE GestorUsuarios SHALL retornar un error con código HTTP 409.
8. IF se recibe una solicitud de creación de usuario con formato de email inválido, THEN THE GestorUsuarios SHALL retornar un error con código HTTP 400.
9. WHEN se recibe una solicitud de creación de usuario con proveedorSso y ssoSubjectId válidos, THE GestorUsuarios SHALL persistir el usuario sin requerir passwordHash.
10. THE GestorUsuarios SHALL registrar en el `LogAuditoria` toda operación de creación, activación y desactivación de `Usuario`.

---

### Requirement 9: Gestión de Roles

**User Story:** Como administrador, quiero gestionar los roles del sistema, para que pueda asignar niveles de acceso diferenciados a cada usuario.

#### Acceptance Criteria

1. WHEN se recibe una solicitud de creación de rol con nombre y descripcion válidos, THE GestorUsuarios SHALL persistir el nuevo `Rol` y retornar su representación con rolId asignado.
2. WHEN se recibe una solicitud de actualización de rol con rolId existente, THE GestorUsuarios SHALL actualizar los campos nombre y descripcion, y retornar el `Rol` actualizado.
3. WHEN se recibe una solicitud de listado de roles, THE GestorUsuarios SHALL retornar todos los roles registrados.
4. WHEN se recibe una solicitud de asignación de rol a usuario con usuarioId y rolId válidos, THE GestorUsuarios SHALL crear la asociación usuario-rol.
5. WHEN se recibe una solicitud de revocación de rol a usuario con usuarioId y rolId válidos, THE GestorUsuarios SHALL eliminar la asociación usuario-rol.
6. IF se recibe una solicitud de creación de rol con nombre duplicado, THEN THE GestorUsuarios SHALL retornar un error con código HTTP 409.
7. IF se recibe una solicitud de asignación de rol con usuarioId o rolId inexistente, THEN THE GestorUsuarios SHALL retornar un error con código HTTP 404.

---

### Requirement 10: Códigos de Acceso

**User Story:** Como administrador, quiero generar códigos de acceso de un solo uso, para que pueda invitar nuevos usuarios o permitir la recuperación de contraseña de forma segura.

#### Acceptance Criteria

1. WHEN se recibe una solicitud de generación de código de acceso con tipoCodigo (INVITACION o RECUPERACION) y usuarioId válidos, THE GestorUsuarios SHALL persistir el `CodigoAcceso` con codigoHash generado aleatoriamente, expiraEn igual a la fecha-hora actual más 24 horas, y usado igual a false.
2. WHEN se recibe una solicitud de validación de código de acceso con codigoHash válido, THE GestorUsuarios SHALL verificar que el código no esté marcado como usado y que expiraEn sea mayor a la fecha-hora actual, y retornar confirmación de validez.
3. WHEN se valida exitosamente un código de acceso, THE GestorUsuarios SHALL marcar el `CodigoAcceso` como usado=true y registrar la fecha-hora en usadoEn.
4. IF se recibe una solicitud de validación con codigoHash cuyo usado sea true, THEN THE GestorUsuarios SHALL retornar un error con código HTTP 410 indicando que el código ya fue utilizado.
5. IF se recibe una solicitud de validación con codigoHash cuyo expiraEn sea menor o igual a la fecha-hora actual, THEN THE GestorUsuarios SHALL retornar un error con código HTTP 410 indicando que el código está vencido.
6. THE GestorUsuarios SHALL registrar en el `LogAuditoria` la generación y el uso de cada `CodigoAcceso`.

---

### Requirement 11: Configuración del Negocio

**User Story:** Como administrador, quiero gestionar la configuración del negocio, para que la información del establecimiento sea precisa y esté disponible para todos los módulos.

#### Acceptance Criteria

1. WHEN se recibe una solicitud de creación de negocio con nombre y ruc válidos, THE GestorNegocio SHALL persistir el `Negocio` con activo=true, registrar la fecha-hora de creación en creadoEn, y retornar la representación con negocioId asignado.
2. WHEN se recibe una solicitud de actualización de negocio con negocioId existente, THE GestorNegocio SHALL actualizar los campos nombre, ruc y activo, y retornar el `Negocio` actualizado.
3. WHEN se recibe una solicitud de consulta del negocio activo, THE GestorNegocio SHALL retornar el `Negocio` con activo=true.
4. WHEN se recibe una solicitud de consulta por negocioId, THE GestorNegocio SHALL retornar el `Negocio` correspondiente.
5. IF se recibe una solicitud de creación de negocio con RUC de formato inválido (distinto de 13 dígitos numéricos), THEN THE GestorNegocio SHALL retornar un error con código HTTP 400.
6. IF se recibe una solicitud de creación de negocio con RUC duplicado, THEN THE GestorNegocio SHALL retornar un error con código HTTP 409.
7. THE GestorNegocio SHALL registrar en el `LogAuditoria` toda operación de creación y actualización de `Negocio`.

---

### Requirement 12: Alertas del Sistema

**User Story:** Como empleado, quiero recibir alertas del sistema, para que pueda reaccionar oportunamente a situaciones críticas como stock bajo o lotes próximos a vencer.

#### Acceptance Criteria

1. WHEN el GestorInventario actualiza el stockActual de un `Producto` a un valor menor o igual a su stockMinimo, THE GestorAlertas SHALL crear una `Alerta` de tipo STOCK_BAJO con mensaje "El producto [nombre] tiene stock [stockActual], igual o por debajo del mínimo [stockMinimo]".
2. WHEN se ejecuta la verificación periódica de vencimientos, THE GestorAlertas SHALL crear una `Alerta` de tipo VENCIMIENTO_PROXIMO por cada `LoteProducto` cuya fechaVencimiento sea menor o igual a los próximos 7 días y cuya cantidadDisponible sea mayor a cero.
3. WHEN se recibe una solicitud de listado de alertas con filtro opcional de tipo y estado leido, THE GestorAlertas SHALL retornar las alertas que coincidan con los filtros provistos, ordenadas por creadoEn descendente.
4. WHEN se recibe una solicitud de marcado de alerta como leída con alertaId válido, THE GestorAlertas SHALL actualizar leido=true en la `Alerta` correspondiente.
5. WHEN se recibe una solicitud de conteo de alertas no leídas, THE GestorAlertas SHALL retornar el número total de alertas con leido=false.
6. IF se recibe una solicitud de marcado de alerta con alertaId inexistente, THEN THE GestorAlertas SHALL retornar un error con código HTTP 404.

---

### Requirement 13: Registro de Auditoría

**User Story:** Como administrador, quiero consultar el registro de auditoría, para que pueda rastrear cualquier cambio realizado sobre las entidades críticas del sistema.

#### Acceptance Criteria

1. THE GestorAuditoria SHALL persistir automáticamente un `LogAuditoria` por cada operación de escritura (creación, actualización, eliminación) sobre las entidades Producto, Categoria, Proveedor, OrdenCompra, MovimientoInventario, Usuario, CodigoAcceso y Negocio.
2. THE GestorAuditoria SHALL almacenar en cada `LogAuditoria` el nombre de la entidad afectada, el identificador de la entidad, la acción realizada (CREAR, ACTUALIZAR, ELIMINAR), el detalle de los cambios en formato JSON, y la fecha-hora del evento.
3. WHEN se recibe una solicitud de consulta de logs con filtros opcionales de entidad, accion y rango de fechas, THE GestorAuditoria SHALL retornar los registros que coincidan con los filtros provistos, ordenados por creadoEn descendente.
4. WHEN se recibe una solicitud de consulta de logs por entidadId, THE GestorAuditoria SHALL retornar todos los `LogAuditoria` asociados a ese identificador de entidad.
5. THE GestorAuditoria SHALL garantizar que ningún `LogAuditoria` pueda ser modificado ni eliminado a través de la API.

---

### Requirement 14: Identificación de Productos mediante IA

**User Story:** Como empleado, quiero identificar productos mediante escaneo de código de barras o imagen, para que el registro de nuevos productos sea más rápido y preciso.

#### Acceptance Criteria

1. WHEN se recibe una solicitud de identificación de producto con imagen o código de barras codificado en base64, THE GestorIa SHALL invocar el modelo de IA configurado, persistir el resultado en `IdentificacionIa` con nombreModelo, probabilidad, resultado y creadoEn, y retornar la representación con identificacionIaId asignado.
2. WHEN la probabilidad del resultado de identificación es mayor o igual a 0.80, THE GestorIa SHALL retornar el resultado con indicador de confianza ALTA.
3. WHEN la probabilidad del resultado de identificación es menor a 0.80, THE GestorIa SHALL retornar el resultado con indicador de confianza BAJA.
4. WHEN se registra una `IdentificacionIa`, THE GestorIa SHALL incrementar el contador de tokensUsados en el `ConsumoIaMensual` del negocio para el mes y año en curso.
5. WHILE el consumo acumulado de tokens del negocio en el mes supere el límite definido en `TokensIaNegocio`, THE GestorIa SHALL retornar un error con código HTTP 429 indicando que se agotó la cuota mensual de IA.
6. WHEN se recibe una solicitud de consulta de historial de identificaciones con filtros opcionales de rango de fechas y productoId, THE GestorIa SHALL retornar las `IdentificacionIa` que coincidan con los filtros, ordenadas por creadoEn descendente.
7. IF se recibe una solicitud de identificación con imagen en formato no soportado (distinto de JPEG, PNG o WEBP), THEN THE GestorIa SHALL retornar un error con código HTTP 400 y mensaje descriptivo.

---

### Requirement 15: Secuencias de Códigos

**User Story:** Como sistema, quiero generar códigos únicos y secuenciales para movimientos y órdenes de compra, para que cada documento tenga un identificador legible y no repetible.

#### Acceptance Criteria

1. WHEN se solicita el siguiente número de secuencia para un tipo dado, THE Sistema SHALL incrementar el último número de la `SecuenciaCodigo` correspondiente en 1 y retornarlo de forma atómica, garantizando que dos operaciones concurrentes nunca obtengan el mismo número.
2. THE Sistema SHALL generar el codigoMovimiento concatenando el prefijoCodigo del `TipoMovimiento` con el número de secuencia formateado con ceros a la izquierda hasta 8 dígitos.
3. THE Sistema SHALL generar el codigoReferencia de `OrdenCompra` con el prefijo "OC-" seguido del número de secuencia formateado con ceros a la izquierda hasta 8 dígitos.
4. THE Sistema SHALL generar el codigoEntrada de `LoteProducto` con el prefijo "LOTE-" seguido del número de secuencia formateado con ceros a la izquierda hasta 8 dígitos.
5. IF la operación de incremento de secuencia falla por conflicto de concurrencia, THEN THE Sistema SHALL reintentar la operación hasta 3 veces antes de retornar un error con código HTTP 503.

---

### Requirement 16: Consistencia Transaccional e Integridad de Datos

**User Story:** Como sistema, quiero que todas las operaciones que afecten múltiples entidades sean atómicas, para que el estado de la base de datos sea siempre consistente.

#### Acceptance Criteria

1. WHEN se recibe la orden de compra con estado RECIBIDA, THE Sistema SHALL ejecutar en una única transacción la actualización del estado de la `OrdenCompra`, la creación de los `LoteProducto` y la actualización del stockActual de cada `Producto`.
2. WHEN se registra un `MovimientoInventario` de tipo SALIDA, THE Sistema SHALL ejecutar en una única transacción la creación del movimiento y la actualización de cantidadDisponible del `LoteProducto` y del stockActual del `Producto`.
3. IF cualquier operación dentro de una transacción falla, THEN THE Sistema SHALL revertir todos los cambios de esa transacción y retornar un error con código HTTP 500 y mensaje descriptivo.
4. THE Sistema SHALL retornar todas las respuestas de error con una estructura JSON uniforme que incluya los campos timestamp, status, error y message.
5. WHEN se recibe cualquier solicitud con cuerpo JSON mal formado, THE Sistema SHALL retornar un error con código HTTP 400 y mensaje que identifique el campo problemático.

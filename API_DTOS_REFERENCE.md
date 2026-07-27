# 📚 API DTOs Reference - DRINKHOUSE Backend

## Índice de Contenido
1. [Productos](#1-productos)
2. [Usuarios](#2-usuarios)
3. [Órdenes de Compra](#3-órdenes-de-compra)
4. [Identificación IA](#4-identificación-ia)
5. [Categorías](#5-categorías)
6. [Proveedores](#6-proveedores)
7. [Roles](#7-roles)
8. [Movimientos de Inventario](#8-movimientos-de-inventario)
9. [Lotes de Producto](#9-lotes-de-producto)
10. [Alertas](#10-alertas)
11. [Negocios](#11-negocios)
12. [Errores](#12-manejo-de-errores)

---

## 1. PRODUCTOS

### 📤 POST/PUT Request - ProductoRequestDto
```json
{
  "nombre": "Johnnie Walker Red Label",
  "marca": "Johnnie Walker",
  "tipo": "Whisky",
  "descripcion": "Whisky escocés blended de alta calidad",
  "categoriaId": 1,
  "costoPromedio": 25.50,
  "margenGanancia": 30.00,
  "precioVenta": 33.15,
  "precioPersonalizado": true,
  "stockMinimo": 10,
  "visibleSinStock": false
}
```

**Validaciones:**
- ✅ `nombre`: obligatorio, no vacío
- ✅ `marca`: obligatorio, no vacío
- ✅ `costoPromedio`: obligatorio, > 0
- ✅ `margenGanancia`: obligatorio, >= 0
- ⚠️ `precioVenta`: requerido solo si `precioPersonalizado = true`

### 📥 Response - ProductoResponseDto
```json
{
  "productoId": 1,
  "nombre": "Johnnie Walker Red Label",
  "marca": "Johnnie Walker",
  "tipo": "Whisky",
  "descripcion": "Whisky escocés blended de alta calidad",
  "categoriaId": 1,
  "costoPromedio": 25.50,
  "margenGanancia": 30.00,
  "precioVenta": 33.15,
  "precioPersonalizado": true,
  "stockActual": 50,
  "stockMinimo": 10,
  "visibleSinStock": false,
  "origenIdentificacion": "MANUAL",
  "activo": true,
  "creadoEn": "2026-07-26T10:30:00Z",
  "actualizadoEn": "2026-07-26T10:30:00Z"
}
```

**Campos adicionales en Response:**
- `productoId`: ID único del producto (generado)
- `stockActual`: Stock actual (calculado por movimientos)
- `origenIdentificacion`: "MANUAL" o "IA"
- `activo`: Estado del producto (por defecto true)
- `creadoEn`, `actualizadoEn`: Timestamps ISO-8601

---

## 2. USUARIOS

### 📤 POST/PUT Request - UsuarioRequestDto
```json
{
  "nombreCompleto": "Juan Pérez García",
  "email": "juan.perez@example.com",
  "passwordHash": "hashed_password_here",
  "proveedorSso": "google",
  "ssoSubjectId": "google_user_id_123",
  "rolId": 1
}
```

**Validaciones:**
- ✅ `nombreCompleto`: obligatorio, no vacío
- ✅ `email`: obligatorio, formato email válido
- ⚠️ `passwordHash`: opcional si se usa SSO
- ⚠️ `proveedorSso` y `ssoSubjectId`: para autenticación con SSO

### 📥 Response - UsuarioResponseDto
```json
{
  "usuarioId": "550e8400-e29b-41d4-a716-446655440000",
  "negocioId": 1,
  "rolId": 1,
  "nombreCompleto": "Juan Pérez García",
  "email": "juan.perez@example.com",
  "proveedorSso": "google",
  "estadoCuenta": "ACTIVO",
  "activadoEn": "2026-07-26T10:30:00Z",
  "creadoEn": "2026-07-26T10:30:00Z",
  "actualizadoEn": "2026-07-26T10:30:00Z"
}
```

**Notas importantes:**
- ⚠️ `usuarioId`: Tipo UUID (no es número)
- 🔒 `passwordHash`: **NUNCA se expone** en responses
- 📊 `estadoCuenta`: Valores: "ACTIVO", "INACTIVO", "SUSPENDIDO"

---

## 3. ÓRDENES DE COMPRA

### 📤 POST/PUT Request - OrdenCompraRequestDto
```json
{
  "proveedorId": 5,
  "negocioId": 1,
  "detalles": [
    {
      "productoId": 10,
      "cantidad": 24,
      "precioUnitario": 25.50
    },
    {
      "productoId": 15,
      "cantidad": 12,
      "precioUnitario": 18.75
    }
  ]
}
```

**Validaciones:**
- ✅ `proveedorId`: obligatorio
- ✅ `detalles`: obligatorio, array no vacío
- ✅ `cantidad`: mínimo 1
- ✅ `precioUnitario`: mayor a 0

### 📥 Response - OrdenCompraResponseDto
```json
{
  "ordenCompraId": 42,
  "codigoReferencia": "OC-00000042",
  "estado": "BORRADOR",
  "total": 837.00,
  "creadoEn": "2026-07-26T10:30:00Z",
  "proveedorId": 5,
  "detalles": [
    {
      "detalleOrdenCompraId": 101,
      "ordenCompraId": 42,
      "productoId": 10,
      "cantidad": 24,
      "precioUnitario": 25.50
    },
    {
      "detalleOrdenCompraId": 102,
      "ordenCompraId": 42,
      "productoId": 15,
      "cantidad": 12,
      "precioUnitario": 18.75
    }
  ]
}
```

**Estados de la Orden:**
- `"BORRADOR"`: Creada pero no enviada (editable)
- `"ENVIADA"`: Enviada al proveedor (no editable)
- `"RECIBIDA"`: Productos recibidos, stock actualizado
- `"ANULADA"`: Orden cancelada

**Flujo de Estados:**
```
BORRADOR --enviar--> ENVIADA --recibir--> RECIBIDA
   |                    |
   +---anular-----------+-----> ANULADA
```

**Operación al RECIBIR orden:**
- ✅ Se crean lotes automáticamente
- ✅ Se incrementa el stock de productos
- ✅ Se registran movimientos de inventario tipo "ENTRADA"

---

## 4. IDENTIFICACIÓN IA

### 📤 POST Request - IdentificacionIaRequestDto
```json
{
  "imagenBase64": "/9j/4AAQSkZJRgABAQEAYABgAAD...",
  "formatoImagen": "image/jpeg",
  "productoId": 15,
  "negocioId": 1,
  "tipoIdentificacion": "BOTELLA"
}
```

**Validaciones:**
- ✅ `imagenBase64`: obligatorio, string base64
- ✅ `formatoImagen`: "image/jpeg", "image/png", "image/webp"
- ✅ `productoId`: obligatorio
- ✅ `negocioId`: obligatorio
- ✅ `tipoIdentificacion`: obligatorio, valores: **"BOTELLA"** o **"FACTURA"**

### 📥 Response - IdentificacionIaResponseDto (Tipo BOTELLA)
```json
{
  "identificacionIaId": 123,
  "nombreModelo": "claude-3-5-sonnet-20241022",
  "resultado": "Se identificó: Johnnie Walker Red Label...",
  "nombreSugerido": "Johnnie Walker Red Label",
  "marcaSugerida": "Johnnie Walker",
  "tipoSugerido": "whisky",
  "reconocido": true,
  "tipoIdentificacion": "BOTELLA",
  "resultadoBotella": {
    "nombre": "Johnnie Walker Red Label",
    "marca": "Johnnie Walker",
    "tipo": "whisky",
    "presentacion": "750ml",
    "graduacionAlcohol": "40%",
    "reconocido": true
  },
  "resultadoFactura": null,
  "productoId": 15,
  "creadoEn": "2026-07-26T10:30:00Z"
}
```

### 📥 Response - IdentificacionIaResponseDto (Tipo FACTURA)
```json
{
  "identificacionIaId": 124,
  "nombreModelo": "claude-3-5-sonnet-20241022",
  "resultado": "Factura procesada correctamente...",
  "nombreSugerido": null,
  "marcaSugerida": null,
  "tipoSugerido": null,
  "reconocido": true,
  "tipoIdentificacion": "FACTURA",
  "resultadoBotella": null,
  "resultadoFactura": {
    "rucProveedor": "1234567890001",
    "razonSocialProveedor": "Distribuidora ABC S.A.",
    "fechaFactura": "2026-07-20",
    "numeroFactura": "001-001-0000123",
    "productos": [
      {
        "nombre": "Johnnie Walker Red",
        "marca": "Johnnie Walker",
        "tipo": "whisky",
        "cantidad": 24,
        "precioUnitario": 25.50,
        "subtotal": 612.00
      },
      {
        "nombre": "Absolut Vodka",
        "marca": "Absolut",
        "tipo": "vodka",
        "cantidad": 12,
        "precioUnitario": 18.75,
        "subtotal": 225.00
      }
    ],
    "totalFactura": 837.00
  },
  "productoId": 15,
  "creadoEn": "2026-07-26T10:30:00Z"
}
```

**Notas importantes:**
- 🔥 **Consume cuota mensual de tokens IA por negocio**
- ⚠️ Error 429 si se excede la cuota
- ⚠️ Error 503 si Claude AI no está disponible

---

## 5. CATEGORÍAS

### 📤 POST/PUT Request - CategoriaRequestDto
```json
{
  "nombre": "Whiskies Premium",
  "margenGananciaPct": 35.00,
  "activo": true
}
```

**Validaciones:**
- ✅ `nombre`: obligatorio, no vacío
- ✅ `margenGananciaPct`: obligatorio, >= 0

### 📥 Response - CategoriaResponseDto
```json
{
  "categoriaId": 1,
  "nombre": "Whiskies Premium",
  "margenGananciaPct": 35.00,
  "activo": true
}
```

---

## 6. PROVEEDORES

### 📤 POST/PUT Request - ProveedorRequestDto
```json
{
  "ruc": "1234567890001",
  "razonSocial": "Distribuidora ABC S.A.",
  "direccion": "Av. Principal 123, Quito",
  "telefono": "0987654321",
  "email": "ventas@distribuidoraabc.com"
}
```

**Validaciones:**
- ✅ `ruc`: obligatorio, exactamente 13 dígitos numéricos
- ✅ `razonSocial`: obligatorio, no vacío
- ✅ `email`: obligatorio, formato email válido

### 📥 Response - ProveedorResponseDto
```json
{
  "proveedorId": 5,
  "ruc": "1234567890001",
  "razonSocial": "Distribuidora ABC S.A.",
  "direccion": "Av. Principal 123, Quito",
  "telefono": "0987654321",
  "email": "ventas@distribuidoraabc.com"
}
```

---

## 7. ROLES

### 📤 POST/PUT Request - RolRequestDto
```json
{
  "nombre": "Administrador",
  "descripcion": "Acceso completo al sistema"
}
```

**Validaciones:**
- ✅ `nombre`: obligatorio, no vacío
- ✅ `descripcion`: obligatorio, no vacío

### 📥 Response - RolResponseDto
```json
{
  "rolId": 1,
  "nombre": "Administrador",
  "descripcion": "Acceso completo al sistema"
}
```

---

## 8. MOVIMIENTOS DE INVENTARIO

### 📤 POST Request - MovimientoInventarioRequestDto
```json
{
  "productoId": 10,
  "loteId": 25,
  "tipoMovimientoId": 2,
  "cantidad": 5,
  "precioUnitario": 25.50
}
```

**Validaciones:**
- ✅ `productoId`: obligatorio
- ✅ `tipoMovimientoId`: obligatorio
- ✅ `cantidad`: obligatorio, > 0
- ⚠️ `loteId`: **requerido para movimientos tipo SALIDA**

**Tipos de Movimiento:**
- `1`: ENTRADA (incrementa stock)
- `2`: SALIDA (decrementa stock)
- `3`: AJUSTE (puede incrementar o decrementar)

### 📥 Response - MovimientoInventarioResponseDto
```json
{
  "movimientoId": 501,
  "codigoMovimiento": "MOV-00000501",
  "productoId": 10,
  "loteId": 25,
  "tipoMovimientoId": 2,
  "tipoMovimiento": "SALIDA",
  "cantidad": 5,
  "precioUnitario": 25.50,
  "creadoEn": "2026-07-26T10:30:00Z"
}
```

---

## 9. LOTES DE PRODUCTO

### 📥 Response - LoteProductoResponseDto
```json
{
  "loteId": 25,
  "codigoEntrada": "LOTE-00000025",
  "cantidadInicial": 24,
  "cantidadDisponible": 19,
  "precioCosto": 25.50,
  "fechaIngreso": "2026-07-20T14:30:00Z",
  "fechaVencimiento": "2028-12-31"
}
```

**Notas:**
- 🔄 `cantidadDisponible`: Se actualiza con cada movimiento de salida
- 📅 `fechaVencimiento`: Puede ser `null` si no aplica
- 🏭 Lotes se generan automáticamente al recibir una orden de compra

---

## 10. ALERTAS

### 📥 Response - AlertaResponseDto
```json
{
  "alertaId": 78,
  "negocioId": 1,
  "tipoAlerta": "STOCK_BAJO",
  "referenciaTipo": "PRODUCTO",
  "referenciaId": 10,
  "mensaje": "El producto 'Johnnie Walker Red' está por debajo del stock mínimo",
  "atendida": false,
  "creadoEn": "2026-07-26T10:30:00Z"
}
```

**Tipos de Alerta:**
- `"STOCK_BAJO"`: Stock debajo del mínimo
- `"LOTE_PROXIMO_VENCER"`: Lote próximo a vencer
- `"LOTE_VENCIDO"`: Lote vencido

**Tipos de Referencia:**
- `"PRODUCTO"`: Referencia a un producto
- `"LOTE"`: Referencia a un lote

---

## 11. NEGOCIOS

### 📥 Response - NegocioResponseDto
```json
{
  "negocioId": 1,
  "nombre": "Licorería El Paraíso",
  "ruc": "0987654321001",
  "activo": true,
  "creadoEn": "2026-01-15T08:00:00"
}
```

**Notas:**
- ⚠️ `creadoEn`: Usa `LocalDateTime` (sin zona horaria)
- 🏢 El sistema soporta multi-tenancy (múltiples negocios)

---

## 12. MANEJO DE ERRORES

### 📥 Error Response - ErrorResponseDto
```json
{
  "timestamp": "2026-07-26T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Producto con ID 999 no encontrado"
}
```

**Códigos HTTP comunes:**

| Código | Tipo | Descripción | Ejemplo |
|--------|------|-------------|---------|
| **400** | Bad Request | JSON mal formado, validación fallida | Campo obligatorio faltante |
| **404** | Not Found | Recurso no encontrado | Producto con ID inexistente |
| **405** | Method Not Allowed | Método HTTP no soportado | POST en endpoint solo GET |
| **409** | Conflict | Conflicto de unicidad | Email o RUC duplicado |
| **422** | Unprocessable Entity | Regla de negocio violada | Orden ya enviada no se puede editar |
| **429** | Too Many Requests | Cuota de IA excedida | Límite mensual de tokens IA |
| **500** | Internal Server Error | Error interno del servidor | Error inesperado |
| **503** | Service Unavailable | Servicio externo no disponible | Claude AI no responde |

---

## 🎯 GUÍA RÁPIDA DE TIPOS DE DATOS

### Tipos primitivos:
- `String`: Texto
- `Integer`: Número entero (ejemplo: 42)
- `Long`: Número entero largo (ejemplo: 1234567890)
- `Double`: Número decimal (ejemplo: 25.50)
- `BigDecimal`: Decimal preciso para moneda (ejemplo: "25.50")
- `Boolean`: true/false
- `UUID`: Identificador único (ejemplo: "550e8400-e29b-41d4-a716-446655440000")

### Tipos de fecha/hora:
- `OffsetDateTime`: Fecha con hora y zona horaria ISO-8601
  - Formato: `"2026-07-26T10:30:00Z"`
- `LocalDateTime`: Fecha con hora sin zona horaria
  - Formato: `"2026-07-26T10:30:00"`
- `LocalDate`: Solo fecha
  - Formato: `"2026-07-26"`


---

## 📋 ENDPOINTS Y DTOs - TABLA DE REFERENCIA RÁPIDA

| Módulo | Endpoint | Método | Request DTO | Response DTO |
|--------|----------|--------|-------------|--------------|
| **Productos** | `/api/v1/productos` | POST | ProductoRequestDto | ProductoResponseDto |
| | `/api/v1/productos/{id}` | PUT | ProductoRequestDto | ProductoResponseDto |
| | `/api/v1/productos/{id}` | GET | - | ProductoResponseDto |
| | `/api/v1/productos` | GET | - | List<ProductoResponseDto> |
| | `/api/v1/productos/buscar` | GET | Query params | List<ProductoResponseDto> |
| **Usuarios** | `/api/v1/usuarios` | POST | UsuarioRequestDto | UsuarioResponseDto |
| | `/api/v1/usuarios/{id}` | PUT | UsuarioRequestDto | UsuarioResponseDto |
| | `/api/v1/usuarios/{id}/activar` | PATCH | - | UsuarioResponseDto |
| | `/api/v1/usuarios/{id}/desactivar` | PATCH | - | UsuarioResponseDto |
| **Órdenes** | `/api/v1/ordenes-compra` | POST | OrdenCompraRequestDto | OrdenCompraResponseDto |
| | `/api/v1/ordenes-compra/{id}` | PUT | OrdenCompraRequestDto | OrdenCompraResponseDto |
| | `/api/v1/ordenes-compra/{id}/enviar` | PATCH | - | OrdenCompraResponseDto |
| | `/api/v1/ordenes-compra/{id}/recibir` | PATCH | - | OrdenCompraResponseDto |
| | `/api/v1/ordenes-compra/{id}/anular` | PATCH | - | OrdenCompraResponseDto |
| **IA** | `/api/v1/ia/identificar` | POST | IdentificacionIaRequestDto | IdentificacionIaResponseDto |
| | `/api/v1/ia/historial` | GET | Query params | List<IdentificacionIaResponseDto> |
| **Categorías** | `/api/v1/categorias` | POST | CategoriaRequestDto | CategoriaResponseDto |
| | `/api/v1/categorias/{id}` | PUT | CategoriaRequestDto | CategoriaResponseDto |
| **Proveedores** | `/api/v1/proveedores` | POST | ProveedorRequestDto | ProveedorResponseDto |
| | `/api/v1/proveedores/{id}` | PUT | ProveedorRequestDto | ProveedorResponseDto |
| **Roles** | `/api/v1/roles` | POST | RolRequestDto | RolResponseDto |
| | `/api/v1/roles/{id}` | PUT | RolRequestDto | RolResponseDto |
| **Movimientos** | `/api/v1/movimientos-inventario` | POST | MovimientoInventarioRequestDto | MovimientoInventarioResponseDto |
| **Lotes** | `/api/v1/lotes-producto` | GET | - | List<LoteProductoResponseDto> |
| **Alertas** | `/api/v1/alertas` | GET | - | List<AlertaResponseDto> |
| **Negocios** | `/api/v1/negocios` | GET | - | List<NegocioResponseDto> |

---

## 💡 EJEMPLOS DE INTEGRACIÓN

### Ejemplo 1: Crear un producto

```javascript
const nuevoProducto = {
  nombre: "Absolut Vodka",
  marca: "Absolut",
  tipo: "Vodka",
  categoriaId: 2,
  costoPromedio: 18.50,
  margenGanancia: 25.00,
  stockMinimo: 15
};

const response = await fetch('http://localhost:8080/api/v1/productos', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(nuevoProducto)
});

const producto = await response.json();
console.log('Producto creado:', producto.productoId);
```


### Ejemplo 2: Crear una orden de compra

```javascript
const nuevaOrden = {
  proveedorId: 5,
  negocioId: 1,
  detalles: [
    { productoId: 10, cantidad: 24, precioUnitario: 25.50 },
    { productoId: 15, cantidad: 12, precioUnitario: 18.75 }
  ]
};

const response = await fetch('http://localhost:8080/api/v1/ordenes-compra', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(nuevaOrden)
});

const orden = await response.json();
console.log('Orden creada:', orden.codigoReferencia);
```

### Ejemplo 3: Enviar y recibir una orden

```javascript
// 1. Enviar la orden (BORRADOR -> ENVIADA)
await fetch(`http://localhost:8080/api/v1/ordenes-compra/${ordenId}/enviar`, {
  method: 'PATCH'
});

// 2. Recibir la orden (ENVIADA -> RECIBIDA)
// Esto generará lotes y actualizará el stock automáticamente
const response = await fetch(`http://localhost:8080/api/v1/ordenes-compra/${ordenId}/recibir`, {
  method: 'PATCH'
});

const ordenRecibida = await response.json();
console.log('Stock actualizado para', ordenRecibida.detalles.length, 'productos');
```

### Ejemplo 4: Identificar producto con IA

```javascript
// Convertir imagen a base64 (ejemplo con File API)
const file = document.getElementById('fileInput').files[0];
const reader = new FileReader();

reader.onload = async (e) => {
  const base64 = e.target.result.split(',')[1]; // Remover el prefijo data:image/...
  
  const solicitud = {
    imagenBase64: base64,
    formatoImagen: 'image/jpeg',
    productoId: 15,
    negocioId: 1,
    tipoIdentificacion: 'BOTELLA'
  };
  
  const response = await fetch('http://localhost:8080/api/v1/ia/identificar', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(solicitud)
  });
  
  const resultado = await response.json();
  
  if (resultado.reconocido) {
    console.log('Producto identificado:', resultado.nombreSugerido);
    console.log('Marca:', resultado.marcaSugerida);
    console.log('Tipo:', resultado.tipoSugerido);
  } else {
    console.log('Producto no reconocido');
  }
};

reader.readAsDataURL(file);
```


### Ejemplo 5: Manejo de errores

```javascript
async function crearProducto(datos) {
  try {
    const response = await fetch('http://localhost:8080/api/v1/productos', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(datos)
    });
    
    if (!response.ok) {
      const error = await response.json();
      
      // Manejar diferentes tipos de error
      switch (error.status) {
        case 400:
          console.error('Datos inválidos:', error.message);
          break;
        case 409:
          console.error('Conflicto de unicidad:', error.message);
          break;
        case 422:
          console.error('Regla de negocio violada:', error.message);
          break;
        default:
          console.error('Error:', error.message);
      }
      
      return null;
    }
    
    return await response.json();
    
  } catch (err) {
    console.error('Error de red:', err);
    return null;
  }
}
```

---

## 🔐 NOTAS DE SEGURIDAD

1. **Passwords**: Nunca se exponen en responses, siempre se hashean antes de almacenar
2. **CORS**: Configurado para permitir todos los orígenes en desarrollo
3. **Validaciones**: Todas las validaciones se ejecutan en el backend
4. **IDs sensibles**: Los IDs de usuario son UUID para mayor seguridad

---

## 📞 SOPORTE

Para dudas o problemas con la integración:
- Revisa los logs del backend (nivel DEBUG habilitado)
- Verifica que el formato JSON sea correcto
- Asegúrate de usar los tipos de datos correctos
- Usa el endpoint `/api/v1/health` para verificar conectividad

---

**Última actualización:** 26 de julio de 2026
**Versión del API:** v1
**Spring Boot:** 4.1.0
**Java:** 21


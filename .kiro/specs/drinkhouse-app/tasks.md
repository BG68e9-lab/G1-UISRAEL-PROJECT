# Implementation Plan: DrinkHouse App

## Overview

Implementación completa de la API REST DrinkHouse App sobre Spring Boot 4.1.0 / Java 21 con arquitectura hexagonal (4 capas). El orden de las tareas sigue las dependencias del modelo de dominio: infraestructura base → SecuenciaCodigo → LogAuditoria → Categorías → Productos → ... → IA → Scheduler → Tests.

Todo el código se escribe en **español** (clases, métodos, variables, comentarios).

## Tasks

- [x] 1. Infraestructura base: excepciones, manejo de errores y configuración
  - [x] 1.1 Crear excepciones de dominio en `dominio/excepciones/`
    - Crear `RecursoNoEncontradoException`, `ConflictoUnicoException`, `ReglaNegocioException`, `CuotaIaExcedidaException`, `ServicioNoDisponibleException` como clases que extienden `RuntimeException`
    - Cada excepción recibe un `String mensaje` en el constructor
    - _Requisitos: 16.3, 16.4_

  - [x] 1.2 Crear `ErrorResponseDto` y `GlobalExceptionHandler`
    - Crear `presentacion/dto/response/ErrorResponseDto.java` con campos `timestamp`, `status`, `error`, `message`
    - Crear `presentacion/controladores/GlobalExceptionHandler.java` con `@RestControllerAdvice`
    - Manejar: `RecursoNoEncontradoException` (404), `ConflictoUnicoException` (409), `ReglaNegocioException` (422), `CuotaIaExcedidaException` (429), `ServicioNoDisponibleException` (503), `MethodArgumentNotValidException` (400), `HttpMessageNotReadableException` (400), `Exception` (500)
    - _Requisitos: 16.3, 16.4, 16.5_

  - [x] 1.3 Configurar `pom.xml` con dependencia jqwik para tests de propiedad
    - Agregar `net.jqwik:jqwik:1.8.5` con `scope=test`
    - Verificar que `spring-boot-starter-test`, `testcontainers` y `postgresql` (Testcontainers) estén presentes
    - _Requisitos: Estrategia de testing_

- [x] 2. Módulo SecuenciaCodigo (base para generación de códigos únicos)
  - [x] 2.1 Completar entidad de dominio `SecuenciaCodigo` y su repositorio
    - Completar `dominio/entidades/SecuenciaCodigo.java` con campos: `secuenciaCodigoId` (Long), `tipo` (String), `ultimoNumero` (Long)
    - Completar `dominio/repositorios/ISecuenciaCodigoRepositorio.java` con métodos: `buscarPorTipo(String tipo)`, `guardar(SecuenciaCodigo seq)`
    - _Requisitos: 15.1_

  - [x] 2.2 Completar entidad JPA `SecuenciaCodigoEntity` y repositorio Spring Data
    - Completar `infraestructura/persistencia/jpa/SecuenciaCodigoEntity.java` con `@Entity`, `@Table(name="secuencia_codigo")`, `@Id`, `@Version Long version` (para optimistic locking), todos los campos mapeados con `@Column`
    - Completar `infraestructura/repositorio/ISecuenciaCodigoJpaRepositorio.java` con `findByTipo(String tipo)`
    - _Requisitos: 15.1, 15.5_

  - [x] 2.3 Completar mapper JPA y adaptador de repositorio para SecuenciaCodigo
    - Completar `infraestructura/persistencia/mapeadores/ISecuenciaCodigoJpaMapper.java` con métodos `aDominio` y `aEntidad`
    - Completar `infraestructura/persistencia/adaptadores/SecuenciaCodigoRepositorioImpl.java` implementando `ISecuenciaCodigoRepositorio`
    - _Requisitos: 15.1_

  - [x] 2.4 Completar caso de uso `ISecuenciaCodigoUseCase` y su implementación
    - Completar `aplicacion/casosuso/entrada/ISecuenciaCodigoUseCase.java` con método `Long siguiente(String tipo)`
    - Completar `aplicacion/casosuso/impl/SecuenciaCodigoUseCaseImpl.java` con lógica de reintento hasta 3 veces ante `OptimisticLockingFailureException`, lanzar `ServicioNoDisponibleException` si fallan los 3 intentos
    - Anotar el método `siguiente` con `@Transactional`
    - _Requisitos: 15.1, 15.5_

- [x] 3. Módulo LogAuditoria (base transversal de auditoría)
  - [x] 3.1 Completar entidad de dominio `LogAuditoria` y su repositorio
    - Completar `dominio/entidades/LogAuditoria.java` con campos: `logId` (Long), `entidad` (String), `entidadId` (String), `accion` (String), `detalle` (String), `creadoEn` (OffsetDateTime)
    - Completar `dominio/repositorios/ILogAuditoriaRepositorio.java` con métodos: `guardar(LogAuditoria log)`, `buscarConFiltros(String entidad, String accion, OffsetDateTime desde, OffsetDateTime hasta)`, `buscarPorEntidadId(String entidadId)`
    - _Requisitos: 13.1, 13.2, 13.3, 13.4_

  - [x] 3.2 Completar entidad JPA, mapper, adaptador y repositorio Spring Data para LogAuditoria
    - Completar `infraestructura/persistencia/jpa/LogAuditoriaEntity.java` con `@Entity`, `@Table(name="log_auditoria")`, todos los campos mapeados; NO definir endpoints de escritura vía API
    - Completar `infraestructura/repositorio/ILogAuditoriaJpaRepositorio.java` con queries de filtro usando `@Query` o `Specification`
    - Completar mapper y adaptador correspondientes
    - _Requisitos: 13.1, 13.5_

  - [x] 3.3 Completar `ILogAuditoriaUseCase` y su implementación
    - Completar interfaz con métodos: `registrar(String entidad, String entidadId, String accion, Object detalle)`, `consultarConFiltros(...)`, `consultarPorEntidadId(String entidadId)`
    - Completar `LogAuditoriaUseCaseImpl`: serializar `detalle` a JSON con Jackson antes de persistir
    - _Requisitos: 13.1, 13.2, 13.3, 13.4_

  - [x] 3.4 Completar DTOs y controller REST de LogAuditoria
    - Crear `LogAuditoriaResponseDto` con todos los campos (sin campos de escritura)
    - Completar `LogAuditoriaController` con GET `/api/v1/auditoria` (filtros: `entidad`, `accion`, `desde`, `hasta`) y GET `/api/v1/auditoria/entidad/{entidadId}`
    - Verificar que NO existen endpoints POST, PUT ni DELETE para auditoria
    - _Requisitos: 13.3, 13.4, 13.5_

- [x] 4. Módulo Negocio
  - [x] 4.1 Completar entidad de dominio `Negocio`, repositorio de dominio
    - Completar `dominio/entidades/Negocio.java` con campos: `negocioId` (Integer), `nombre` (String), `ruc` (String), `activo` (Boolean), `creadoEn` (LocalDateTime)
    - Completar `dominio/repositorios/INegocioRepositorio.java` con métodos: `guardar`, `buscarPorId`, `buscarActivo`, `existePorRuc`
    - _Requisitos: 11.1, 11.2, 11.3, 11.4_

  - [x] 4.2 Completar entidad JPA, mapper JPA, adaptador y repositorio Spring Data para Negocio
    - Completar `NegocioEntity.java` con `@Entity`, `@Table(name="negocio")`, todos los campos mapeados
    - Completar `INegocioJpaRepositorio.java` con `findByActivoTrue()` y `existsByRuc(String ruc)`
    - Completar mapper y adaptador correspondientes
    - _Requisitos: 11.1, 11.6_

  - [x] 4.3 Completar `INegocioUseCase` y `NegocioUseCaseImpl`
    - Métodos: `crearNegocio`, `actualizarNegocio`, `buscarActivo`, `buscarPorId`
    - Validar formato RUC con regex `\d{13}` lanzando `ReglaNegocioException` si inválido
    - Verificar unicidad de RUC lanzando `ConflictoUnicoException` si duplicado
    - Llamar a `logAuditoriaUseCase.registrar(...)` en crear y actualizar
    - _Requisitos: 11.1, 11.2, 11.5, 11.6, 11.7_

  - [x] 4.4 Completar DTOs y `NegocioController`
    - Crear `NegocioRequestDto` (`@NotBlank nombre`, `@NotBlank @Pattern(regexp="\\d{13}") ruc`, `Boolean activo`)
    - Crear `NegocioResponseDto` con todos los campos
    - Completar mapper DTO `INegocioDtoMapper`
    - Completar `NegocioController` con POST `/api/v1/negocio` (201), PUT `/{id}` (200), GET `/activo` (200), GET `/{id}` (200)
    - _Requisitos: 11.1, 11.2, 11.3, 11.4_

- [x] 5. Módulo Categorías
  - [x] 5.1 Completar entidad de dominio `Categoria` y repositorio de dominio
    - Completar `dominio/entidades/Categoria.java` con campos: `categoriaId` (Long), `nombre` (String), `margenGananciaPct` (BigDecimal), `activo` (Boolean)
    - Completar `dominio/repositorios/ICategoriaRepositorio.java` con métodos: `guardar`, `buscarPorId`, `listarTodas`, `eliminar`, `existePorNombre`, `tieneProductosAsociados`
    - _Requisitos: 1.1, 1.2, 1.3, 1.4, 1.5_

  - [x] 5.2 Completar entidad JPA, mapper JPA, adaptador y repositorio Spring Data para Categoria
    - Completar `CategoriaEntity.java` con `@Entity`, `@Table(name="categoria")`, todos los campos mapeados
    - Completar `ICategoriaJpaRepositorio.java` con `existsByNombre(String nombre)` y query para verificar productos asociados
    - Completar mapper y adaptador correspondientes
    - _Requisitos: 1.1, 1.6, 1.7_

  - [x] 5.3 Completar `ICategoriaUseCase` y `CategoriaUseCaseImpl`
    - Métodos: `crearCategoria`, `actualizarCategoria`, `buscarPorId`, `listarCategorias`, `eliminarCategoria`
    - Verificar unicidad de nombre → `ConflictoUnicoException` (409)
    - Verificar productos asociados antes de eliminar → `ReglaNegocioException` (422)
    - Llamar a `logAuditoriaUseCase.registrar(...)` en crear, actualizar y eliminar
    - _Requisitos: 1.1 al 1.8_

  - [x] 5.4 Completar DTOs y `CategoriaController`
    - Crear `CategoriaRequestDto` (`@NotBlank nombre`, `@NotNull @DecimalMin("0") margenGananciaPct`, `Boolean activo`)
    - Crear `CategoriaResponseDto` y completar `ICategoriaDtoMapper`
    - Completar `CategoriaController` con POST (201), PUT `/{id}` (200), GET `/{id}` (200), GET (200), DELETE `/{id}` (204)
    - _Requisitos: 1.1 al 1.7_

- [x] 6. Módulo Productos
  - [x] 6.1 Completar entidad de dominio `Producto` y repositorio de dominio
    - Completar `dominio/entidades/Producto.java` con todos los campos del diseño: `productoId`, `nombre`, `marca`, `tipo`, `descripcion`, `costoPromedio`, `margenGanancia`, `precioVenta`, `precioPersonalizado`, `stockActual`, `stockMinimo`, `visibleSinStock`, `origenIdentificacion`
    - Completar `IProductoRepositorio.java` con métodos: `guardar`, `buscarPorId`, `listarTodos`, `eliminar`, `existePorNombre`, `buscarConFiltros(nombre, marca, tipo, categoriaId)`
    - _Requisitos: 2.1, 2.4, 2.5, 2.6_

  - [x] 6.2 Completar entidad JPA, mapper JPA, adaptador y repositorio Spring Data para Producto
    - Completar `ProductoEntity.java` con `@Entity`, `@Table(name="producto")`, `@ManyToOne` hacia `CategoriaEntity` y `NegocioEntity`, todos los campos con `@Column`
    - Completar `IProductoJpaRepositorio.java` con `existsByNombre`, `findAll(Specification)` o query con filtros
    - Completar mapper y adaptador correspondientes
    - _Requisitos: 2.1, 2.8_

  - [x] 6.3 Completar `IProductoUseCase` y `ProductoUseCaseImpl` con lógica de precio
    - Implementar cálculo de `precioVenta = costoPromedio * (1 + margenGanancia / 100)` cuando `precioPersonalizado = false`
    - Cuando `precioPersonalizado = true`, almacenar el `precioVenta` provisto sin recalcular
    - Validar `costoPromedio > 0` lanzando `ReglaNegocioException` (400)
    - Verificar unicidad de nombre → `ConflictoUnicoException` (409)
    - Llamar a `logAuditoriaUseCase.registrar(...)` en crear, actualizar y eliminar
    - _Requisitos: 2.1, 2.2, 2.3, 2.7, 2.8, 2.9, 2.10_

  - [x] 6.4 Completar DTOs y `ProductoController`
    - Crear `ProductoRequestDto` con todas las validaciones Jakarta (`@NotBlank`, `@DecimalMin`)
    - Crear `ProductoResponseDto` y completar `IProductoDtoMapper`
    - Completar `ProductoController` con POST (201), PUT `/{id}` (200), GET `/{id}` (200), GET (200), GET `/buscar` con `@RequestParam` opcionales, DELETE `/{id}` (204)
    - _Requisitos: 2.1 al 2.9_

  - [ ]* 6.5 Escribir test de propiedad: Cálculo de precio de venta
    - **Propiedad 1: Para cualquier costoPromedio > 0 y margenGanancia >= 0 con precioPersonalizado = false, precioVenta = costoPromedio * (1 + margenGanancia / 100)**
    - Usar `@Property(tries = 100)` con `@ForAll @Positive BigDecimal costoPromedio`
    - **Valida: Requisito 2.1**

  - [ ]* 6.6 Escribir test de propiedad: Precio personalizado no se recalcula
    - **Propiedad 2: Para cualquier producto con precioPersonalizado = true, precioVenta almacenado == precioVenta provisto**
    - **Valida: Requisito 2.2**

  - [ ]* 6.7 Escribir test de propiedad: Filtros de búsqueda de productos
    - **Propiedad 3: Todos los productos retornados satisfacen simultáneamente todos los filtros provistos**
    - **Valida: Requisito 2.6**

- [x] 7. Módulo Proveedores
  - [x] 7.1 Completar entidad de dominio `Proveedor` y repositorio de dominio
    - Completar `dominio/entidades/Proveedor.java` con campos: `proveedorId` (Long), `ruc`, `razonSocial`, `direccion`, `telefono`, `email`
    - Completar `IProveedorRepositorio.java` con métodos: `guardar`, `buscarPorId`, `listarTodos`, `existePorRuc`
    - _Requisitos: 3.1, 3.3, 3.4_

  - [x] 7.2 Completar entidad JPA, mapper JPA, adaptador y repositorio Spring Data para Proveedor
    - Completar `ProveedorEntity.java` con `@Entity`, `@Table(name="proveedor")`, todos los campos
    - Completar `IProveedorJpaRepositorio.java` con `existsByRuc(String ruc)`
    - Completar mapper y adaptador
    - _Requisitos: 3.1, 3.5_

  - [x] 7.3 Completar `IProveedorUseCase` y `ProveedorUseCaseImpl`
    - Validar formato RUC con regex `\d{13}` lanzando `ReglaNegocioException` (400)
    - Verificar unicidad de RUC → `ConflictoUnicoException` (409)
    - Llamar a `logAuditoriaUseCase.registrar(...)` en crear y actualizar
    - _Requisitos: 3.1, 3.2, 3.5, 3.6, 3.7_

  - [x] 7.4 Completar DTOs y `ProveedorController`
    - Crear `ProveedorRequestDto` con `@Pattern(regexp="\\d{13}") ruc`, `@NotBlank razonSocial`, `@Email email`
    - Crear `ProveedorResponseDto` y completar `IProveedorDtoMapper`
    - Completar `ProveedorController` con POST (201), PUT `/{id}` (200), GET `/{id}` (200), GET (200)
    - _Requisitos: 3.1 al 3.6_

  - [ ]* 7.5 Escribir test de propiedad: Validación de formato RUC
    - **Propiedad 4: Cualquier string que no sea exactamente 13 dígitos numéricos debe ser rechazado con HTTP 400**
    - Probar también para `NegocioRequestDto`
    - **Valida: Requisitos 3.6, 11.5**

- [x] 8. Módulo Roles y Usuarios (base de autenticación)
  - [x] 8.1 Completar entidad de dominio `Rol` y repositorio de dominio
    - Completar `dominio/entidades/Rol.java` con campos: `rolId` (Integer), `nombre`, `descripcion`
    - Completar `IRolRepositorio.java` con métodos: `guardar`, `buscarPorId`, `listarTodos`, `existePorNombre`
    - _Requisitos: 9.1, 9.2, 9.3_

  - [x] 8.2 Completar entidad JPA, mapper, adaptador y repositorio Spring Data para Rol
    - Completar `RolEntity.java` con `@Entity`, `@Table(name="rol")`
    - Completar `IRolJpaRepositorio.java` con `existsByNombre(String nombre)`
    - Completar mapper y adaptador
    - _Requisitos: 9.1, 9.6_

  - [x] 8.3 Completar entidad de dominio `Usuario` y repositorio de dominio
    - Completar `dominio/entidades/Usuario.java` con todos los campos del diseño (UUID, nombres, apellidos, email, passwordHash, proveedorSso, ssoSubjectId, estadoCuenta, activadoEn, creadoEn, actualizadoEn, List<Rol> roles)
    - Completar `IUsuarioRepositorio.java` con métodos: `guardar`, `buscarPorId`, `listarConFiltro(estadoCuenta)`, `existePorEmail`, `asignarRol`, `revocarRol`
    - _Requisitos: 8.1, 8.4, 8.5, 9.4, 9.5_

  - [x] 8.4 Completar entidad JPA, mapper, adaptador y repositorio Spring Data para Usuario
    - Completar `UsuarioEntity.java` con `@Entity`, `@Table(name="usuario")`, relación `@ManyToMany` con `RolEntity` usando tabla `usuario_rol`
    - Completar `IUsuarioJpaRepositorio.java` con `existsByEmail` y `findByEstadoCuenta`
    - Completar mapper y adaptador
    - _Requisitos: 8.1, 8.7, 9.4_

  - [x] 8.5 Completar `IRolUseCase` y `RolUseCaseImpl`
    - Métodos: `crearRol`, `actualizarRol`, `listarRoles`, `asignarRolAUsuario`, `revocarRolDeUsuario`
    - Verificar unicidad de nombre → `ConflictoUnicoException` (409)
    - Verificar existencia de usuarioId y rolId → `RecursoNoEncontradoException` (404)
    - _Requisitos: 9.1 al 9.7_

  - [x] 8.6 Completar `IUsuarioUseCase` y `UsuarioUseCaseImpl`
    - Métodos: `crearUsuario`, `activarUsuario`, `desactivarUsuario`, `buscarPorId`, `listarConFiltro`, `actualizarUsuario`
    - Validar formato email lanzando `ReglaNegocioException` (400) si inválido
    - Verificar unicidad email → `ConflictoUnicoException` (409)
    - En creación: UUID autogenerado, estadoCuenta = PENDIENTE; si SSO, omitir passwordHash
    - Llamar a `logAuditoriaUseCase.registrar(...)` en crear, activar y desactivar
    - _Requisitos: 8.1 al 8.10_

  - [x] 8.7 Completar DTOs y controllers de Rol y Usuario
    - Crear `RolRequestDto`, `RolResponseDto`, `IRolDtoMapper`
    - Crear `UsuarioRequestDto` (sin `@NotNull` en passwordHash — opcional si SSO), `UsuarioResponseDto` (SIN campo passwordHash), `IUsuarioDtoMapper`
    - Completar `RolController` con POST, PUT `/{id}`, GET, POST `/{rolId}/usuarios/{usuarioId}`, DELETE `/{rolId}/usuarios/{usuarioId}`
    - Completar `UsuarioController` con POST, PATCH `/{id}/activar`, PATCH `/{id}/desactivar`, GET `/{id}`, GET con `?estadoCuenta`, PUT `/{id}`
    - _Requisitos: 8.1 al 8.9, 9.1 al 9.7_

  - [ ]* 8.8 Escribir test de propiedad: PasswordHash nunca se expone
    - **Propiedad 16: Para cualquier usuario con passwordHash no nulo, el UsuarioResponseDto nunca contiene passwordHash**
    - **Valida: Requisito 8.4**

  - [ ]* 8.9 Escribir test de propiedad: Filtro de usuarios por estado
    - **Propiedad 17: Todos los usuarios retornados tienen el estadoCuenta solicitado**
    - **Valida: Requisito 8.5**

  - [ ]* 8.10 Escribir test de propiedad: Validación de formato de email
    - **Propiedad 18: Cualquier string sin formato email válido debe rechazarse con HTTP 400**
    - **Valida: Requisito 8.8**

- [x] 9. Módulo CodigosAcceso
  - [x] 9.1 Completar entidad de dominio `CodigoAcceso` y repositorio de dominio
    - Completar `dominio/entidades/CodigoAcceso.java` con campos: `codigoAccesoId` (UUID), `tipoCodigo`, `codigoHash`, `expiraEn` (OffsetDateTime), `usado` (Boolean), `usadoEn` (OffsetDateTime)
    - Completar `ICodigoAccesoRepositorio.java` con métodos: `guardar`, `buscarPorHash(String codigoHash)`
    - _Requisitos: 10.1, 10.2, 10.3_

  - [x] 9.2 Completar entidad JPA, mapper, adaptador y repositorio Spring Data para CodigoAcceso
    - Completar `CodigoAccesoEntity.java` con `@Entity`, `@Table(name="codigo_acceso")`, `@ManyToOne` a `UsuarioEntity`
    - Completar `ICodigoAccesoJpaRepositorio.java` con `findByCodigoHash(String hash)`
    - Completar mapper y adaptador
    - _Requisitos: 10.1_

  - [x] 9.3 Completar `ICodigoAccesoUseCase` y `CodigoAccesoUseCaseImpl`
    - Generación: `codigoHash = UUID.randomUUID().toString()`, `expiraEn = now + 24h`, `usado = false`
    - Validación: verificar `!usado` y `expiraEn > now`; si `usado = true` lanzar error 410; si expirado lanzar error 410
    - Al validar exitosamente: `usado = true`, registrar `usadoEn`
    - Llamar a `logAuditoriaUseCase.registrar(...)` en generación y uso
    - _Requisitos: 10.1 al 10.6_

  - [x] 9.4 Completar DTOs y `CodigoAccesoController`
    - Crear `CodigoAccesoRequestDto` (`@NotBlank tipoCodigo`, `@NotNull UUID usuarioId`)
    - Crear `ValidarCodigoRequestDto` (`@NotBlank String codigoHash`)
    - Crear `CodigoAccesoResponseDto` (sin codigoHash expuesto en generación — solo en el response inicial)
    - Completar `CodigoAccesoController` con POST `/api/v1/codigos-acceso` (201) y POST `/api/v1/codigos-acceso/validar` (200, 410)
    - _Requisitos: 10.1 al 10.5_

  - [ ]* 9.5 Escribir test de propiedad: CodigoAcceso expira en 24 horas exactas
    - **Propiedad 19: Para cualquier CodigoAcceso generado, expiraEn ∈ [ahora+23h59m, ahora+24h01m] y usado = false**
    - **Valida: Requisito 10.1**

  - [ ]* 9.6 Escribir test de propiedad: Validación de código no-expirado y no-usado
    - **Propiedad 20: Para cualquier CodigoAcceso con usado=false y expiraEn > ahora, la validación retorna éxito**
    - **Valida: Requisito 10.2**

  - [ ]* 9.7 Escribir test de propiedad: Código marcado como usado tras validación
    - **Propiedad 21: Después de validar exitosamente, usado = true**
    - **Valida: Requisito 10.3**

- [x] 10. Checkpoint — Infraestructura base y módulos auxiliares completos
  - Asegurarse de que compila sin errores: `./mvnw compile`
  - Verificar que `GlobalExceptionHandler` retorna JSON uniforme para cada tipo de excepción
  - Verificar que `SecuenciaCodigoUseCaseImpl` lanza `ServicioNoDisponibleException` tras 3 intentos fallidos
  - Asegurarse de que todos los tests pasen, consultar al usuario si hay dudas.

- [x] 11. Módulo TiposMovimiento
  - [x] 11.1 Completar entidad de dominio `TipoMovimiento`, repositorio de dominio, entidad JPA, mapper, adaptador y repositorio Spring Data
    - Campos: `tipoMovimientoId` (Integer), `codigo`, `prefijoCodigo`, `descripcion`
    - Repositorio: `guardar`, `buscarPorId`, `listarTodos`, `existePorCodigo`
    - JPA: `@Entity`, `@Table(name="tipo_movimiento")`, `existsByCodigo`
    - _Requisitos: 7.1, 7.2, 7.3, 7.4_

  - [x] 11.2 Completar `ITipoMovimientoUseCase`, `TipoMovimientoUseCaseImpl`, DTOs y controller
    - Verificar unicidad de `codigo` → `ConflictoUnicoException` (409)
    - Crear `TipoMovimientoRequestDto`, `TipoMovimientoResponseDto`, `ITipoMovimientoDtoMapper`
    - Completar `TipoMovimientoController` con POST (201), GET (200), GET `/{id}` (200)
    - _Requisitos: 7.1 al 7.4_

- [x] 12. Módulo Alertas
  - [x] 12.1 Completar entidad de dominio `Alerta` y repositorio de dominio
    - Completar `dominio/entidades/Alerta.java` con campos: `alertaId` (Long), `tipo`, `mensaje`, `leido` (Boolean), `creadoEn` (OffsetDateTime)
    - Completar `IAlertaRepositorio.java` con métodos: `guardar`, `buscarPorId`, `listarConFiltros(tipo, leido)`, `contarNoLeidas`
    - _Requisitos: 12.1, 12.2, 12.3, 12.4, 12.5_

  - [x] 12.2 Completar entidad JPA, mapper, adaptador y repositorio Spring Data para Alerta
    - Completar `AlertaEntity.java` con `@Entity`, `@Table(name="alerta")`
    - Completar `IAlertaJpaRepositorio.java` con `findByTipoAndLeido`, `countByLeidoFalse`, ordenado por `creadoEn` desc
    - Completar mapper y adaptador
    - _Requisitos: 12.3, 12.5_

  - [x] 12.3 Completar `IAlertaUseCase` y `AlertaUseCaseImpl`
    - Métodos: `crearAlertaStockBajo(Producto p)`, `crearAlertaVencimientoProximo(LoteProducto l)`, `listarConFiltros(tipo, leido)`, `marcarComoLeida(Long id)`, `contarNoLeidas()`
    - Mensaje STOCK_BAJO: `"El producto [nombre] tiene stock [stockActual], igual o por debajo del mínimo [stockMinimo]"`
    - `marcarComoLeida` lanza `RecursoNoEncontradoException` si alertaId no existe
    - _Requisitos: 12.1, 12.2, 12.3, 12.4, 12.5, 12.6_

  - [x] 12.4 Completar DTOs y `AlertaController`
    - Crear `AlertaResponseDto` y `IAlertaDtoMapper`
    - Completar `AlertaController` con GET `/api/v1/alertas` (filtros `tipo`, `leido`), PATCH `/{id}/leer` (200, 404), GET `/no-leidas/conteo` (200)
    - _Requisitos: 12.3, 12.4, 12.5, 12.6_

  - [ ]* 12.5 Escribir test de propiedad: Filtros de alertas y orden cronológico inverso
    - **Propiedad 23: Todos los resultados satisfacen filtros tipo/leido y están ordenados por creadoEn descendente**
    - **Valida: Requisito 12.3**

  - [ ]* 12.6 Escribir test de propiedad: Conteo de alertas no leídas es exacto
    - **Propiedad 24: El endpoint de conteo retorna exactamente el número de alertas con leido = false**
    - **Valida: Requisito 12.5**

- [x] 13. Módulo Lotes de Producto
  - [x] 13.1 Completar entidad de dominio `LoteProducto` y repositorio de dominio
    - Completar `dominio/entidades/LoteProducto.java` con campos: `loteId` (Long), `codigoEntrada`, `cantidadInicial` (BigDecimal), `cantidadDisponible` (BigDecimal), `precioCosto` (BigDecimal), `fechaIngreso` (OffsetDateTime), `fechaVencimiento` (LocalDate)
    - Completar `ILoteProductoRepositorio.java` con métodos: `guardar`, `buscarPorId`, `buscarPorProductoOrdenadoPorFechaIngreso`, `buscarProximosAVencer(LocalDate limite)`, `buscarPorLoteId`
    - _Requisitos: 5.1, 5.2, 5.4_

  - [x] 13.2 Completar entidad JPA, mapper, adaptador y repositorio Spring Data para LoteProducto
    - Completar `LoteProductoEntity.java` con `@Entity`, `@Table(name="lote_producto")`, `@ManyToOne` a `ProductoEntity`
    - Completar `ILoteProductoJpaRepositorio.java` con `findByProductoIdOrderByFechaIngreso`, `findProximosAVencer(@Param("limite") LocalDate limite)` con `@Query`
    - Completar mapper y adaptador
    - _Requisitos: 5.2, 5.4_

  - [x] 13.3 Completar `ILoteProductoUseCase` y `LoteProductoUseCaseImpl`
    - Creación: `cantidadDisponible = cantidadInicial`, `fechaIngreso = now()`, `codigoEntrada = "LOTE-" + format("%08d", secuenciaUseCase.siguiente("LOTE"))`
    - Anotar método de creación con `@Transactional`
    - Validar `cantidadInicial > 0` → `ReglaNegocioException` (400); verificar `productoId` → `RecursoNoEncontradoException` (404)
    - _Requisitos: 5.1, 5.3, 5.5, 5.6_

  - [x] 13.4 Completar DTOs y `LoteProductoController`
    - Crear `LoteProductoRequestDto`, `LoteProductoResponseDto`, `ILoteProductoDtoMapper`
    - Completar `LoteProductoController` con POST (201), GET `/producto/{productoId}` (200), GET `/{id}` (200), GET `/proximos-vencer?dias=7` (200)
    - _Requisitos: 5.1 al 5.4_

  - [ ]* 13.5 Escribir test de propiedad: Invariante de creación de lote
    - **Propiedad 8: Para cualquier LoteProducto recién creado, cantidadDisponible = cantidadInicial**
    - **Valida: Requisito 5.1**

  - [ ]* 13.6 Escribir test de propiedad: Ordenamiento FIFO de lotes
    - **Propiedad 9: Para cualquier conjunto de lotes de un producto, el resultado está ordenado por fechaIngreso[i] <= fechaIngreso[i+1]**
    - **Valida: Requisito 5.2**

  - [ ]* 13.7 Escribir test de propiedad: Filtro de lotes próximos a vencer
    - **Propiedad 10: Todos los lotes retornados tienen fechaVencimiento <= hoy+N días y cantidadDisponible > 0**
    - **Valida: Requisito 5.4**

- [x] 14. Módulo Movimientos de Inventario
  - [x] 14.1 Completar entidad de dominio `MovimientoInventario` y repositorio de dominio
    - Completar `dominio/entidades/MovimientoInventario.java` con campos: `movimientoId` (Long), `codigoMovimiento`, `cantidad` (BigDecimal), `precioUnitario` (BigDecimal), `creadoEn` (OffsetDateTime)
    - Completar `IMovimientoInventarioRepositorio.java` con métodos: `guardar`, `buscarPorProductoConFiltros(productoId, tipo, desde, hasta)`
    - _Requisitos: 6.1, 6.2, 6.3, 6.4_

  - [x] 14.2 Completar entidad JPA, mapper, adaptador y repositorio Spring Data para MovimientoInventario
    - Completar `MovimientoInventarioEntity.java` con `@Entity`, relaciones `@ManyToOne` a `ProductoEntity`, `LoteProductoEntity` y `TipoMovimientoEntity`
    - Completar `IMovimientoInventarioJpaRepositorio.java` con query de filtros multi-campo y orden por `creadoEn desc`
    - Completar mapper y adaptador
    - _Requisitos: 6.4_

  - [x] 14.3 Completar `IMovimientoInventarioUseCase` y `MovimientoInventarioUseCaseImpl`
    - Lógica ENTRADA: generar código `{prefijoCodigo}{n:08d}`, incrementar `producto.stockActual += cantidad`
    - Lógica SALIDA (dentro de `@Transactional`): verificar `lote.cantidadDisponible >= cantidad` → `ReglaNegocioException` (422); decrementar `lote.cantidadDisponible` y `producto.stockActual`
    - Lógica AJUSTE: `producto.stockActual += cantidad` (positivo o negativo)
    - Post-movimiento: si `stockActual <= stockMinimo` llamar `alertaUseCase.crearAlertaStockBajo(producto)`
    - Llamar a `logAuditoriaUseCase.registrar(...)` para cada movimiento creado
    - _Requisitos: 6.1 al 6.8_

  - [x] 14.4 Completar DTOs y `MovimientoInventarioController`
    - Crear `MovimientoRequestDto` con `@NotNull productoId`, `loteId`, `@NotNull tipoMovimientoId`, `@NotNull @DecimalMin(value="0",inclusive=false) cantidad`, `precioUnitario`
    - Crear `MovimientoResponseDto` y `IMovimientoDtoMapper`
    - Completar `MovimientoInventarioController` con POST `/api/v1/movimientos` (201), GET `/api/v1/movimientos/producto/{productoId}` con `@RequestParam` opcionales
    - _Requisitos: 6.1 al 6.7_

  - [ ]* 14.5 Escribir test de propiedad: Movimiento ENTRADA incrementa stock
    - **Propiedad 11: Para stockActual=S y entrada de cantidad=C>0, después stockActual = S+C**
    - **Valida: Requisito 6.1**

  - [ ]* 14.6 Escribir test de propiedad: Movimiento SALIDA decrementa stock y lote simultáneamente
    - **Propiedad 12: Para cantidadDisponible=D, stockActual=S y salida válida C (0<C<=D), después cantidadDisponible=D-C y stockActual=S-C**
    - **Valida: Requisito 6.2**

  - [ ]* 14.7 Escribir test de propiedad: Movimiento AJUSTE actualiza stock con cantidad signed
    - **Propiedad 13: Para stockActual=S y ajuste cantidad=C (positivo o negativo), después stockActual=S+C**
    - **Valida: Requisito 6.3**

  - [ ]* 14.8 Escribir test de propiedad: Filtros de movimientos son correctos y ordenados
    - **Propiedad 14: Todos los movimientos retornados satisfacen filtros de tipo/rango de fechas y están ordenados por creadoEn descendente**
    - **Valida: Requisito 6.4**

  - [ ]* 14.9 Escribir test de propiedad: Alerta STOCK_BAJO se genera post-movimiento
    - **Propiedad 15: Si stockActual <= stockMinimo después del movimiento, debe existir al menos una Alerta de tipo STOCK_BAJO para ese producto**
    - **Valida: Requisitos 6.5, 12.1**

- [x] 15. Módulo Órdenes de Compra (EstadoOc + OrdenCompra + DetalleOrdenCompra)
  - [x] 15.1 Completar entidades de dominio `EstadoOc`, `OrdenCompra` y `DetalleOrdenCompra`
    - Completar `EstadoOc.java` con campos: `estadoOcId` (Integer), `nombre`
    - Completar `OrdenCompra.java` con campos del diseño: `ordenCompraId`, `codigoReferencia`, `estado`, `total` (Double), `creadoEn` (OffsetDateTime)
    - Completar `DetalleOrdenCompra.java` con campos: `detalleOrdenCompraId`, `ordenCompraId`, `cantidad` (Integer), `precioUnitario` (Double)
    - Completar repositorios de dominio correspondientes (`IOrdenCompraRepositorio`, `IDetalleOrdenCompraRepositorio`)
    - _Requisitos: 4.1, 4.6_

  - [x] 15.2 Completar entidades JPA, mappers, adaptadores y repositorios Spring Data para OC
    - Completar `OrdenCompraEntity.java` con `@Entity`, `@ManyToOne` a `ProveedorEntity` y `EstadoOcEntity`, `@OneToMany` a `DetalleOrdenCompraEntity`
    - Completar `DetalleOrdenCompraEntity.java` con `@Entity`, `@ManyToOne` a `OrdenCompraEntity` y `ProductoEntity`
    - Completar `IOrdenCompraJpaRepositorio.java` con query de filtros `estado` + rango de fechas
    - Completar mappers y adaptadores
    - _Requisitos: 4.7_

  - [x] 15.3 Completar `IOrdenCompraUseCase` y `OrdenCompraUseCaseImpl`
    - Creación: verificar `productoId` de cada detalle → `RecursoNoEncontradoException` (404); calcular `total = sum(cantidad * precioUnitario)`; generar `codigoReferencia = "OC-" + format("%08d", siguiente("OC"))`; estado inicial BORRADOR; anotar con `@Transactional`
    - `enviarOrden`: verificar estado BORRADOR → ENVIADA; si no, `ReglaNegocioException` (422)
    - `recibirOrden` (`@Transactional`): verificar estado ENVIADA; transicionar a RECIBIDA; crear `LoteProducto` por cada detalle; incrementar `stockActual` de cada producto; llamar a `logAuditoriaUseCase.registrar(...)`
    - `anularOrden`: verificar estado BORRADOR o ENVIADA → ANULADA
    - `actualizarOrden`: solo si estado BORRADOR; recalcular total
    - _Requisitos: 4.1 al 4.11_

  - [x] 15.4 Completar DTOs y `OrdenCompraController`
    - Crear `OrdenCompraRequestDto`, `DetalleOrdenCompraRequestDto`, `OrdenCompraResponseDto`, `IOrdenCompraDtoMapper`
    - Completar `OrdenCompraController` con POST (201), PUT `/{id}` (200), PATCH `/{id}/enviar` (200), PATCH `/{id}/recibir` (200), PATCH `/{id}/anular` (200), GET `/{id}` (200), GET con filtros `estado`, `desde`, `hasta`
    - _Requisitos: 4.1 al 4.10_

  - [ ]* 15.5 Escribir test de propiedad: Total de orden de compra
    - **Propiedad 5: Para cualquier lista de detalles con cantidades y precios positivos, total = sum(cantidad * precioUnitario)**
    - **Valida: Requisito 4.1**

  - [ ]* 15.6 Escribir test de propiedad: Recepción de OC incrementa stock correctamente
    - **Propiedad 6: Al recibir OC con N detalles, stockActual de cada producto se incrementa exactamente en la cantidad del detalle**
    - **Valida: Requisito 4.4**

  - [ ]* 15.7 Escribir test de propiedad: Filtros de listado de órdenes son correctos
    - **Propiedad 7: Todos los resultados cumplen los filtros de estado y/o rango de fechas provistos**
    - **Valida: Requisito 4.7**

- [x] 16. Checkpoint — Módulos de negocio core completos
  - Asegurarse de que compila sin errores: `./mvnw compile`
  - Verificar que los tests de propiedad de Productos, Lotes y Movimientos pasan: `./mvnw test -Dtest="*PropiedadTest"`
  - Verificar que las transacciones de `recibirOrden` y `registrarMovimientoSalida` funcionan correctamente
  - Asegurarse de que todos los tests pasen, consultar al usuario si hay dudas.

- [x] 17. Módulo IA (IdentificacionIa + ConsumoIaMensual + TokensIaNegocio)
  - [x] 17.1 Completar entidades de dominio y repositorios para IA
    - Completar `IdentificacionIa.java` con campos: `identificacionIaId` (Long), `nombreModelo`, `probabilidad` (Double), `resultado`, `creadoEn` (OffsetDateTime)
    - Completar `ConsumoIaMensual.java` con campos: `consumoId` (Long), `mes` (Integer), `anio` (Integer), `tokensUsados` (Long)
    - Completar `TokensIaNegocio.java` con campos: `tokensIaId` (Long), `limiteTokens` (Long)
    - Completar repositorios: `IIdentificacionIaRepositorio`, `IConsumoIaMensualRepositorio` (con `findByNegocioYMes`), `ITokensIaNegocioRepositorio` (con `findByNegocioId`)
    - _Requisitos: 14.1, 14.4, 14.5_

  - [x] 17.2 Completar entidades JPA, mappers, adaptadores y repositorios Spring Data para IA
    - Completar `IdentificacionIaEntity.java` con `@ManyToOne` a `ProductoEntity` y `NegocioEntity`
    - Completar `ConsumoIaMensualEntity.java` con `@ManyToOne` a `NegocioEntity`
    - Completar `TokensIaNegocioEntity.java` con `@OneToOne` a `NegocioEntity`
    - Completar los 3 repos JPA, mappers y adaptadores correspondientes
    - _Requisitos: 14.4, 14.5_

  - [x] 17.3 Completar `IIdentificacionIaUseCase` y `IdentificacionIaUseCaseImpl`
    - Antes de identificar: verificar `tokensUsados < limiteTokens` → `CuotaIaExcedidaException` (429)
    - Clasificar confianza: `probabilidad >= 0.80` → `"ALTA"`, si no `"BAJA"`
    - Después de identificar: incrementar `tokensUsados` en `ConsumoIaMensual`, crear registro en primera vez
    - Validar formato de imagen: solo JPEG, PNG, WEBP → `ReglaNegocioException` (400)
    - _Requisitos: 14.1 al 14.7_

  - [x] 17.4 Completar DTOs y `IdentificacionIaController`
    - Crear `IdentificacionIaRequestDto` (`@NotBlank imagenBase64`, `formatoImagen`)
    - Crear `IdentificacionIaResponseDto` (incluye `nivelConfianza`)
    - Completar `IdentificacionIaController` con POST `/api/v1/ia/identificar` (201, 400, 429) y GET `/api/v1/ia/historial` (filtros `productoId`, `desde`, `hasta`)
    - _Requisitos: 14.1, 14.6, 14.7_

  - [ ]* 17.5 Escribir test de propiedad: Clasificación de confianza de IA
    - **Propiedad 27: Para cualquier Double p en [0.0,1.0], si p >= 0.80 confianza = "ALTA", si p < 0.80 confianza = "BAJA"**
    - Usar `@ForAll @DoubleRange(min=0.0, max=1.0) double probabilidad`
    - **Valida: Requisitos 14.2, 14.3**

  - [ ]* 17.6 Escribir test de propiedad: Acumulación de tokens de IA
    - **Propiedad 28: Para tokensUsados=T antes y consumo K, después tokensUsados = T+K**
    - **Valida: Requisito 14.4**

  - [ ]* 17.7 Escribir test de propiedad: Cuota de IA rechaza cuando consumo >= límite
    - **Propiedad 29: Para cualquier negocio con tokensUsados >= limiteTokens, la solicitud debe rechazarse con HTTP 429**
    - **Valida: Requisito 14.5**

- [x] 18. Scheduler de alertas por vencimiento
  - [x] 18.1 Crear `AlertaScheduler` en `infraestructura/configuracion/`
    - Crear `AlertaScheduler.java` con `@Component`, `@EnableScheduling` en clase de configuración Spring
    - Implementar `@Scheduled(cron = "0 0 7 * * *") void verificarVencimientos()` que consulta `loteRepositorio.buscarProximosAVencer(LocalDate.now().plusDays(7))` y llama `alertaUseCase.crearAlertaVencimientoProximo(lote)` por cada resultado
    - _Requisitos: 12.2_

  - [ ]* 18.2 Escribir test de propiedad: Alerta VENCIMIENTO_PROXIMO cubre todos los lotes elegibles
    - **Propiedad 22: Después de ejecutar la verificación, existe una Alerta de tipo VENCIMIENTO_PROXIMO por cada lote con fechaVencimiento <= hoy+7 días y cantidadDisponible > 0**
    - **Valida: Requisito 12.2**

- [ ] 19. Tests de propiedad restantes (SecuenciaCodigo, LogAuditoria, errores uniformes)
  - [x] 19.1 Escribir test de propiedad: Atomicidad de secuencia de códigos bajo concurrencia
    - **Propiedad 30: Para N invocaciones concurrentes a ISecuenciaCodigoUseCase.siguiente(tipo), los N valores retornados deben ser todos distintos**
    - Lanzar N threads en paralelo con `ExecutorService`, recolectar resultados en un `Set`, verificar `Set.size() == N`
    - **Valida: Requisito 15.1**

  - [x] 19.2 Escribir test de propiedad: Formato de código de movimiento
    - **Propiedad 31: Para cualquier prefijoCodigo y número n, codigoMovimiento = prefijoCodigo + String.format("%08d", n)**
    - **Valida: Requisitos 15.2, 15.3, 15.4**

  - [x] 19.3 Escribir test de propiedad: Estructura de respuesta de error uniforme
    - **Propiedad 32: Para cualquier error HTTP (400, 404, 409, 410, 422, 429, 500, 503), el JSON contiene timestamp, status, error y message**
    - Usar `@WebMvcTest` con mocks que lanzan cada excepción
    - **Valida: Requisito 16.4**

  - [x] 19.4 Escribir test de propiedad: LogAuditoria contiene todos los campos requeridos
    - **Propiedad 25: Para cualquier operación de escritura sobre entidades auditadas, el LogAuditoria persistido tiene entidad no nulo, entidadId no nulo, accion en {CREAR,ACTUALIZAR,ELIMINAR}, detalle en JSON válido, creadoEn no nulo**
    - **Valida: Requisitos 13.1, 13.2**

  - [x] 19.5 Escribir test de propiedad: Filtros de LogAuditoria son correctos
    - **Propiedad 26: Todos los resultados de consulta satisfacen los filtros de entidad, accion y rango de fechas**
    - **Valida: Requisito 13.3**

  - [x] 19.6 Escribir test de propiedad: Auditoría completa en todas las escrituras
    - **Propiedad 33: Para cualquier operación de creación/actualización/eliminación sobre Producto, Categoria, Proveedor, OrdenCompra, MovimientoInventario, Usuario, CodigoAcceso o Negocio, siempre existe un LogAuditoria con entidadId correcto y accion correcta**
    - **Valida: Requisitos 1.8, 2.10, 3.7, 4.11, 6.8, 8.10, 10.6, 11.7, 13.1**

- [x] 20. Tests de integración con Testcontainers
  - [x] 20.1 Configurar base de tests de integración con `@SpringBootTest` + Testcontainers
    - Crear clase base `IntegracionBaseTest.java` con `@SpringBootTest(webEnvironment=RANDOM_PORT)`, `@Testcontainers` y `@Container PostgreSQLContainer`
    - Configurar `application-test.yml` con datasource apuntando al container
    - _Requisitos: 16.1, 16.2_

  - [x] 20.2 Test de integración: Atomicidad de `recibirOrden`
    - Verificar que en una sola transacción se actualiza estado OC → RECIBIDA, se crean N LoteProducto y se incrementa stockActual de cada producto
    - Verificar rollback completo si falla cualquier parte (simular error en creación de lote)
    - _Requisitos: 4.4, 16.1_

  - [x] 20.3 Test de integración: Atomicidad de movimiento SALIDA
    - Verificar que la creación del movimiento, el decremento de `cantidadDisponible` y el decremento de `stockActual` ocurren en la misma transacción
    - Verificar rollback si `cantidadDisponible < cantidad`
    - _Requisitos: 6.2, 16.2_

  - [x] 20.4 Test de integración: Concurrencia de `SecuenciaCodigo`
    - Lanzar 20 threads concurrentes llamando a `secuenciaCodigoUseCase.siguiente("MOVIMIENTO")`
    - Verificar que los 20 valores retornados son todos distintos (sin duplicados)
    - _Requisitos: 15.1_

  - [x] 20.5 Test de integración: Inmutabilidad de `LogAuditoria`
    - Verificar que no existe endpoint `PUT /api/v1/auditoria/{id}` (HTTP 405)
    - Verificar que no existe endpoint `DELETE /api/v1/auditoria/{id}` (HTTP 405)
    - _Requisitos: 13.5_

- [x] 21. Checkpoint final — Todos los módulos integrados y tests completos
  - Ejecutar suite de tests completa: `./mvnw test`
  - Verificar que los 33 tests de propiedad pasan con mínimo 100 iteraciones cada uno
  - Verificar que los tests de integración con Testcontainers pasan
  - Verificar que el contexto de Spring Boot arranca sin errores de configuración
  - Asegurarse de que todos los tests pasen, consultar al usuario si hay dudas.

## Notes

- Las tareas marcadas con `*` son opcionales (tests de propiedad y tests de integración); pueden omitirse para un MVP más rápido, pero se recomienda ejecutarlas antes de producción.
- El orden de dependencia de módulos es: `Infraestructura base` → `SecuenciaCodigo` → `LogAuditoria` → `Negocio` → `Categorías` → `Roles/Usuarios` → `CodigosAcceso` → `Productos` → `Proveedores` → `TiposMovimiento` → `Alertas` → `Lotes` → `Movimientos` → `OrdenesCompra` → `IA` → `Scheduler`.
- Todos los `*UseCaseImpl` que orquestan múltiples entidades deben estar anotados con `@Transactional`.
- El `GlobalExceptionHandler` se conecta automáticamente a todos los controllers vía `@RestControllerAdvice`.
- Cada `*UseCaseImpl` inyecta `ILogAuditoriaUseCase` para registrar auditoría — no usar AOP para esto, mantener la llamada explícita.
- Los mappers MapStruct (`I*JpaMapper`, `I*DtoMapper`) deben tener `@Mapper(componentModel = "spring")`.
- Los campos Lombok (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`) se usan en entidades JPA y DTOs, pero NO en entidades de dominio puro (solo getters/setters manuales o `@Getter @Setter` de Lombok están permitidos en dominio).
- La propiedad 30 (concurrencia de secuencias) requiere Testcontainers (PostgreSQL real) para ser válida.

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1.1", "1.2", "1.3"] },
    { "id": 1, "tasks": ["2.1", "3.1"] },
    { "id": 2, "tasks": ["2.2", "2.3", "3.2"] },
    { "id": 3, "tasks": ["2.4", "3.3", "3.4"] },
    { "id": 4, "tasks": ["4.1"] },
    { "id": 5, "tasks": ["4.2", "4.3"] },
    { "id": 6, "tasks": ["4.4", "5.1"] },
    { "id": 7, "tasks": ["5.2", "5.3", "8.1", "8.3"] },
    { "id": 8, "tasks": ["5.4", "8.2", "8.4"] },
    { "id": 9, "tasks": ["6.1", "8.5", "8.6", "9.1"] },
    { "id": 10, "tasks": ["6.2", "8.7", "9.2"] },
    { "id": 11, "tasks": ["6.3", "9.3", "11.1"] },
    { "id": 12, "tasks": ["6.4", "9.4", "11.2"] },
    { "id": 13, "tasks": ["6.5", "6.6", "6.7", "7.1", "9.5", "9.6", "9.7"] },
    { "id": 14, "tasks": ["7.2", "7.3", "12.1"] },
    { "id": 15, "tasks": ["7.4", "7.5", "12.2"] },
    { "id": 16, "tasks": ["12.3", "13.1"] },
    { "id": 17, "tasks": ["12.4", "12.5", "12.6", "13.2"] },
    { "id": 18, "tasks": ["13.3", "13.4", "13.5", "13.6", "13.7"] },
    { "id": 19, "tasks": ["14.1"] },
    { "id": 20, "tasks": ["14.2", "14.3"] },
    { "id": 21, "tasks": ["14.4", "14.5", "14.6", "14.7", "14.8", "14.9"] },
    { "id": 22, "tasks": ["15.1"] },
    { "id": 23, "tasks": ["15.2", "15.3"] },
    { "id": 24, "tasks": ["15.4", "15.5", "15.6", "15.7"] },
    { "id": 25, "tasks": ["17.1"] },
    { "id": 26, "tasks": ["17.2", "17.3"] },
    { "id": 27, "tasks": ["17.4", "17.5", "17.6", "17.7", "18.1"] },
    { "id": 28, "tasks": ["18.2", "19.1", "19.2", "19.3", "19.4", "19.5", "19.6"] },
    { "id": 29, "tasks": ["20.1"] },
    { "id": 30, "tasks": ["20.2", "20.3", "20.4", "20.5"] }
  ]
}
```

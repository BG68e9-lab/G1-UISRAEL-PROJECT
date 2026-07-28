-- =====================================================================
-- Script de carga de catalogos base: tipos_movimiento y estados_oc
-- Base de datos: PostgreSQL (drinkhouse_db)
--
-- Por que hace falta:
--   Estas dos tablas son catalogos de referencia (lookup tables) que el
--   backend valida por `codigo` antes de guardar un movimiento de
--   inventario o una orden de compra. No hay ninguna migracion que las
--   siembre (el esquema se crea con ddl-auto=update a partir de las
--   entidades JPA, que solo define columnas, no filas). Si estas tablas
--   estan vacias, CUALQUIER movimiento o cambio de estado de OC falla
--   con un error "invalido" aunque el valor enviado sea correcto.
--
-- Que hace:
--   1. tipos_movimiento: ENTRADA, SALIDA, AJUSTE (con su prefijo para
--      generar codigo_movimiento).
--   2. estados_oc: BORRADOR, ENVIADA, RECIBIDA, ANULADA (deben coincidir
--      exactamente con los strings que usa OrdenCompraUseCaseImpl para
--      la maquina de estados).
--
-- Es seguro volver a correrlo: usa WHERE NOT EXISTS por codigo, asi que
-- no duplica filas si ya existen.
--
-- Uso:
--   psql -U postgres -d drinkhouse_db -f scripts/seed_catalogos.sql
-- =====================================================================

DO $$
BEGIN
    -- ---------------------------------------------------------------
    -- 1) tipos_movimiento
    -- ---------------------------------------------------------------
    IF NOT EXISTS (SELECT 1 FROM tipos_movimiento WHERE codigo = 'ENTRADA') THEN
        INSERT INTO tipos_movimiento (codigo, prefijo_codigo, descripcion)
        VALUES ('ENTRADA', 'ENT', 'Ingreso de stock (compra, ajuste positivo, etc.)');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM tipos_movimiento WHERE codigo = 'SALIDA') THEN
        INSERT INTO tipos_movimiento (codigo, prefijo_codigo, descripcion)
        VALUES ('SALIDA', 'SAL', 'Consumo de stock de un lote existente (venta, merma, etc.)');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM tipos_movimiento WHERE codigo = 'AJUSTE') THEN
        INSERT INTO tipos_movimiento (codigo, prefijo_codigo, descripcion)
        VALUES ('AJUSTE', 'AJU', 'Correccion manual de inventario');
    END IF;

    RAISE NOTICE 'tipos_movimiento listo: %',
        (SELECT string_agg(codigo, ', ') FROM tipos_movimiento);

    -- ---------------------------------------------------------------
    -- 2) estados_oc
    -- ---------------------------------------------------------------
    IF NOT EXISTS (SELECT 1 FROM estados_oc WHERE codigo = 'BORRADOR') THEN
        INSERT INTO estados_oc (codigo, etiqueta) VALUES ('BORRADOR', 'Borrador');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM estados_oc WHERE codigo = 'ENVIADA') THEN
        INSERT INTO estados_oc (codigo, etiqueta) VALUES ('ENVIADA', 'Enviada al proveedor');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM estados_oc WHERE codigo = 'RECIBIDA') THEN
        INSERT INTO estados_oc (codigo, etiqueta) VALUES ('RECIBIDA', 'Recibida');
    END IF;

    IF NOT EXISTS (SELECT 1 FROM estados_oc WHERE codigo = 'ANULADA') THEN
        INSERT INTO estados_oc (codigo, etiqueta) VALUES ('ANULADA', 'Anulada');
    END IF;

    RAISE NOTICE 'estados_oc listo: %',
        (SELECT string_agg(codigo, ', ') FROM estados_oc);
END $$;

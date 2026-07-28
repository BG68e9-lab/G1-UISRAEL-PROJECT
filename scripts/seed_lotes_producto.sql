-- =====================================================================
-- Script de carga de datos de prueba: Lotes de producto
-- Base de datos: PostgreSQL (drinkhouse_db)
--
-- Que hace:
--   Por cada producto activo del negocio (misma logica de negocio que
--   scripts/seed_productos.sql: usa el primer negocio existente), crea
--   UN lote de entrada en `lotes_producto` con:
--     - cantidad_inicial = cantidad_disponible = stock_actual del producto
--     - precio_costo = costo_promedio del producto
--     - fecha_ingreso = hace 15 dias
--     - fecha_vencimiento: solo se asigna a Cerveza (270 dias) y Vino
--       (730 dias); el resto de categorias (whisky, ron, vodka, tequila,
--       licores) se deja en NULL porque no vencen.
--
-- Requisito: correr primero scripts/seed_productos.sql (o tener productos
-- ya cargados para el negocio).
--
-- Es seguro volver a correrlo: no verifica duplicados por codigo_entrada,
-- asi que si lo ejecutas dos veces se generan lotes duplicados (el
-- codigo_entrada se re-genera con un sufijo unico basado en el momento
-- de ejecucion para evitar choques de longitud/duplicado exacto).
--
-- Uso:
--   psql -U postgres -d drinkhouse_db -f scripts/seed_lotes_producto.sql
-- =====================================================================

DO $$
DECLARE
    v_negocio_id   INTEGER;
    r              RECORD;
    v_codigo       TEXT;
    v_fecha_venc   DATE;
    v_contador     INTEGER := 0;
BEGIN
    -- ---------------------------------------------------------------
    -- 1) Negocio (mismo criterio que seed_productos.sql)
    -- ---------------------------------------------------------------
    SELECT negocio_id INTO v_negocio_id FROM negocios ORDER BY negocio_id LIMIT 1;

    IF v_negocio_id IS NULL THEN
        RAISE EXCEPTION 'No existe ningun negocio en la tabla negocios. Corre primero scripts/seed_productos.sql';
    END IF;

    RAISE NOTICE 'Usando negocio_id=%', v_negocio_id;

    -- ---------------------------------------------------------------
    -- 2) Un lote de entrada por cada producto activo del negocio
    -- ---------------------------------------------------------------
    FOR r IN
        SELECT producto_id, nombre, tipo, costo_promedio, stock_actual
        FROM productos
        WHERE negocio_id = v_negocio_id AND activo = true
        ORDER BY producto_id
    LOOP
        v_contador := v_contador + 1;

        -- codigo_entrada tiene limite de 15 caracteres en la tabla
        v_codigo := 'LT' || lpad(r.producto_id::text, 4, '0') || '-' || to_char(now(), 'HH24MISS');
        v_codigo := left(v_codigo, 15);

        -- Solo cerveza y vino tienen fecha de vencimiento en este negocio
        v_fecha_venc := CASE
            WHEN r.tipo = 'Cerveza' THEN (CURRENT_DATE + INTERVAL '270 days')::date
            WHEN r.tipo = 'Vino'    THEN (CURRENT_DATE + INTERVAL '730 days')::date
            ELSE NULL
        END;

        INSERT INTO lotes_producto (
            negocio_id, producto_id, codigo_entrada,
            cantidad_inicial, cantidad_disponible, precio_costo,
            fecha_ingreso, fecha_vencimiento,
            creado_en, usuario_creacion, activo
        ) VALUES (
            v_negocio_id, r.producto_id, v_codigo,
            COALESCE(r.stock_actual, 0), COALESCE(r.stock_actual, 0), r.costo_promedio,
            now() - INTERVAL '15 days', v_fecha_venc,
            now(), 'seed-script', true
        );
    END LOOP;

    RAISE NOTICE 'Carga de lotes de producto completada (% lotes creados).', v_contador;
END $$;

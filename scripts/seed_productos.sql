-- =====================================================================
-- Script de carga de datos de prueba: Productos (licoreria)
-- Base de datos: PostgreSQL (drinkhouse_db)
--
-- Que hace:
--   1. Usa el primer negocio existente en la tabla `negocios`. Si no hay
--      ninguno, crea un negocio de demo ("DrinkHouse Demo").
--   2. Crea (si no existen ya) las categorias tipicas de una licoreria:
--      Whisky, Vodka, Ron, Cerveza, Vino, Tequila, Licores/Cremas.
--   3. Inserta 18 productos de ejemplo distribuidos en esas categorias,
--      con costo, margen de ganancia y precio de venta realistas.
--
-- Es seguro volver a correrlo: las categorias no se duplican (se
-- reutilizan si ya existen para el negocio), aunque los productos SI se
-- insertaran de nuevo cada vez que se ejecute (no hay verificacion de
-- duplicados por nombre).
--
-- Uso:
--   psql -U postgres -d drinkhouse_db -f scripts/seed_productos.sql
-- =====================================================================

DO $$
DECLARE
    v_negocio_id      INTEGER;
    v_cat_whisky      INTEGER;
    v_cat_vodka       INTEGER;
    v_cat_ron         INTEGER;
    v_cat_cerveza     INTEGER;
    v_cat_vino        INTEGER;
    v_cat_tequila     INTEGER;
    v_cat_licor       INTEGER;
BEGIN
    -- ---------------------------------------------------------------
    -- 1) Negocio
    -- ---------------------------------------------------------------
    SELECT negocio_id INTO v_negocio_id FROM negocios ORDER BY negocio_id LIMIT 1;

    IF v_negocio_id IS NULL THEN
        INSERT INTO negocios (nombre, ruc, activo, creado_en)
        VALUES ('DrinkHouse Demo', '9999999999999', true, now())
        RETURNING negocio_id INTO v_negocio_id;

        RAISE NOTICE 'No existia ningun negocio: se creo "DrinkHouse Demo" (negocio_id=%)', v_negocio_id;
    ELSE
        RAISE NOTICE 'Usando negocio existente negocio_id=%', v_negocio_id;
    END IF;

    -- ---------------------------------------------------------------
    -- 2) Categorias (crea solo si no existen para este negocio)
    -- ---------------------------------------------------------------
    SELECT categoria_id INTO v_cat_whisky FROM categorias WHERE negocio_id = v_negocio_id AND nombre = 'Whisky';
    IF v_cat_whisky IS NULL THEN
        INSERT INTO categorias (negocio_id, nombre, margen_ganancia_pct, activo)
        VALUES (v_negocio_id, 'Whisky', 33.00, true)
        RETURNING categoria_id INTO v_cat_whisky;
    END IF;

    SELECT categoria_id INTO v_cat_vodka FROM categorias WHERE negocio_id = v_negocio_id AND nombre = 'Vodka';
    IF v_cat_vodka IS NULL THEN
        INSERT INTO categorias (negocio_id, nombre, margen_ganancia_pct, activo)
        VALUES (v_negocio_id, 'Vodka', 37.00, true)
        RETURNING categoria_id INTO v_cat_vodka;
    END IF;

    SELECT categoria_id INTO v_cat_ron FROM categorias WHERE negocio_id = v_negocio_id AND nombre = 'Ron';
    IF v_cat_ron IS NULL THEN
        INSERT INTO categorias (negocio_id, nombre, margen_ganancia_pct, activo)
        VALUES (v_negocio_id, 'Ron', 34.00, true)
        RETURNING categoria_id INTO v_cat_ron;
    END IF;

    SELECT categoria_id INTO v_cat_cerveza FROM categorias WHERE negocio_id = v_negocio_id AND nombre = 'Cerveza';
    IF v_cat_cerveza IS NULL THEN
        INSERT INTO categorias (negocio_id, nombre, margen_ganancia_pct, activo)
        VALUES (v_negocio_id, 'Cerveza', 42.00, true)
        RETURNING categoria_id INTO v_cat_cerveza;
    END IF;

    SELECT categoria_id INTO v_cat_vino FROM categorias WHERE negocio_id = v_negocio_id AND nombre = 'Vino';
    IF v_cat_vino IS NULL THEN
        INSERT INTO categorias (negocio_id, nombre, margen_ganancia_pct, activo)
        VALUES (v_negocio_id, 'Vino', 45.00, true)
        RETURNING categoria_id INTO v_cat_vino;
    END IF;

    SELECT categoria_id INTO v_cat_tequila FROM categorias WHERE negocio_id = v_negocio_id AND nombre = 'Tequila';
    IF v_cat_tequila IS NULL THEN
        INSERT INTO categorias (negocio_id, nombre, margen_ganancia_pct, activo)
        VALUES (v_negocio_id, 'Tequila', 32.00, true)
        RETURNING categoria_id INTO v_cat_tequila;
    END IF;

    SELECT categoria_id INTO v_cat_licor FROM categorias WHERE negocio_id = v_negocio_id AND nombre = 'Licores y Cremas';
    IF v_cat_licor IS NULL THEN
        INSERT INTO categorias (negocio_id, nombre, margen_ganancia_pct, activo)
        VALUES (v_negocio_id, 'Licores y Cremas', 35.00, true)
        RETURNING categoria_id INTO v_cat_licor;
    END IF;

    -- ---------------------------------------------------------------
    -- 3) Productos
    -- ---------------------------------------------------------------
    INSERT INTO productos (
        negocio_id, categoria_id, nombre, marca, tipo, descripcion,
        costo_promedio, margen_ganancia_pct, precio_venta, precio_personalizado,
        stock_actual, stock_minimo, visible_sin_stock, activo,
        creado_en, actualizado_en
    ) VALUES
    -- Whisky
    (v_negocio_id, v_cat_whisky, 'Johnnie Walker Red Label 750ml', 'Johnnie Walker', 'Whisky',
        'Whisky escoces blended, botella de 750ml', 14.00, 35.00, 18.90, false, 40, 10, false, true, now(), now()),
    (v_negocio_id, v_cat_whisky, 'Buchanan''s 12 Anos 750ml', 'Buchanan''s', 'Whisky',
        'Whisky escoces blended premium 12 anos, 750ml', 22.00, 30.00, 28.60, false, 24, 6, false, true, now(), now()),
    (v_negocio_id, v_cat_whisky, 'Jack Daniel''s Old No. 7 750ml', 'Jack Daniel''s', 'Whisky',
        'Whisky Tennessee, botella de 750ml', 16.50, 32.00, 21.78, false, 30, 8, false, true, now(), now()),

    -- Vodka
    (v_negocio_id, v_cat_vodka, 'Absolut Vodka 750ml', 'Absolut', 'Vodka',
        'Vodka sueco, botella de 750ml', 12.00, 35.00, 16.20, false, 35, 10, false, true, now(), now()),
    (v_negocio_id, v_cat_vodka, 'Smirnoff Vodka 750ml', 'Smirnoff', 'Vodka',
        'Vodka triple destilado, botella de 750ml', 8.50, 40.00, 11.90, false, 50, 12, false, true, now(), now()),

    -- Ron
    (v_negocio_id, v_cat_ron, 'Bacardi Blanco 750ml', 'Bacardi', 'Ron',
        'Ron blanco, botella de 750ml', 9.00, 38.00, 12.42, false, 45, 12, false, true, now(), now()),
    (v_negocio_id, v_cat_ron, 'Ron Zacapa 23 750ml', 'Zacapa', 'Ron',
        'Ron anejado 23 anos, botella de 750ml', 28.00, 30.00, 36.40, false, 15, 5, false, true, now(), now()),
    (v_negocio_id, v_cat_ron, 'Havana Club 7 Anos 750ml', 'Havana Club', 'Ron',
        'Ron cubano anejo 7 anos, botella de 750ml', 15.00, 33.00, 19.95, false, 28, 8, false, true, now(), now()),

    -- Cerveza
    (v_negocio_id, v_cat_cerveza, 'Pilsener Six Pack 330ml', 'Pilsener', 'Cerveza',
        'Six pack de cerveza rubia, latas de 330ml', 4.50, 45.00, 6.53, false, 80, 20, false, true, now(), now()),
    (v_negocio_id, v_cat_cerveza, 'Corona Extra Six Pack 355ml', 'Corona', 'Cerveza',
        'Six pack de cerveza clara, botellas de 355ml', 6.80, 40.00, 9.52, false, 60, 15, false, true, now(), now()),
    (v_negocio_id, v_cat_cerveza, 'Heineken Six Pack 330ml', 'Heineken', 'Cerveza',
        'Six pack de cerveza lager, botellas de 330ml', 7.20, 40.00, 10.08, false, 55, 15, false, true, now(), now()),

    -- Vino
    (v_negocio_id, v_cat_vino, 'Casillero del Diablo Cabernet Sauvignon 750ml', 'Concha y Toro', 'Vino',
        'Vino tinto Cabernet Sauvignon, botella de 750ml', 7.50, 45.00, 10.88, false, 36, 10, false, true, now(), now()),
    (v_negocio_id, v_cat_vino, 'Santa Rita 120 Sauvignon Blanc 750ml', 'Santa Rita', 'Vino',
        'Vino blanco Sauvignon Blanc, botella de 750ml', 6.90, 45.00, 10.01, false, 30, 10, false, true, now(), now()),
    (v_negocio_id, v_cat_vino, 'Concha y Toro Rose 750ml', 'Concha y Toro', 'Vino',
        'Vino rosado, botella de 750ml', 7.00, 45.00, 10.15, false, 24, 8, false, true, now(), now()),

    -- Tequila
    (v_negocio_id, v_cat_tequila, 'Jose Cuervo Especial Reposado 750ml', 'Jose Cuervo', 'Tequila',
        'Tequila reposado, botella de 750ml', 13.50, 35.00, 18.23, false, 26, 8, false, true, now(), now()),
    (v_negocio_id, v_cat_tequila, 'Don Julio Blanco 750ml', 'Don Julio', 'Tequila',
        'Tequila blanco premium, botella de 750ml', 32.00, 30.00, 41.60, false, 12, 4, false, true, now(), now()),

    -- Licores y cremas
    (v_negocio_id, v_cat_licor, 'Baileys Irish Cream 750ml', 'Baileys', 'Licor',
        'Crema de whisky irlandes, botella de 750ml', 14.00, 35.00, 18.90, false, 22, 6, false, true, now(), now()),
    (v_negocio_id, v_cat_licor, 'Kahlua Cafe 750ml', 'Kahlua', 'Licor',
        'Licor de cafe, botella de 750ml', 11.00, 35.00, 14.85, false, 20, 6, false, true, now(), now());

    RAISE NOTICE 'Carga de productos de prueba completada (18 productos, 7 categorias).';
END $$;

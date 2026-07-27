-- Tipos de movimiento requeridos por el sistema
INSERT INTO tipos_movimiento (codigo, descripcion, prefijo_codigo)
SELECT 'OC', 'Orden de compra', 'OC'
WHERE NOT EXISTS (
    SELECT 1 FROM tipos_movimiento WHERE codigo = 'OC'
);

-- Secuencia de códigos para OC por cada negocio existente
INSERT INTO secuencias_codigo (negocio_id, tipo_movimiento_id, ultimo_numero, version)
SELECT n.negocio_id, tm.tipo_movimiento_id, 0, 0
FROM negocios n
CROSS JOIN tipos_movimiento tm
WHERE tm.codigo = 'OC'
AND NOT EXISTS (
    SELECT 1 FROM secuencias_codigo sc
    WHERE sc.negocio_id = n.negocio_id
    AND sc.tipo_movimiento_id = tm.tipo_movimiento_id
);

-- Estados de orden de compra requeridos por el sistema
INSERT INTO estados_oc (codigo, etiqueta)
SELECT 'BORRADOR', 'Borrador'
WHERE NOT EXISTS (SELECT 1 FROM estados_oc WHERE codigo = 'BORRADOR');

INSERT INTO estados_oc (codigo, etiqueta)
SELECT 'ENVIADA', 'Enviada'
WHERE NOT EXISTS (SELECT 1 FROM estados_oc WHERE codigo = 'ENVIADA');

INSERT INTO estados_oc (codigo, etiqueta)
SELECT 'RECIBIDA', 'Recibida'
WHERE NOT EXISTS (SELECT 1 FROM estados_oc WHERE codigo = 'RECIBIDA');

INSERT INTO estados_oc (codigo, etiqueta)
SELECT 'ANULADA', 'Anulada'
WHERE NOT EXISTS (SELECT 1 FROM estados_oc WHERE codigo = 'ANULADA');

-- Datos iniciales de prueba (las fechas son relativas al día de ejecución)

INSERT INTO editorial (id, nombre, correo) VALUES (1, 'Planeta', 'contacto@planeta.test');
INSERT INTO editorial (id, nombre, correo) VALUES (2, 'Porrúa', 'ventas@porrua.test');
INSERT INTO editorial (id, nombre, correo) VALUES (3, 'Fondo de Cultura Económica', 'contacto@fce.test');
INSERT INTO editorial (id, nombre, correo) VALUES (4, 'Siglo XXI', 'ventas@sigloxxi.test');

INSERT INTO stand (id, metros_cuadrados, estado, empresa_bloqueo) VALUES ('A1', 9, 'RESERVADO', NULL);
INSERT INTO stand (id, metros_cuadrados, estado, empresa_bloqueo) VALUES ('A2', 9, 'RESERVADO', NULL);
INSERT INTO stand (id, metros_cuadrados, estado, empresa_bloqueo) VALUES ('A3', 12, 'OCUPADO', NULL);
INSERT INTO stand (id, metros_cuadrados, estado, empresa_bloqueo) VALUES ('B1', 16, 'OCUPADO', NULL);
INSERT INTO stand (id, metros_cuadrados, estado, empresa_bloqueo) VALUES ('B2', 16, 'RESERVADO', NULL);
INSERT INTO stand (id, metros_cuadrados, estado, empresa_bloqueo) VALUES ('B3', 20, 'BLOQUEADO', 'Distribuidora Océano');
INSERT INTO stand (id, metros_cuadrados, estado, empresa_bloqueo) VALUES ('C1', 9, 'DISPONIBLE', NULL);
INSERT INTO stand (id, metros_cuadrados, estado, empresa_bloqueo) VALUES ('C2', 12, 'OCUPADO', NULL);

INSERT INTO reservacion (id, id_stand, id_editorial, fecha_reserva, monto_pagado, monto_acreditado)
    VALUES (1, 'A1', 1, DATEADD('DAY', -5, CURRENT_DATE), 2700, 3000);
INSERT INTO reservacion (id, id_stand, id_editorial, fecha_reserva, monto_pagado, monto_acreditado)
    VALUES (2, 'A2', 2, DATEADD('DAY', -1, CURRENT_DATE), 0, 0);
INSERT INTO reservacion (id, id_stand, id_editorial, fecha_reserva, monto_pagado, monto_acreditado)
    VALUES (3, 'A3', 3, DATEADD('DAY', -10, CURRENT_DATE), 5400, 6000);
INSERT INTO reservacion (id, id_stand, id_editorial, fecha_reserva, monto_pagado, monto_acreditado)
    VALUES (4, 'B1', 4, DATEADD('DAY', -20, CURRENT_DATE), 21600, 24000);
INSERT INTO reservacion (id, id_stand, id_editorial, fecha_reserva, monto_pagado, monto_acreditado)
    VALUES (5, 'B2', 2, DATEADD('DAY', -8, CURRENT_DATE), 0, 0);
INSERT INTO reservacion (id, id_stand, id_editorial, fecha_reserva, monto_pagado, monto_acreditado)
    VALUES (6, 'C2', 1, DATEADD('DAY', -12, CURRENT_DATE), 9000, 10000);

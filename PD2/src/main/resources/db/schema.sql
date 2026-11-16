-- Estructura de la base de datos del sistema de stands

DROP TABLE IF EXISTS reservacion;
DROP TABLE IF EXISTS stand;
DROP TABLE IF EXISTS editorial;

CREATE TABLE editorial (
    id INT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    correo VARCHAR(80) NOT NULL
);

CREATE TABLE stand (
    id VARCHAR(5) PRIMARY KEY,
    metros_cuadrados FLOAT NOT NULL,
    estado VARCHAR(15) NOT NULL,
    empresa_bloqueo VARCHAR(60)
);

CREATE TABLE reservacion (
    id INT PRIMARY KEY,
    id_stand VARCHAR(5) NOT NULL,
    id_editorial INT NOT NULL,
    fecha_reserva DATE NOT NULL,
    monto_pagado FLOAT NOT NULL,
    monto_acreditado FLOAT NOT NULL,
    FOREIGN KEY (id_stand) REFERENCES stand(id),
    FOREIGN KEY (id_editorial) REFERENCES editorial(id)
);

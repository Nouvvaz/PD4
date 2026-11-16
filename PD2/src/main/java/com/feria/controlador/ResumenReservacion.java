package com.feria.controlador;

import com.feria.modelo.Editorial;
import com.feria.modelo.Stand;

import java.time.LocalDate;

/** Reservación con sus datos relacionados y las condiciones ya calculadas. */
public record ResumenReservacion(int id, Stand stand, Editorial editorial, LocalDate fechaReserva,
                                 LocalDate vigenciaHasta, double montoPagado, double porcentajeCubierto,
                                 CondicionReservacion condicion) {
}

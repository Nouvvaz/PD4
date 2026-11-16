package com.feria.modelo;

import java.time.LocalDate;

public class Reservacion {
    private final int id;
    private final String idStand;
    private final int idEditorial;
    private final LocalDate fechaReserva;
    private final double montoPagado;
    private final double montoAcreditado;

    public Reservacion(int id, String idStand, int idEditorial, LocalDate fechaReserva,
                       double montoPagado, double montoAcreditado) {
        this.id = id;
        this.idStand = idStand;
        this.idEditorial = idEditorial;
        this.fechaReserva = fechaReserva;
        this.montoPagado = montoPagado;
        this.montoAcreditado = montoAcreditado;
    }

    public int getId() { return id; }
    public String getIdStand() { return idStand; }
    public int getIdEditorial() { return idEditorial; }
    public LocalDate getFechaReserva() { return fechaReserva; }
    public double getMontoPagado() { return montoPagado; }
    public double getMontoAcreditado() { return montoAcreditado; }
}

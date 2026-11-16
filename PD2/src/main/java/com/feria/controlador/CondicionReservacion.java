package com.feria.controlador;

public enum CondicionReservacion {
    VIGENTE("VIGENTE"),
    POR_VENCER("POR VENCER"),
    VENCIDA("VENCIDA"),
    EN_PAGOS("OCUPADO - EN PAGOS"),
    PAGADA("PAGADA"),
    INACTIVA("INACTIVA");

    private final String texto;

    CondicionReservacion(String texto) { this.texto = texto; }

    public String getTexto() { return texto; }
}

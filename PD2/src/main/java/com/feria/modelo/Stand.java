package com.feria.modelo;

public class Stand {
    private final String id;
    private final double metrosCuadrados;
    private final EstadoStand estado;
    private final String empresaBloqueo;

    public Stand(String id, double metrosCuadrados, EstadoStand estado, String empresaBloqueo) {
        this.id = id;
        this.metrosCuadrados = metrosCuadrados;
        this.estado = estado;
        this.empresaBloqueo = empresaBloqueo;
    }

    public String getId() { return id; }
    public double getMetrosCuadrados() { return metrosCuadrados; }
    public EstadoStand getEstado() { return estado; }
    public String getEmpresaBloqueo() { return empresaBloqueo; }
}

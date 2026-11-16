package com.feria.modelo;

public class Editorial {
    private final int id;
    private final String nombre;
    private final String correo;

    public Editorial(int id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
}

package com.feria.controlador;

import com.feria.configuracion.Configuracion;
import com.feria.modelo.Stand;
import com.feria.persistencia.StandDAO;

import java.util.List;

/** Consulta de stands y cálculo de su precio. */
public class ControladorStands {
    private final StandDAO standDAO;
    private final Configuracion config;

    public ControladorStands(StandDAO standDAO, Configuracion config) {
        this.standDAO = standDAO;
        this.config = config;
    }

    public List<Stand> listarStands() {
        return standDAO.listarTodos();
    }

    /** Precio = metros cuadrados x costo por m2 configurado. */
    public double precioDe(Stand stand) {
        return stand.getMetrosCuadrados() * config.getCostoPorM2();
    }
}

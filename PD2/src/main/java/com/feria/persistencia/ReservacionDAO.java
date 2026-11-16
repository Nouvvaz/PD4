package com.feria.persistencia;

import com.feria.modelo.Reservacion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReservacionDAO {
    private static final String SELECT_TODAS =
            "SELECT id, id_stand, id_editorial, fecha_reserva, monto_pagado, monto_acreditado "
                    + "FROM reservacion ORDER BY id";

    public List<Reservacion> listarTodas() {
        List<Reservacion> reservaciones = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtener();
             Statement statement = conexion.createStatement();
             ResultSet rs = statement.executeQuery(SELECT_TODAS)) {
            while (rs.next()) {
                reservaciones.add(new Reservacion(
                        rs.getInt("id"),
                        rs.getString("id_stand"),
                        rs.getInt("id_editorial"),
                        rs.getDate("fecha_reserva").toLocalDate(),
                        rs.getDouble("monto_pagado"),
                        rs.getDouble("monto_acreditado")));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron consultar las reservaciones: " + e.getMessage(), e);
        }
        return reservaciones;
    }
}

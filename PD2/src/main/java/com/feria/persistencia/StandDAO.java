package com.feria.persistencia;

import com.feria.modelo.EstadoStand;
import com.feria.modelo.Stand;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StandDAO {
    private static final String SELECT_TODOS =
            "SELECT id, metros_cuadrados, estado, empresa_bloqueo FROM stand ORDER BY id";

    public List<Stand> listarTodos() {
        List<Stand> stands = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtener();
             Statement statement = conexion.createStatement();
             ResultSet rs = statement.executeQuery(SELECT_TODOS)) {
            while (rs.next()) {
                stands.add(new Stand(
                        rs.getString("id"),
                        rs.getDouble("metros_cuadrados"),
                        EstadoStand.valueOf(rs.getString("estado")),
                        rs.getString("empresa_bloqueo")));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron consultar los stands: " + e.getMessage(), e);
        }
        return stands;
    }
}

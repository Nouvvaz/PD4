package com.feria.persistencia;

import com.feria.modelo.Editorial;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EditorialDAO {
    private static final String SELECT_TODAS = "SELECT id, nombre, correo FROM editorial ORDER BY id";

    public List<Editorial> listarTodas() {
        List<Editorial> editoriales = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtener();
             Statement statement = conexion.createStatement();
             ResultSet rs = statement.executeQuery(SELECT_TODAS)) {
            while (rs.next()) {
                editoriales.add(new Editorial(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("correo")));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudieron consultar las editoriales: " + e.getMessage(), e);
        }
        return editoriales;
    }
}

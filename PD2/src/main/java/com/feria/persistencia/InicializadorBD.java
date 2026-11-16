package com.feria.persistencia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Crea las tablas y carga los datos iniciales ejecutando los scripts de src/main/resources/db. */
public class InicializadorBD {
    private static final String[] SCRIPTS = {"/db/schema.sql", "/db/datos.sql"};

    private InicializadorBD() { }

    public static void crearYPoblar() {
        try (Connection conexion = ConexionBD.obtener();
             Statement statement = conexion.createStatement()) {
            for (String script : SCRIPTS) {
                for (String sentencia : leerSentencias(script)) {
                    statement.execute(sentencia);
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo crear la base de datos: " + e.getMessage(), e);
        }
    }

    private static List<String> leerSentencias(String recurso) {
        try (InputStream in = InicializadorBD.class.getResourceAsStream(recurso)) {
            if (in == null) {
                throw new PersistenciaException("No se encontró el script " + recurso, null);
            }
            String sql = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)).lines()
                    .filter(linea -> !linea.trim().startsWith("--"))
                    .collect(Collectors.joining("\n"));
            List<String> sentencias = new ArrayList<>();
            for (String sentencia : sql.split(";")) {
                if (!sentencia.isBlank()) sentencias.add(sentencia.trim());
            }
            return sentencias;
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer el script " + recurso, e);
        }
    }
}

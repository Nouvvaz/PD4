package com.feria.persistencia;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Crea las conexiones JDBC hacia la base de datos del sistema. */
public class ConexionBD {
    private static final String URL = System.getProperty("db.url", "jdbc:h2:mem:feria;DB_CLOSE_DELAY=-1");
    private static final String USUARIO = System.getProperty("db.user", "sa");
    private static final String CLAVE = System.getProperty("db.password", "");

    private ConexionBD() { }

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    /** Abre y cierra una conexión de prueba; devuelve el gestor y la URL a la que se conectó. */
    public static String verificar() {
        try (Connection conexion = obtener()) {
            DatabaseMetaData datos = conexion.getMetaData();
            return datos.getDatabaseProductName() + " " + datos.getDatabaseProductVersion()
                    + " (" + datos.getURL() + ")";
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo conectar a la base de datos: " + e.getMessage(), e);
        }
    }
}

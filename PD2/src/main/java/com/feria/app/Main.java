package com.feria.app;

import com.feria.configuracion.Configuracion;
import com.feria.controlador.ControladorEditoriales;
import com.feria.controlador.ControladorNotificaciones;
import com.feria.controlador.ControladorReservaciones;
import com.feria.controlador.ControladorStands;
import com.feria.notificaciones.NotificadorConsola;
import com.feria.persistencia.ConexionBD;
import com.feria.persistencia.EditorialDAO;
import com.feria.persistencia.InicializadorBD;
import com.feria.persistencia.PersistenciaException;
import com.feria.persistencia.ReservacionDAO;
import com.feria.persistencia.StandDAO;
import com.feria.vista.VistaConsola;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        try {
            InicializadorBD.crearYPoblar();
            System.out.println("Conexión a la base de datos exitosa: " + ConexionBD.verificar());
            System.out.println();
        } catch (PersistenciaException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
            return;
        }

        LocalDate hoy = LocalDate.now();
        Configuracion config = new Configuracion(1500, 7, 30, 10,
                hoy.plusDays(30), hoy.plusDays(60), 3);

        StandDAO standDAO = new StandDAO();
        EditorialDAO editorialDAO = new EditorialDAO();
        ReservacionDAO reservacionDAO = new ReservacionDAO();

        ControladorStands stands = new ControladorStands(standDAO, config);
        ControladorEditoriales editoriales = new ControladorEditoriales(editorialDAO);
        ControladorReservaciones reservaciones =
                new ControladorReservaciones(reservacionDAO, standDAO, editorialDAO, stands, config);
        ControladorNotificaciones notificaciones =
                new ControladorNotificaciones(reservaciones, config, new NotificadorConsola());

        VistaConsola vista = new VistaConsola(stands, editoriales, reservaciones, notificaciones, config, hoy);
        try {
            vista.mostrarListados();
        } catch (PersistenciaException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
            return;
        }
        vista.ejecutar();
    }
}

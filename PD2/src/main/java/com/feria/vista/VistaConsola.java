package com.feria.vista;

import com.feria.configuracion.Configuracion;
import com.feria.controlador.ControladorEditoriales;
import com.feria.controlador.ControladorNotificaciones;
import com.feria.controlador.ControladorReservaciones;
import com.feria.controlador.ControladorStands;
import com.feria.controlador.ResumenReservacion;
import com.feria.modelo.Editorial;
import com.feria.modelo.EstadoStand;
import com.feria.modelo.Stand;
import com.feria.persistencia.PersistenciaException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/** Interfaz de línea de comandos del sistema de venta de stands. */
public class VistaConsola {
    private final ControladorStands controladorStands;
    private final ControladorEditoriales controladorEditoriales;
    private final ControladorReservaciones controladorReservaciones;
    private final ControladorNotificaciones controladorNotificaciones;
    private final Configuracion config;
    private LocalDate hoy;

    public VistaConsola(ControladorStands controladorStands, ControladorEditoriales controladorEditoriales,
                        ControladorReservaciones controladorReservaciones,
                        ControladorNotificaciones controladorNotificaciones,
                        Configuracion config, LocalDate hoy) {
        this.controladorStands = controladorStands;
        this.controladorEditoriales = controladorEditoriales;
        this.controladorReservaciones = controladorReservaciones;
        this.controladorNotificaciones = controladorNotificaciones;
        this.config = config;
        this.hoy = hoy;
    }

    public void mostrarListados() {
        mostrarStands();
        System.out.println();
        mostrarEditoriales();
        System.out.println();
        mostrarReservaciones();
        System.out.println();
    }

    public void ejecutar() {
        System.out.println("Sistema de venta de stands. Escribe 'ayuda' para ver los comandos.");
        System.out.println("Fecha del sistema: " + hoy);

        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!in.hasNextLine()) break;
            String[] p = in.nextLine().trim().split("\\s+");
            if (p[0].isEmpty()) continue;
            try {
                switch (p[0].toLowerCase()) {
                    case "ayuda" -> ayuda();
                    case "stands" -> mostrarStands();
                    case "editoriales" -> mostrarEditoriales();
                    case "reservaciones" -> mostrarReservaciones();
                    case "listados" -> mostrarListados();
                    case "config" -> System.out.println(config);
                    case "fecha" -> {
                        if (p.length > 1) cambiarFecha(p[1]);
                        System.out.println("Fecha del sistema: " + hoy);
                    }
                    case "notificar" -> {
                        int n = controladorNotificaciones.revisar(hoy).size();
                        System.out.println(n + " notificación(es) enviada(s).");
                    }
                    case "salir" -> { return; }
                    default -> System.out.println("Comando desconocido. Usa 'ayuda'.");
                }
            } catch (PersistenciaException e) {
                System.out.println("Error de base de datos: " + e.getMessage());
            } catch (NumberFormatException | DateTimeParseException e) {
                System.out.println("Error: usa el formato AAAA-MM-DD o +N / -N días.");
            }
        }
    }

    private void cambiarFecha(String valor) {
        if (valor.startsWith("+") || valor.startsWith("-")) {
            hoy = hoy.plusDays(Long.parseLong(valor));
        } else {
            hoy = LocalDate.parse(valor);
        }
    }

    private void mostrarStands() {
        List<Stand> stands = controladorStands.listarStands();
        System.out.println("STANDS (" + stands.size() + ")");
        for (Stand s : stands) {
            String detalle = s.getEstado() == EstadoStand.BLOQUEADO ? " | para " + s.getEmpresaBloqueo() : "";
            System.out.printf(Locale.US, "%-4s %6.1f m2  $%,10.2f  %-10s%s%n",
                    s.getId(), s.getMetrosCuadrados(), controladorStands.precioDe(s), s.getEstado(), detalle);
        }
    }

    private void mostrarEditoriales() {
        List<Editorial> editoriales = controladorEditoriales.listarEditoriales();
        System.out.println("EDITORIALES (" + editoriales.size() + ")");
        for (Editorial e : editoriales) {
            System.out.printf(Locale.US, "%-3d %-28s <%s>%n", e.getId(), e.getNombre(), e.getCorreo());
        }
    }

    private void mostrarReservaciones() {
        List<ResumenReservacion> reservaciones = controladorReservaciones.listarReservaciones(hoy);
        System.out.println("RESERVACIONES (" + reservaciones.size() + ")");
        for (ResumenReservacion r : reservaciones) {
            System.out.printf(Locale.US,
                    "#%-2d %-4s %-27s reservada %s | vigencia %s | pagado $%,10.2f | cubierto %5.1f%% | %s%n",
                    r.id(), r.stand().getId(), r.editorial().getNombre(), r.fechaReserva(), r.vigenciaHasta(),
                    r.montoPagado(), r.porcentajeCubierto(), r.condicion().getTexto());
        }
    }

    private void ayuda() {
        System.out.println("""
                stands                         lista todos los stands
                editoriales                    lista las editoriales registradas
                reservaciones                  lista las reservaciones con su condición
                listados                       muestra los tres listados
                config                         muestra la configuración
                fecha [AAAA-MM-DD | +N | -N]   ver o cambiar la fecha del sistema (para pruebas)
                notificar                      revisa vencimientos y envía avisos a las editoriales
                salir""");
    }
}

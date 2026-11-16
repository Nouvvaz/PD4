package com.feria.controlador;

import com.feria.configuracion.Configuracion;
import com.feria.modelo.EstadoStand;
import com.feria.notificaciones.Notificacion;
import com.feria.notificaciones.Notificador;
import com.feria.notificaciones.TipoNotificacion;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/** Revisa los vencimientos de las reservaciones y avisa a las editoriales. */
public class ControladorNotificaciones {
    private final ControladorReservaciones controladorReservaciones;
    private final Configuracion config;
    private final Notificador notificador;

    public ControladorNotificaciones(ControladorReservaciones controladorReservaciones, Configuracion config,
                                     Notificador notificador) {
        this.controladorReservaciones = controladorReservaciones;
        this.config = config;
        this.notificador = notificador;
    }

    /** Genera y envía las notificaciones que corresponden a la fecha dada. */
    public List<Notificacion> revisar(LocalDate hoy) {
        List<Notificacion> enviadas = new ArrayList<>();
        for (ResumenReservacion r : controladorReservaciones.listarReservaciones(hoy)) {
            if (r.condicion() == CondicionReservacion.VENCIDA) continue;
            EstadoStand estado = r.stand().getEstado();

            if (estado == EstadoStand.RESERVADO) {
                agregar(enviadas, r, TipoNotificacion.RESERVA_POR_VENCER, r.vigenciaHasta(), hoy);
            }
            boolean conSaldo = (estado == EstadoStand.RESERVADO || estado == EstadoStand.OCUPADO)
                    && !controladorReservaciones.estaCubiertaCompleta(r);
            if (conSaldo) {
                agregar(enviadas, r, TipoNotificacion.PRONTO_PAGO_POR_VENCER, config.getFechaProntoPago(), hoy);
                agregar(enviadas, r, TipoNotificacion.FECHA_LIMITE_POR_VENCER, config.getFechaMaximaCubrirCosto(), hoy);
            }
        }
        enviadas.forEach(notificador::enviar);
        return enviadas;
    }

    private void agregar(List<Notificacion> lista, ResumenReservacion r, TipoNotificacion tipo,
                         LocalDate vence, LocalDate hoy) {
        long dias = ChronoUnit.DAYS.between(hoy, vence);
        if (dias >= 0 && dias <= config.getDiasAnticipoAviso()) {
            lista.add(new Notificacion(r.editorial(), tipo, r.stand().getId(), vence, dias));
        }
    }
}

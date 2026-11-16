package com.feria.controlador;

import com.feria.configuracion.Configuracion;
import com.feria.modelo.Editorial;
import com.feria.modelo.EstadoStand;
import com.feria.modelo.Reservacion;
import com.feria.modelo.Stand;
import com.feria.persistencia.EditorialDAO;
import com.feria.persistencia.ReservacionDAO;
import com.feria.persistencia.StandDAO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Consulta de reservaciones y cálculo de su vigencia, porcentaje cubierto y condición. */
public class ControladorReservaciones {
    private static final double COBERTURA_TOTAL = 99.999;

    private final ReservacionDAO reservacionDAO;
    private final StandDAO standDAO;
    private final EditorialDAO editorialDAO;
    private final ControladorStands controladorStands;
    private final Configuracion config;

    public ControladorReservaciones(ReservacionDAO reservacionDAO, StandDAO standDAO, EditorialDAO editorialDAO,
                                    ControladorStands controladorStands, Configuracion config) {
        this.reservacionDAO = reservacionDAO;
        this.standDAO = standDAO;
        this.editorialDAO = editorialDAO;
        this.controladorStands = controladorStands;
        this.config = config;
    }

    public List<ResumenReservacion> listarReservaciones(LocalDate hoy) {
        Map<String, Stand> stands = standDAO.listarTodos().stream()
                .collect(Collectors.toMap(Stand::getId, Function.identity()));
        Map<Integer, Editorial> editoriales = editorialDAO.listarTodas().stream()
                .collect(Collectors.toMap(Editorial::getId, Function.identity()));

        List<ResumenReservacion> resumenes = new ArrayList<>();
        for (Reservacion r : reservacionDAO.listarTodas()) {
            Stand stand = stands.get(r.getIdStand());
            Editorial editorial = editoriales.get(r.getIdEditorial());
            double porcentaje = r.getMontoAcreditado() / controladorStands.precioDe(stand) * 100.0;
            LocalDate vigenciaHasta = r.getFechaReserva().plusDays(config.getDiasVigenciaReserva());
            resumenes.add(new ResumenReservacion(r.getId(), stand, editorial, r.getFechaReserva(), vigenciaHasta,
                    r.getMontoPagado(), porcentaje, condicionDe(stand, porcentaje, vigenciaHasta, hoy)));
        }
        return resumenes;
    }

    public boolean estaCubiertaCompleta(ResumenReservacion reservacion) {
        return reservacion.porcentajeCubierto() >= COBERTURA_TOTAL;
    }

    private CondicionReservacion condicionDe(Stand stand, double porcentaje, LocalDate vigenciaHasta, LocalDate hoy) {
        if (stand.getEstado() == EstadoStand.OCUPADO) {
            return porcentaje >= COBERTURA_TOTAL ? CondicionReservacion.PAGADA : CondicionReservacion.EN_PAGOS;
        }
        if (stand.getEstado() == EstadoStand.RESERVADO) {
            long dias = ChronoUnit.DAYS.between(hoy, vigenciaHasta);
            if (dias < 0) return CondicionReservacion.VENCIDA;
            if (dias <= config.getDiasAnticipoAviso()) return CondicionReservacion.POR_VENCER;
            return CondicionReservacion.VIGENTE;
        }
        return CondicionReservacion.INACTIVA;
    }
}

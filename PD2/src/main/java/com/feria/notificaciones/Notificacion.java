package com.feria.notificaciones;

import com.feria.modelo.Editorial;

import java.time.LocalDate;

public record Notificacion(Editorial destinatario, TipoNotificacion tipo, String standId,
                           LocalDate fechaVence, long diasRestantes) {
    public String mensaje() {
        String cuando = diasRestantes == 0 ? "hoy" : "en " + diasRestantes + " día(s)";
        return String.format("Para %s <%s>: %s (stand %s). Vence %s (%s).",
                destinatario.getNombre(), destinatario.getCorreo(), tipo.getTexto(), standId, fechaVence, cuando);
    }
}

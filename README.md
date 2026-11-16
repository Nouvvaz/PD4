# Sistema administrativo de venta de stands

Aplicación de línea de comandos en Java 17 para administrar la venta de stands a editoriales. Los datos se leen de una base de datos relacional mediante JDBC y el código está organizado por capas (modelo, controlador, persistencia y vista).

## Video explicativo

URL del video (máximo 5 minutos, con acceso verificado): _pendiente de agregar_

## Ejecutar

Con Maven (descarga el conector de H2 automáticamente):

```bash
mvn compile exec:java
```

Sin Maven, usando el conector incluido en `lib/`:

```bash
mkdir -p out
javac -encoding UTF-8 -cp lib/h2-2.2.220.jar -d out $(find src/main/java -name "*.java")
java -cp "out:src/main/resources:lib/h2-2.2.220.jar" com.feria.app.Main
```

(en Windows el separador del classpath es `;` en lugar de `:`).

Al iniciar, `main()` crea la base de datos H2 (en memoria), ejecuta `schema.sql` y `datos.sql`, muestra que la conexión a la BD fue exitosa y presenta los listados de Stands, Editoriales y Reservaciones leídos con JDBC. Después queda disponible la línea de comandos para consultas adicionales.

### Comandos

| Comando | Descripción |
|---|---|
| `stands` | Lista de stands (estado, metros cuadrados y precio calculado) |
| `editoriales` | Lista de editoriales registradas |
| `reservaciones` | Lista de reservaciones con vigencia, monto pagado, porcentaje cubierto y condición |
| `listados` | Muestra los tres listados seguidos |
| `config` | Muestra la configuración del sistema |
| `fecha [AAAA-MM-DD \| +N \| -N]` | Ve o cambia la fecha del sistema (para pruebas) |
| `notificar` | Revisa vencimientos y envía avisos a las editoriales |
| `salir` | Termina el programa |

Ejemplo de prueba de notificaciones:

```
> listados
> notificar
> fecha +28
> notificar
```

## Requisitos

**Funcionales**
- RF1. Mapear los stands con identificador, metros cuadrados y estado (disponible, reservado, ocupado, bloqueado), leídos desde la base de datos.
- RF2. El precio de un stand se calcula con sus dimensiones: m² × costo por m² (configurable); no se almacena en la base de datos.
- RF3. Reservado: la editorial paga después; la reservación tiene tiempo de vigencia (fecha de reserva + días de vigencia configurables). Si vence sin llegar al porcentaje mínimo, se muestra como vencida.
- RF4. Ocupado: el stand pasa a ocupado cuando el pago cubre al menos el porcentaje mínimo; se sigue pagando hasta cubrir el 100 %.
- RF5. Bloqueado: reserva especial del administrador (negociación previa a la salida del sistema).
- RF6. Descuento por pronto pago: los pagos hasta la fecha límite de descuento acreditan más de lo pagado (monto acreditado vs. monto pagado).
- RF7. Fecha máxima para cubrir el costo total.
- RF8. Porcentaje mínimo configurable para pasar de reservado a ocupado.
- RF9. **Notificar a las editoriales** cuando: su reservación esté por vencer, el pronto pago esté por vencer, o la fecha límite para cubrir el costo esté por vencer. Las reservaciones vencidas no generan avisos.
- RF10. El sistema consulta la base de datos, reconstruye los objetos y devuelve tres listados completos: stands, editoriales y reservaciones (reflejando sus condiciones y estados).

En esta etapa solo se realizan operaciones de lectura (`SELECT`). Las operaciones de escritura (reservar, pagar, bloquear, liberar, transferir) quedan para la siguiente etapa.

**No funcionales**
- RNF1. Funciona por línea de comandos.
- RNF2. Código separado en paquetes: `modelo`, `controlador`, `persistencia`, `vista`, `notificaciones`, `configuracion` y `app`.
- RNF3. Los datos viven en una base de datos relacional (H2) accedida con JDBC (`DriverManager`, `Statement`, `ResultSet`), cerrando siempre las conexiones.
- RNF4. El modelo contiene solo datos mínimos (sin lógica de negocio); toda la lógica está en los controladores y cada controlador atiende un solo proceso.
- RNF5. Todas las clases que se conectan a la base de datos están en el paquete `persistencia`.
- RNF6. El canal de notificación es intercambiable (interfaz `Notificador`).

## Arquitectura

| Capa | Paquete | Contenido |
|---|---|---|
| Modelo | `com.feria.modelo` | `Stand`, `Editorial`, `Reservacion`, `EstadoStand` |
| Controlador | `com.feria.controlador` | `ControladorStands`, `ControladorEditoriales`, `ControladorReservaciones`, `ControladorNotificaciones` |
| Persistencia | `com.feria.persistencia` | `ConexionBD`, `InicializadorBD`, `StandDAO`, `EditorialDAO`, `ReservacionDAO` |
| Vista | `com.feria.vista` | `VistaConsola` |
| Notificaciones | `com.feria.notificaciones` | `Notificacion`, `TipoNotificacion`, `Notificador`, `NotificadorConsola` |
| Configuración | `com.feria.configuracion` | `Configuracion` |
| Arranque | `com.feria.app` | `Main` |

Flujo de una consulta: `VistaConsola` → controlador → DAO → base de datos. El DAO recorre el `ResultSet` y reconstruye los objetos del modelo; el controlador calcula lo que no se guarda (precio, vigencia, porcentaje cubierto, condición) y devuelve el resultado a la vista.

## Base de datos

Tres tablas, definidas en `src/main/resources/db/schema.sql` y pobladas con `datos.sql`:

```mermaid
erDiagram
    EDITORIAL ||--o{ RESERVACION : realiza
    STAND ||--o{ RESERVACION : "es reservado en"
    EDITORIAL {
        int id PK
        varchar nombre
        varchar correo
    }
    STAND {
        varchar id PK
        float metros_cuadrados
        varchar estado
        varchar empresa_bloqueo
    }
    RESERVACION {
        int id PK
        varchar id_stand FK
        int id_editorial FK
        date fecha_reserva
        float monto_pagado
        float monto_acreditado
    }
```

- `stand.estado` guarda el nombre del estado (`DISPONIBLE`, `RESERVADO`, `OCUPADO`, `BLOQUEADO`).
- La vigencia de la reservación, el precio del stand y el porcentaje cubierto se calculan en el controlador.

La conexión se define en `ConexionBD` (por defecto H2 en memoria). Puede cambiarse con `-Ddb.url`, `-Ddb.user` y `-Ddb.password`.

## Estados de un stand

```mermaid
stateDiagram-v2
    [*] --> DISPONIBLE
    DISPONIBLE --> RESERVADO: reservar
    DISPONIBLE --> BLOQUEADO: bloquear (admin)
    BLOQUEADO --> DISPONIBLE: liberar (admin)
    BLOQUEADO --> RESERVADO: transferir a editorial
    RESERVADO --> OCUPADO: pago >= % mínimo
    RESERVADO --> DISPONIBLE: reserva vencida
```

## Diagrama de clases

```mermaid
classDiagram
    direction LR
    class Stand {
        -String id
        -double metrosCuadrados
        -EstadoStand estado
        -String empresaBloqueo
    }
    class Editorial {
        -int id
        -String nombre
        -String correo
    }
    class Reservacion {
        -int id
        -String idStand
        -int idEditorial
        -LocalDate fechaReserva
        -double montoPagado
        -double montoAcreditado
    }
    class EstadoStand {
        <<enumeration>>
        DISPONIBLE
        RESERVADO
        OCUPADO
        BLOQUEADO
    }
    class ConexionBD {
        +obtener() Connection
    }
    class InicializadorBD {
        +crearYPoblar()
    }
    class StandDAO {
        +listarTodos() List~Stand~
    }
    class EditorialDAO {
        +listarTodas() List~Editorial~
    }
    class ReservacionDAO {
        +listarTodas() List~Reservacion~
    }
    class ControladorStands {
        +listarStands() List~Stand~
        +precioDe(Stand) double
    }
    class ControladorEditoriales {
        +listarEditoriales() List~Editorial~
    }
    class ControladorReservaciones {
        +listarReservaciones(LocalDate) List~ResumenReservacion~
        +estaCubiertaCompleta(ResumenReservacion) boolean
    }
    class ControladorNotificaciones {
        +revisar(LocalDate) List~Notificacion~
    }
    class ResumenReservacion {
        <<record>>
        id
        stand
        editorial
        fechaReserva
        vigenciaHasta
        montoPagado
        porcentajeCubierto
        condicion
    }
    class CondicionReservacion {
        <<enumeration>>
        VIGENTE
        POR_VENCER
        VENCIDA
        EN_PAGOS
        PAGADA
        INACTIVA
    }
    class Configuracion {
        -double costoPorM2
        -int diasVigenciaReserva
        -double porcentajeMinimoOcupado
        -double descuentoProntoPago
        -LocalDate fechaProntoPago
        -LocalDate fechaMaximaCubrirCosto
        -int diasAnticipoAviso
    }
    class Notificador {
        <<interface>>
        +enviar(Notificacion)
    }
    class NotificadorConsola
    class Notificacion {
        <<record>>
        destinatario
        tipo
        standId
        fechaVence
        diasRestantes
    }
    class TipoNotificacion {
        <<enumeration>>
        RESERVA_POR_VENCER
        PRONTO_PAGO_POR_VENCER
        FECHA_LIMITE_POR_VENCER
    }
    class VistaConsola {
        +ejecutar()
    }
    class Main

    Stand --> EstadoStand
    Reservacion ..> Stand : idStand
    Reservacion ..> Editorial : idEditorial

    StandDAO ..> ConexionBD
    EditorialDAO ..> ConexionBD
    ReservacionDAO ..> ConexionBD
    InicializadorBD ..> ConexionBD
    StandDAO ..> Stand
    EditorialDAO ..> Editorial
    ReservacionDAO ..> Reservacion

    ControladorStands --> StandDAO
    ControladorStands --> Configuracion
    ControladorEditoriales --> EditorialDAO
    ControladorReservaciones --> ReservacionDAO
    ControladorReservaciones --> StandDAO
    ControladorReservaciones --> EditorialDAO
    ControladorReservaciones --> ControladorStands
    ControladorReservaciones --> Configuracion
    ControladorReservaciones ..> ResumenReservacion
    ResumenReservacion --> CondicionReservacion
    ControladorNotificaciones --> ControladorReservaciones
    ControladorNotificaciones --> Configuracion
    ControladorNotificaciones --> Notificador
    ControladorNotificaciones ..> Notificacion
    Notificacion --> TipoNotificacion
    NotificadorConsola ..|> Notificador

    VistaConsola --> ControladorStands
    VistaConsola --> ControladorEditoriales
    VistaConsola --> ControladorReservaciones
    VistaConsola --> ControladorNotificaciones
    Main --> InicializadorBD
    Main --> VistaConsola
```

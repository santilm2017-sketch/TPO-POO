package Dominio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PromocionAnticipada implements EstrategiaPromocion {
    private static final double PORCENTAJE_DESCUENTO = 0.15; // 15% (RN11)
    private static final int DIAS_ANTICIPACION_MINIMA = 60;   // Más de 60 días (RN11)

    @Override
    public boolean esAplicable(Reserva reserva) {
        if (reserva == null || reserva.getFecha() == null || reserva.getSalida() == null) {
            return false;
        }

        try {
            LocalDate fechaReserva = LocalDate.parse(reserva.getFecha());
            LocalDate fechaInicioSalida = LocalDate.parse(reserva.getSalida().getFechaInicio());
            long diasAnticipacion = ChronoUnit.DAYS.between(fechaReserva, fechaInicioSalida);

            return diasAnticipacion > DIAS_ANTICIPACION_MINIMA;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public double calcularDescuento(Reserva reserva, double subtotalPasajes) {
        if (!esAplicable(reserva)) {
            return 0.0;
        }
        return subtotalPasajes * PORCENTAJE_DESCUENTO;
    }
}

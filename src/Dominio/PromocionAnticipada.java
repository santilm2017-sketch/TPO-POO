package Dominio;

public class PromocionAnticipada implements EstrategiaPromocion {
    private static final double PORCENTAJE_DESCUENTO = 0.15; // 15% (RN11)
    private static final int DIAS_ANTICIPACION_MINIMA = 60;   // Más de 60 días (RN11)

    @Override
    public boolean esAplicable(Reserva reserva) {
        if (reserva == null || reserva.getFecha() == null || reserva.getSalida() == null) {
            return false;
        }

        Fecha compra = new Fecha(reserva.getFecha());
        return compra.diasHasta(new Fecha(reserva.getSalida().getFechaInicio())) > DIAS_ANTICIPACION_MINIMA;
    }

    @Override
    public double calcularDescuento(Reserva reserva, double subtotalPasajes) {
        if (!esAplicable(reserva)) {
            return 0.0;
        }
        return subtotalPasajes * PORCENTAJE_DESCUENTO;
    }
}

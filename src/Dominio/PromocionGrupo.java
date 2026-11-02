package Dominio;

public class PromocionGrupo implements EstrategiaPromocion {
    private static final double PORCENTAJE_DESCUENTO = 0.10; // 10% (RN11)
    private static final int CANTIDAD_MINIMA_PASAJEROS = 6;  // 6 o más pasajeros (RN11)

    @Override
    public boolean esAplicable(Reserva reserva) {
        if (reserva == null || reserva.getPasajeros() == null) {
            return false;
        }
        return reserva.getPasajeros().size() >= CANTIDAD_MINIMA_PASAJEROS;
    }

    @Override
    public double calcularDescuento(Reserva reserva, double subtotalPasajes) {
        if (!esAplicable(reserva)) {
            return 0.0;
        }
        return subtotalPasajes * PORCENTAJE_DESCUENTO;
    }
}
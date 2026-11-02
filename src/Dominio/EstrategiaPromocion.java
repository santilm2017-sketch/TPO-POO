package Dominio;

public interface EstrategiaPromocion {
    /**
     * Evalúa si la reserva cumple con los requisitos para aplicar esta promoción.
     */
    boolean esAplicable(Reserva reserva);

    /**
     * Calcula el monto total en dinero ($) a descontar sobre el subtotal de pasajes.
     */
    double calcularDescuento(Reserva reserva, double subtotalPasajes);
}

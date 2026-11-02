package Dominio;

import java.util.ArrayList;
import java.util.List;

public class CalculadoraPrecioReserva {
    private List<EstrategiaPromocion> promocionesDisponibles;

    public CalculadoraPrecioReserva() {
        this.promocionesDisponibles = new ArrayList<>();
        // Por defecto, registra las dos promociones oficiales del sistema (RN11)
        this.promocionesDisponibles.add(new PromocionAnticipada());
        this.promocionesDisponibles.add(new PromocionGrupo());
    }

    /**
     * Calcula el precio de pasaje individual según el tipo de pasajero (RN10).
     */
    public double calcularPrecioPasajero(Pasajero pasajero, double tarifaBase) {
        if (pasajero == null) {
            return 0.0;
        }

        String tipo = pasajero.getTipo();
        if (tipo == null) {
            tipo = "adulto";
        }

        switch (tipo.toLowerCase()) {
            case "menor_3":
            case "bebe":
                return 0.0; // Menor de 3 años no paga (RN10)
            case "menor_12":
            case "menor":
                return tarifaBase * 0.60; // Menor de 12 años paga el 60% (RN10)
            case "jubilado":
                return tarifaBase * 0.90; // Jubilado tiene 10% de descuento (RN10)
            case "adulto":
            default:
                return tarifaBase; // Adulto paga el 100% de la tarifa (RN10)
        }
    }

    /**
     * Calcula el subtotal acumulado de los pasajes de todos los integrantes de la reserva.
     */
    public double calcularSubtotalPasajes(Reserva reserva, double tarifaBase) {
        double subtotal = 0.0;
        for (Pasajero p : reserva.getPasajeros()) {
            subtotal += calcularPrecioPasajero(p, tarifaBase);
        }
        return subtotal;
    }

    /**
     * Suma los servicios adicionales contratados por todos los pasajeros de la reserva (RN13).
     */
    public double calcularTotalServiciosAdicionales(Reserva reserva) {
        double totalAdicionales = 0.0;
        for (Pasajero p : reserva.getPasajeros()) {
            for (ContratacionServicio cs : p.getContrataciones()) {
                totalAdicionales += cs.calcularImporte();
            }
        }
        return totalAdicionales;
    }

    /**
     * Evalúa las promociones aplicables y devuelve la más beneficiosa (mayor descuento en $) (RN12).
     */
    public double obtenerMejorDescuento(Reserva reserva, double subtotalPasajes) {
        double mayorDescuento = 0.0;
        for (EstrategiaPromocion promo : promocionesDisponibles) {
            if (promo.esAplicable(reserva)) {
                double descuento = promo.calcularDescuento(reserva, subtotalPasajes);
                if (descuento > mayorDescuento) {
                    mayorDescuento = descuento;
                }
            }
        }
        return mayorDescuento;
    }

    /**
     * Calcula el precio total final de la reserva y actualiza su atributo 'total'.
     */
    public double calcularTotalReserva(Reserva reserva, String temporada) {
        if (reserva == null || reserva.getSalida() == null) {
            throw new IllegalArgumentException("La reserva o su salida asociada no pueden ser nulas.");
        }

        // 1. Obtener tarifa base según la fecha y temporada
        Salida salida = reserva.getSalida();
        Tarifa tarifaVigente = salida.getTour().getTarifaVigente(salida.getFechaInicio(), temporada);
        double precioBase = tarifaVigente.getPrecioBase();

        // 2. Calcular subtotal de pasajes según categorías de pasajeros
        double subtotalPasajes = calcularSubtotalPasajes(reserva, precioBase);

        // 3. Obtener el mejor descuento promocional (regla de no acumulación RN12)
        double descuentoPromocion = obtenerMejorDescuento(reserva, subtotalPasajes);

        // 4. Sumar los servicios adicionales contratados (RN13)
        double totalAdicionales = calcularTotalServiciosAdicionales(reserva);

        // 5. Total final = (Subtotal Pasajes - Descuento Promo) + Adicionales
        double totalFinal = (subtotalPasajes - descuentoPromocion) + totalAdicionales;
        reserva.setTotal(totalFinal);

        return totalFinal;
    }

    public void agregarPromocion(EstrategiaPromocion promocion) {
        if (promocion != null) {
            promocionesDisponibles.add(promocion);
        }
    }
}

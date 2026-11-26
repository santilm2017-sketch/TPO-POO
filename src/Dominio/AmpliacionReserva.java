package Dominio;

import java.util.List;
import Dominio.Excepciones.RequisitoPasajeroException;

// Coordina la ampliación sin cambiar el formato de las reservas guardadas.
public class AmpliacionReserva {
    public static void validarEstado(Reserva reserva, Fecha hoy) {
        if (!reserva.vigente() || !reserva.getSalida().abierta()) throw new IllegalArgumentException("La reserva está cerrada.");
        if (hoy.toString().compareTo(reserva.getFecha()) < 0 || reserva.diasHastaSalida(hoy) < 10)
            throw new IllegalArgumentException("Se pueden agregar pasajeros hasta diez días antes, dentro del plazo de pago.");
    }

    public static void agregar(Reserva reserva, List<Pasajero> nuevos, Fecha hoy, double tarifa)
            throws RequisitoPasajeroException {
        validarEstado(reserva, hoy);
        if (nuevos == null || nuevos.isEmpty()) throw new IllegalArgumentException("Ingrese pasajeros nuevos.");
        if (!reserva.getSalida().verificarCupoDisponible(nuevos.size())) throw new IllegalArgumentException("Cupo insuficiente.");
        List<Pasajero> completa = reserva.getPasajeros();
        for (Pasajero nuevo : nuevos) {
            if (nuevo == null) throw new IllegalArgumentException("Pasajero obligatorio.");
            reserva.validarPasajero(nuevo);
            for (Pasajero p : completa) if (p.getDocumento().equalsIgnoreCase(nuevo.getDocumento()))
                throw new IllegalArgumentException("Pasajero duplicado.");
            completa.add(nuevo);
        }
        CalculadoraPrecioReserva calculadora = new CalculadoraPrecioReserva();
        double antes = calculadora.calcularConTarifa(reserva, tarifa);
        double despues = calculadora.calcularConPasajeros(reserva, completa, tarifa);
        double total = reserva.getTotal() + despues - antes;
        if (!Importes.esValido(total)) throw new IllegalArgumentException("Importe inválido para el cálculo en centavos.");
        total = Importes.redondear(total);
        if (total < reserva.calcularMontoPagado()) throw new IllegalArgumentException("El nuevo total no puede ser menor que lo pagado.");
        // Se valida el lote entero y se calcula sin tocar la reserva. Confirmar es el último paso.
        for (Pasajero nuevo : nuevos) reserva.agregarPasajero(nuevo);
        reserva.setTotal(total);
    }
}

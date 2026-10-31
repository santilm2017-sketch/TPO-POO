
package Dominio;

public class PoliticaCancelacionCliente implements PoliticaCancelacion {

    @Override
    public double calcularDevolucion(double montoTotal, int diasAnticipacion) {
        if (montoTotal < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo.");
        }

        if (diasAnticipacion >= 30) {
            return montoTotal * 0.90;
        } else if (diasAnticipacion >= 15) {
            return montoTotal * 0.50;
        } else if (diasAnticipacion >= 7) {
            return montoTotal * 0.20;
        } else {
            return 0.0;
        }
    }
}

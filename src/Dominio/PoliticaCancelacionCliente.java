package Dominio;

public class PoliticaCancelacionCliente implements PoliticaCancelacion {
    @Override
    public double calcularDevolucion(double montoTotal, int diasAnticipacion) {
        if (diasAnticipacion >= 30) {
            return montoTotal * 0.80; // Retención del 20%
        } else if (diasAnticipacion >= 15) {
            return montoTotal * 0.50; // Retención del 50%
        } else {
            return 0.0; // Sin devolución
        }
    }
}

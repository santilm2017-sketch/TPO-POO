package Dominio;

public class PoliticaCancelacionAgencia implements PoliticaCancelacion {
    @Override
    public double calcularDevolucion(double montoTotal, int diasAnticipacion) {
        return montoTotal;
    }
}

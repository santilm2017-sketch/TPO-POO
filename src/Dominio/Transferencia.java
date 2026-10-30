package Dominio;

public class Transferencia implements MedioPago{
    @Override
    public double calcularMontoFinal(double importe, int cuotas) {
        return importe;
    }
}

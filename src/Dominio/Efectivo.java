package Dominio;

public class Efectivo implements MedioPago{
    @Override
    public double calcularMontoFinal(double importe, int cuotas) {
        return importe;
    }
}

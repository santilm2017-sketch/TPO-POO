package Dominio;

public class Efectivo implements MedioPago, java.io.Serializable{
    @Override
    public double calcularMontoFinal(double importe, int cuotas) {
        return importe;
    }
}

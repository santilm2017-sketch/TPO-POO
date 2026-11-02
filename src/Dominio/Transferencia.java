package Dominio;

public class Transferencia implements MedioPago, java.io.Serializable{
    @Override
    public double calcularMontoFinal(double importe, int cuotas) {
        return importe;
    }
}

package Dominio;

public class Transferencia implements MedioPago, java.io.Serializable{

    public String getNombre() { return "transferencia"; }

    @Override
    public double calcularMontoFinal(double importe, int cuotas) {
        return importe;
    }
}

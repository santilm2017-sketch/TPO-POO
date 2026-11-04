package Dominio;

public class Efectivo implements MedioPago, java.io.Serializable{

    public String getNombre() { return "efectivo"; }

    @Override
    public double calcularMontoFinal(double importe, int cuotas) {
        return importe;
    }
}

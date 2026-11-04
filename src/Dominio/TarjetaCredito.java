package Dominio;

public class TarjetaCredito implements MedioPago, java.io.Serializable{

    public String getNombre() { return "tarjeta"; }

    @Override
    public double calcularMontoFinal(double importe, int cuotas) {
        if (cuotas > 1){
            return importe * 1.07;
        }
        return importe;
    }
}

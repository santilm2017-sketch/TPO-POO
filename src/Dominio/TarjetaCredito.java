package Dominio;

public class TarjetaCredito implements MedioPago{
    @Override
    public double calcularMontoFinal(double importe, int cuotas) {
        if (cuotas > 1){
            return importe * 1.07;
        }
        return importe;
    }
}

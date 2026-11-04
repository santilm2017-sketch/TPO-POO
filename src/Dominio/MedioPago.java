package Dominio;

public interface MedioPago {
    String getNombre();
    double calcularMontoFinal(double importe, int cuotas);
}

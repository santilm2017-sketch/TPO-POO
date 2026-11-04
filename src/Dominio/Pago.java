package Dominio;

import java.io.Serializable;

public class Pago implements Serializable {
    private String fecha;
    private double importe;
    private int cuotas;
    private MedioPago medioPago;

    public Pago(String fecha, double importe, MedioPago medioPago) {
        this(fecha, importe, medioPago, 1);
    }

    public Pago(String fecha, double importe, MedioPago medioPago, int cuotas) {
        new Fecha(fecha);
        if (medioPago == null || cuotas < 1) throw new IllegalArgumentException("Medio y cuotas inválidos.");
        this.cuotas = cuotas;
        if (!Importes.esValido(importe) || importe == 0) {
            throw new IllegalArgumentException("El importe del pago debe ser mayor a cero.");
        }
        this.fecha = fecha;
        this.importe = Importes.redondear(importe);
        if (this.importe != importe) throw new IllegalArgumentException("Use hasta dos decimales en el pago.");
        this.medioPago = medioPago;
    }

    public String getFecha() {
        return fecha;
    }

    public double getImporte() {
        return importe;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public double getMontoCobrado() {
        return Importes.redondear(medioPago.calcularMontoFinal(importe, cuotas));
    }
}

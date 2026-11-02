package Dominio;

import java.io.Serializable;

public class Pago implements Serializable {
    private String fecha;
    private double importe;
    private MedioPago medioPago;

    public Pago(String fecha, double importe, MedioPago medioPago) {
        if (importe <= 0) {
            throw new IllegalArgumentException("El importe del pago debe ser mayor a cero.");
        }
        this.fecha = fecha;
        this.importe = importe;
        this.medioPago = medioPago;
    }

    // --- GETTERS Y SETTERS ---

    public String getFecha() {
        return fecha;
    }

    public double getImporte() {
        return importe;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }
}
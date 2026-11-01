package Dominio;

public class CancelacionReserva implements java.io.Serializable {
    private String fecha;
    private String motivo;
    private double importeDevuelto;

    public CancelacionReserva(String fecha, String motivo, double importeDevuelto) {
        if (importeDevuelto < 0) {
            throw new IllegalArgumentException("El importe devuelto no puede ser negativo.");
        }
        this.fecha = fecha;
        this.motivo = motivo;
        this.importeDevuelto = importeDevuelto;
    }

    public String getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public double getImporteDevuelto() {
        return importeDevuelto;
    }
}

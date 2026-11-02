package Dominio;

public class ContratacionServicio implements java.io.Serializable {
    private String fecha;
    private Pasajero pasajero;
    private ServicioAdicional servicio;

    public ContratacionServicio(Pasajero pasajero, ServicioAdicional servicio) {
        if (pasajero == null || servicio == null) {
            throw new IllegalArgumentException("Pasajero y servicio son obligatorios.");
        }
        this.pasajero = pasajero;
        this.servicio = servicio;
    }

    public ContratacionServicio(Pasajero pasajero, ServicioAdicional servicio, String fecha) {
        this(pasajero, servicio);
        this.fecha = fecha;
    }

    public double calcularImporte() {
        return servicio.getPrecio();
    }

    public String getFecha() {
        return fecha;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public ServicioAdicional getServicio() {
        return servicio;
    }
}

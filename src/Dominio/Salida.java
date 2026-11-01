package Dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Salida implements Serializable {
    private Tour tour;
    private String fechaInicio;
    private String fechaFin;
    private String estado; // "PROGRAMADA", "EN_CURSO", "FINALIZADA", "CANCELADA"
    private int cupoMaximo;
    private int cuposReservados;
    private Guia guia;
    private Vehiculo vehiculo;
    private List<Reserva> reservas;

    public Salida(Tour tour, String fechaInicio, String fechaFin, int cupoMaximo) {
        if (tour == null) {
            throw new IllegalArgumentException("El tour no puede ser nulo.");
        }
        this.tour = tour;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupoMaximo = cupoMaximo > 0 ? cupoMaximo : tour.getCupoMaximo();
        this.cuposReservados = 0;
        this.estado = "PROGRAMADA";
        this.reservas = new ArrayList<>();
    }

    // --- MÉTODOS DE NEGOCIO ---

    public boolean verificarCupoDisponible(int cantidad) {
        return (cuposReservados + cantidad) <= cupoMaximo;
    }

    public void ocuparCupos(int cantidad) {
        if (!verificarCupoDisponible(cantidad)) {
            throw new IllegalStateException("No hay suficientes cupos disponibles.");
        }
        this.cuposReservados += cantidad;
    }

    public void liberarCupos(int cantidad) {
        this.cuposReservados -= cantidad;
        if (this.cuposReservados < 0) {
            this.cuposReservados = 0;
        }
    }

    public void registrarReserva(Reserva reserva) {
        if (reserva != null) {
            this.reservas.add(reserva);
        }
    }

    public void asignarGuia(Guia guia) {
        this.guia = guia;
    }

    public void asignarVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    /**
     * Calcula el total acumulado en servicios adicionales vendidos en esta salida (Experto en Información)
     */
    public double calcularTotalAdicionalesVendidos() {
        double total = 0.0;
        for (Reserva r : reservas) {
            if (r != null && !"CANCELADA".equalsIgnoreCase(r.getEstado())) {
                for (Pasajero p : r.getPasajeros()) {
                    for (ContratacionServicio cs : p.getContrataciones()) {
                        total += cs.calcularImporte();
                    }
                }
            }
        }
        return total;
    }

    // --- GETTERS Y SETTERS ---

    public Tour getTour() {
        return tour;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public int getCuposReservados() {
        return cuposReservados;
    }

    public Guia getGuia() {
        return guia;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public List<Reserva> getReservas() {
        return new ArrayList<>(reservas);
    }
}
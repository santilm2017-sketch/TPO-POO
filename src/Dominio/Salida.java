package Dominio;

import java.util.Comparator;
import Dominio.Excepciones.CupoSuperadoException;

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
    private boolean listaEmitida;
    private Guia guia;
    private Vehiculo vehiculo;
    private List<Reserva> reservas;

    public Salida(Tour tour, String fechaInicio, String fechaFin, int cupoMaximo) {
        if (tour == null) {
            throw new IllegalArgumentException("El tour no puede ser nulo.");
        }
        Fecha inicio = new Fecha(fechaInicio);
        Fecha fin = new Fecha(fechaFin);
        if (inicio.diasHasta(fin) + 1 != tour.getDuracionDias()
                || cupoMaximo < tour.getCupoMinimo() || cupoMaximo > tour.getCupoMaximo()) {
            throw new IllegalArgumentException("Las fechas y el cupo deben corresponder al tour.");
        }
        this.tour = tour;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.cupoMaximo = cupoMaximo;
        this.cuposReservados = 0;
        this.estado = "PROGRAMADA";
        this.reservas = new ArrayList<>();
    }

    // --- MÉTODOS DE NEGOCIO ---

    public String getId() { return tour.getCodigo() + "_" + Fecha.mostrar(fechaInicio); }

    public boolean abierta() {
        return "PROGRAMADA".equals(estado) || "CONFIRMADA".equals(estado) || "COMPLETA".equals(estado);
    }

    public boolean verificarCupoDisponible(int cantidad) {
        return abierta() && cantidad > 0 && cantidad <= cupoMaximo - cuposReservados;
    }

    void actualizarOcupacion() {
        cuposReservados = 0;
        for (Reserva reserva : reservas) {
            if (reserva.vigente()) cuposReservados += reserva.getPasajeros().size();
        }
        listaEmitida = false;
        if (abierta()) {
            if (cuposReservados >= cupoMaximo) estado = "COMPLETA";
            else if (cuposReservados >= tour.getCupoMinimo()) estado = "CONFIRMADA";
            else estado = "PROGRAMADA";
        }
    }

    public void registrarReserva(Reserva reserva) {
        if (reserva.getSalida() != this || !verificarCupoDisponible(reserva.getPasajeros().size())) {
            throw new CupoSuperadoException("No hay cupo para registrar la reserva.");
        }
        reservas.add(reserva);
        actualizarOcupacion();
    }

    public void asignarGuia(Guia guia) {
        if (!abierta() || !guia.estaHabilitado(tour)) throw new RuntimeException("Salida cerrada o guía sin habilitación.");
        this.guia = guia;
    }

    public void asignarVehiculo(Vehiculo vehiculo) {
        if (!abierta() || !vehiculo.verificarCapacidad(cupoMaximo)) throw new RuntimeException("Salida cerrada o capacidad insuficiente.");
        this.vehiculo = vehiculo;
    }

    public String emitirListaPasajeros() {
        if (!abierta()) throw new RuntimeException("La salida no está abierta.");
        List<Pasajero> lista = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.vigente()) {
                for (Pasajero p : reserva.getPasajeros()) lista.add(p);
            }
        }
        lista.sort(Comparator.comparing(Pasajero::getNombre));
        String texto = "Pasajeros de " + getId();
        for (Pasajero p : lista) {
            texto += "\n" + p.getNombre() + " | " + p.getDocumento() + " | "
                    + p.getContactoEmergencia() + " | " + p.getRestricciones();
        }
        listaEmitida = true;
        return texto;
    }

    public void iniciar() {
        if (!abierta() || cuposReservados < tour.getCupoMinimo() || !listaEmitida || guia == null || vehiculo == null) {
            throw new RuntimeException("Se requieren cupo mínimo, lista vigente, guía y vehículo.");
        }
        for (Reserva r : reservas) {
            if (r.vigente() && r.saldoPendiente() > 0) throw new RuntimeException("Hay pasajeros con saldo pendiente.");
        }
        estado = "EN_CURSO";
    }

    public void cerrar() {
        if (!"EN_CURSO".equals(estado)) throw new RuntimeException("La salida debe estar en curso.");
        estado = "FINALIZADA";
        for (Reserva reserva : reservas) reserva.finalizar();
    }

    public void controlarPlazos(Fecha hoy) {
        if (!abierta()) return;
        long dias = hoy.diasHasta(new Fecha(fechaInicio));
        if (dias < 10) {
            for (Reserva r : reservas) {
                if (r.vigente() && r.saldoPendiente() > 0) r.anularPorSaldo(hoy);
            }
        }
        if (dias <= 7 && cuposReservados < tour.getCupoMinimo()) {
            for (Reserva r : reservas) {
                if (r.vigente()) r.cancelar(hoy, "Cupo mínimo no alcanzado", true);
            }
            estado = "CANCELADA";
        }
    }

    /**
     * Calcula el total acumulado en servicios adicionales vendidos en esta salida (Experto en Información)
     */
    public double calcularTotalAdicionalesVendidos() {
        double total = 0.0;
        for (Reserva r : reservas) {
            if (r != null && r.vigente()) {
                for (Pasajero p : r.getPasajeros()) {
                    for (ContratacionServicio cs : p.getContrataciones()) {
                        total += cs.calcularImporte();
                    }
                }
            }
        }
        return total;
    }

    public void vincularTour(Tour catalogo) {
        if (!tour.getCodigo().equals(catalogo.getCodigo())) throw new IllegalArgumentException("Tour incorrecto.");
        tour = catalogo;
    }

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
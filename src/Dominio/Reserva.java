package Dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Reserva implements Serializable {
    private static int contadorReservas = 1;
    private int numero;
    private String fecha; // Formato YYYY-MM-DD
    private String estado; // "PENDIENTE", "CONFIRMADA", "ANULADA", "CANCELADA", "FINALIZADA"
    private double total;
    private Cliente cliente;
    private Vendedor vendedor;
    private Salida salida;
    private List<Pasajero> pasajeros;
    private List<Pago> pagos;

    public Reserva(String fecha, Cliente cliente, Vendedor vendedor, Salida salida) {
        if (cliente == null || salida == null) {
            throw new IllegalArgumentException("El cliente y la salida son obligatorios para crear una reserva.");
        }
        this.numero = contadorReservas++;
        this.fecha = fecha;
        this.estado = "PENDIENTE";
        this.total = 0.0;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.salida = salida;
        this.pasajeros = new ArrayList<>();
        this.pagos = new ArrayList<>();

        // Asocia la reserva a la salida correspondientemente
        salida.registrarReserva(this);
    }

    // --- MÉTODOS DE NEGOCIO ---

    public void agregarPasajero(Pasajero pasajero) {
        if (pasajero != null) {
            this.pasajeros.add(pasajero);
        }
    }

    public void agregarPago(Pago pago) {
        if (pago != null) {
            this.pagos.add(pago);
            verificarEstadoPago();
        }
    }

    public double calcularMontoPagado() {
        double monto = 0.0;
        for (Pago p : pagos) {
            if (p != null) {
                monto += p.getImporte();
            }
        }
        return monto;
    }

    private void verificarEstadoPago() {
        if (total > 0) {
            double pagado = calcularMontoPagado();
            // Se confirma al alcanzar el 30% de seña (RN14)
            if (pagado >= total * 0.30 && "PENDIENTE".equalsIgnoreCase(estado)) {
                this.estado = "CONFIRMADA";
            }
        }
    }

    // --- GETTERS Y SETTERS ---

    public int getNumero() {
        return numero;
    }

    public String getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public Salida getSalida() {
        return salida;
    }

    public List<Pasajero> getPasajeros() {
        return new ArrayList<>(pasajeros);
    }

    public List<Pago> getPagos() {
        return new ArrayList<>(pagos);
    }
}
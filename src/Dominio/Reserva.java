package Dominio;

import Dominio.Excepciones.RequisitoPasajeroException;
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
    private CancelacionReserva cancelacion;
    private List<String> notas = new ArrayList<>();
    private boolean registrada;

    public Reserva(String fecha, Cliente cliente, Vendedor vendedor, Salida salida) {
        if (cliente == null || salida == null || vendedor == null) {
            throw new IllegalArgumentException("El cliente y la salida son obligatorios para crear una reserva.");
        }
        new Fecha(fecha);
        this.numero = contadorReservas++;
        this.fecha = fecha;
        this.estado = "PENDIENTE";
        this.total = 0.0;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.salida = salida;
        this.pasajeros = new ArrayList<>();
        this.pagos = new ArrayList<>();

    }

    public static void continuarNumeracion(int ultimo) {
        if (contadorReservas <= ultimo) contadorReservas = ultimo + 1;
    }

    public void confirmarAlta() {
        if (registrada) throw new RuntimeException("Reserva ya registrada.");
        salida.registrarReserva(this);
        registrada = true;
        verificarEstadoPago();
    }

    public void agregarPasajero(Pasajero pasajero) {
        if (registrada || pasajero == null) throw new RuntimeException("No se puede agregar el pasajero.");
        for (Pasajero p : pasajeros) {
            if (p.getDocumento().toLowerCase().equals(pasajero.getDocumento().toLowerCase())) throw new IllegalArgumentException("Pasajero duplicado.");
        }
        pasajeros.add(pasajero);
    }

    public boolean vigente() {
        return !"CANCELADA".equals(estado) && !"ANULADA".equals(estado);
    }

    private void exigirAbierta() {
        if (!vigente() || !salida.abierta()) throw new RuntimeException("La reserva está cerrada.");
    }

    public void validarPasajero(Pasajero p) throws RequisitoPasajeroException {
        int edad = p.edadEn(salida.getFechaInicio());
        String tipo = p.getTipo().toLowerCase();
        boolean categoriaValida;
        if (edad < 3) categoriaValida = tipo.equals("menor_3") || tipo.equals("bebe");
        else if (edad < 12) categoriaValida = tipo.equals("menor_12") || tipo.equals("menor");
        else categoriaValida = tipo.equals("adulto") || tipo.equals("jubilado");
        if (p.getFechaNacimiento().compareTo(salida.getFechaInicio()) > 0 || !categoriaValida
                || !salida.getTour().validarRequisitosPasajero(p, salida.getFechaInicio())) {
            throw new RequisitoPasajeroException("Categoría o requisitos del pasajero inválidos.");
        }
    }

    public void reemplazar(String documento, Pasajero nuevo, Fecha hoy)
            throws RequisitoPasajeroException {
        exigirAbierta();
        if (diasHastaSalida(hoy) < 3) throw new RuntimeException("Venció el plazo de reemplazo.");
        Pasajero anterior = buscarPasajero(documento);
        for (Pasajero p : pasajeros) {
            if (p != anterior && p.getDocumento().toLowerCase().equals(nuevo.getDocumento().toLowerCase())) throw new IllegalArgumentException("Documento duplicado.");
        }
        for (ContratacionServicio c : anterior.getContrataciones()) nuevo.contratarServicio(c.getServicio(), c.getFecha());
        validarPasajero(nuevo);
        pasajeros.set(pasajeros.indexOf(anterior), nuevo);
        if (!pagos.isEmpty()) anotar("Reemplazo " + documento + " por " + nuevo.getDocumento(), hoy);
        salida.actualizarOcupacion();
    }

    public Pasajero buscarPasajero(String documento) {
        for (Pasajero p : pasajeros) if (p.getDocumento().toLowerCase().equals(documento.toLowerCase())) return p;
        throw new IllegalArgumentException("Pasajero inexistente.");
    }

    public void contratar(String documento, ServicioAdicional servicio, Fecha hoy) {
        exigirAbierta();
        if (diasHastaSalida(hoy) < 10) throw new RuntimeException("Venció el plazo de pago.");
        buscarPasajero(documento).contratarServicio(servicio, hoy.toString());
        total = Importes.redondear(total + servicio.getPrecio());
        verificarEstadoPago();
    }

    public void ajustar(String motivo, double diferencia, Fecha hoy) {
        exigirAbierta();
        if (pagos.isEmpty() || motivo.isBlank() || !Importes.esValido(total + diferencia)
                || Importes.redondear(total + diferencia) < calcularMontoPagado()) {
            throw new IllegalArgumentException("El ajuste requiere pago previo, motivo y total que cubra lo abonado.");
        }
        anotar(motivo + " | total anterior $ " + Importes.mostrar(total) + " | diferencia $ " + Importes.mostrar(diferencia), hoy);
        total = Importes.redondear(total + diferencia);
        verificarEstadoPago();
    }

    private void anotar(String texto, Fecha hoy) {
        notas.add(Fecha.mostrar(hoy.toString()) + " | " + texto);
    }

    public List<String> getNotas() {
        List<String> resultado = new ArrayList<>();
        for (String nota : notas) {
            if (nota.length() >= 10 && nota.charAt(4) == '-' && nota.charAt(7) == '-') {
                nota = Fecha.mostrar(nota.substring(0, 10)) + nota.substring(10, nota.length());
            }
            resultado.add(nota);
        }
        return resultado;
    }

    public int diasHastaSalida(Fecha fechaOperacion) {
        return fechaOperacion.diasHasta(new Fecha(salida.getFechaInicio()));
    }

    public void cancelar(Fecha hoy, String motivo, boolean porAgencia) {
        registrarCancelacion(hoy, motivo, porAgencia, diasHastaSalida(hoy));
    }

    private void registrarCancelacion(Fecha hoy, String motivo, boolean porAgencia, int anticipacion) {
        exigirAbierta();
        if (motivo == null || motivo.isBlank()) throw new IllegalArgumentException("Falta motivo.");
        PoliticaCancelacion politica = porAgencia ? new PoliticaCancelacionAgencia() : new PoliticaCancelacionCliente();
        cancelacion = new CancelacionReserva(hoy.toString(), motivo, 0);
        for (Pago pago : pagos) {
            double importe = politica.calcularDevolucion(pago.getMontoCobrado(), anticipacion);
            cancelacion.devolver(pago, Importes.redondear(importe));
        }
        estado = "CANCELADA";
        salida.actualizarOcupacion();
    }

    void anularPorSaldo(Fecha hoy) {
        // El saldo venció al pasar del día 10 al 9, aunque la agencia abra más tarde.
        registrarCancelacion(hoy, "Saldo vencido", false, 9);
        estado = "ANULADA";
    }

    void finalizar() { if (vigente()) estado = "FINALIZADA"; }

    public CancelacionReserva getCancelacion() { return cancelacion; }

    public Pago registrarPago(String fecha, double importe, MedioPago medio, int cuotas) {
        Pago pago = new Pago(fecha, importe, medio, cuotas);
        agregarPago(pago);
        return pago;
    }

    public void agregarPago(Pago pago) {
        exigirAbierta();
        if (pago == null || pago.getImporte() > saldoPendiente() || pago.getFecha().compareTo(fecha) < 0
                || diasHastaSalida(new Fecha(pago.getFecha())) < 10) {
            throw new IllegalArgumentException("Pago fuera de plazo, anterior a la reserva o superior al saldo.");
        }
        pagos.add(pago);
        verificarEstadoPago();
    }

    public double saldoPendiente() { return Importes.redondear(total - calcularMontoPagado()); }

    public double calcularMontoPagado() {
        double monto = 0.0;
        for (Pago p : pagos) {
            if (p != null) {
                monto += p.getImporte();
            }
        }
        return Importes.redondear(monto);
    }

    private void verificarEstadoPago() {
        estado = calcularMontoPagado() >= Importes.redondear(total * 0.30) ? "CONFIRMADA" : "PENDIENTE";
    }

    public int getNumero() {
        return numero;
    }

    public String getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public double getTotal() {
        return total;
    }

    void setTotal(double total) {
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
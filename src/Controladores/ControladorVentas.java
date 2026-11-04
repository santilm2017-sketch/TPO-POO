package Controladores;

import Dominio.Excepciones.CupoSuperadoException;
import Dominio.Excepciones.RequisitoPasajeroException;

import Dominio.*;
import Persistencia.*;

public class ControladorVentas {
    private Repositorio<Cliente> clienteRepo;
    private Repositorio<Reserva> reservaRepo;
    private Repositorio<Salida> salidaRepo;
    private CalculadoraPrecioReserva calculadoraPrecio;

    public ControladorVentas(Repositorio<Cliente> clientes, Repositorio<Salida> salidas) {
        this.clienteRepo = clientes;
        this.salidaRepo = salidas;
        this.reservaRepo = new RepositorioReservas(salidas);
        this.calculadoraPrecio = new CalculadoraPrecioReserva();
    }

    public void registrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        if (clienteRepo.buscarPorId(cliente.getDocumento()) != null) {
            throw new RuntimeException("Ya existe un cliente registrado con el documento: " + cliente.getDocumento());
        }
        clienteRepo.guardar(cliente);
    }

    public Cliente buscarCliente(String documento) {
        return clienteRepo.buscarPorId(documento);
    }

    public void crearReserva(Reserva reserva, String temporada) throws RequisitoPasajeroException {
        if (reserva == null || reserva.getSalida() == null) {
            throw new IllegalArgumentException("La reserva o su salida asociada no pueden ser nulas.");
        }

        Salida salida = reserva.getSalida();
        int cantidadPasajeros = reserva.getPasajeros().size();

        if (salidaRepo.buscarPorId(salida.getId()) != salida || buscarCliente(reserva.getCliente().getDocumento()) == null) {
            throw new IllegalArgumentException("La salida y el titular deben estar registrados.");
        }
        if (reserva.diasHastaSalida(new Fecha(reserva.getFecha())) < 10) throw new RuntimeException("Plazo de pago vencido.");
        if (!salida.verificarCupoDisponible(cantidadPasajeros)) {
            throw new CupoSuperadoException("Cantidad de pasajeros inválida o cupo insuficiente.");
        }
        for (Pasajero p : reserva.getPasajeros()) reserva.validarPasajero(p);
        calculadoraPrecio.calcularTotalReserva(reserva, temporada);
        reservaRepo.guardar(reserva);
    }

    public Pago cobrar(int numero, Fecha fecha, double importe, MedioPago medio, int cuotas) {
        Reserva r = exigirReserva(numero);
        Pago pago = r.registrarPago(fecha.toString(), importe, medio, cuotas);
        reservaRepo.actualizar(r);
        return pago;
    }

    public void contratar(int numero, String documento, ServicioAdicional servicio, Fecha fecha) {
        Reserva r = exigirReserva(numero);
        r.contratar(documento, servicio, fecha);
        reservaRepo.actualizar(r);
    }

    public void reemplazar(int numero, String documento, Pasajero nuevo, Fecha fecha)
            throws RequisitoPasajeroException {
        Reserva r = exigirReserva(numero);
        r.reemplazar(documento, nuevo, fecha);
        reservaRepo.actualizar(r);
    }

    public void ajustar(int numero, String motivo, double diferencia, Fecha fecha) {
        Reserva r = exigirReserva(numero);
        r.ajustar(motivo, diferencia, fecha);
        reservaRepo.actualizar(r);
    }

    public CancelacionReserva cancelarReserva(int numero, String motivo, boolean porAgencia, Fecha fecha) {
        Reserva r = exigirReserva(numero);
        r.cancelar(fecha, motivo, porAgencia);
        reservaRepo.actualizar(r);
        return r.getCancelacion();
    }

    public Reserva exigirReserva(int numero) {
        Reserva r = reservaRepo.buscarPorId("" + numero);
        if (r == null) throw new IllegalArgumentException("Reserva inexistente.");
        return r;
    }

}

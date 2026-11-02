package Controladores;

import Dominio.*;
import Persistencia.*;
import java.util.List;

public class ControladorVentas {
    private IRepository<Cliente> clienteRepo;
    private IRepository<Reserva> reservaRepo;
    private IRepository<Salida> salidaRepo;
    private CalculadoraPrecioReserva calculadoraPrecio;

    public ControladorVentas() {
        this.clienteRepo = new ClienteRepository();
        this.reservaRepo = new ReservaRepository();
        this.salidaRepo = new SalidaRepository();
        this.calculadoraPrecio = new CalculadoraPrecioReserva();
    }

    public void registrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        if (clienteRepo.buscarPorId(cliente.getDocumento()) != null) {
            throw new IllegalStateException("Ya existe un cliente registrado con el documento: " + cliente.getDocumento());
        }
        clienteRepo.guardar(cliente);
    }

    public Cliente buscarCliente(String documento) {
        return clienteRepo.buscarPorId(documento);
    }

    public List<Cliente> obtenerTodosClientes() {
        return clienteRepo.obtenerTodos();
    }

    public void crearReserva(Reserva reserva, String temporada) {
        if (reserva == null || reserva.getSalida() == null) {
            throw new IllegalArgumentException("La reserva o su salida asociada no pueden ser nulas.");
        }

        Salida salida = reserva.getSalida();
        int cantidadPasajeros = reserva.getPasajeros().size();

        // Verificar cupos en la salida (RN)
        if (!salida.verificarCupoDisponible(cantidadPasajeros)) {
            throw new IllegalStateException("No hay cupos suficientes disponibles para esa salida.");
        }

        // Calcular precio final mediante SRP/Calculadora (RN10, RN11, RN12, RN13)
        calculadoraPrecio.calcularTotalReserva(reserva, temporada);

        // Ocupar cupos en la salida y persistir actualización
        salida.ocuparCupos(cantidadPasajeros);
        salidaRepo.actualizar(salida);

        // Guardar la reserva
        reservaRepo.guardar(reserva);
    }

    public void registrarPago(int numeroReserva, Pago pago) {
        Reserva reserva = reservaRepo.buscarPorId(String.valueOf(numeroReserva));
        if (reserva == null) {
            throw new IllegalArgumentException("No se encontro ninguna reserva con el numero: " + numeroReserva);
        }

        reserva.agregarPago(pago);
        reservaRepo.actualizar(reserva);
    }

    public void cancelarReserva(int numeroReserva, String motivo, boolean porAgencia) {
        Reserva reserva = reservaRepo.buscarPorId(String.valueOf(numeroReserva));
        if (reserva == null) {
            throw new IllegalArgumentException("No se encontro ninguna reserva con el numero: " + numeroReserva);
        }

        reserva.setEstado("CANCELADA");

        // Liberar los cupos ocupados por los pasajeros
        Salida salida = reserva.getSalida();
        if (salida != null) {
            salida.liberarCupos(reserva.getPasajeros().size());
            salidaRepo.actualizar(salida);
        }

        reservaRepo.actualizar(reserva);
    }

    public Reserva buscarReserva(int numero) {
        return reservaRepo.buscarPorId(String.valueOf(numero));
    }

    public List<Reserva> obtenerTodasReservas() {
        return reservaRepo.obtenerTodos();
    }
}

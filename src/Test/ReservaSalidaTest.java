package Test;

import Dominio.*;
import Dominio.Excepciones.CupoSuperadoException;
import Dominio.Excepciones.RequisitoPasajeroException;

public class ReservaSalidaTest {
    public static void main(String[] args) throws RequisitoPasajeroException {
        probarPagos();
        probarCupos();
        probarOperacion();
        System.out.println("=== PRUEBAS DE RESERVAS Y SALIDAS COMPLETADAS ===");
    }

    private static Salida salida(int cupo) {
        Tour tour = new TourCultural("T", "Museos", "", 1, 1, cupo);
        tour.agregarTarifa(new Tarifa("baja", "2028-01-01", 100));
        return new Salida(tour, "2028-03-01", "2028-03-01", cupo);
    }

    private static Pasajero adulto(String documento) {
        return new Pasajero(documento, "Ana", "1990-01-01", "adulto", "111", "Ninguna");
    }

    private static Reserva reserva(Salida salida, Pasajero pasajero) {
        Reserva reserva = new Reserva("2028-02-01", new Cliente("C1", "Ana", "", ""),
                new Vendedor("V1", "Luis", "", "", "V1"), salida);
        reserva.agregarPasajero(pasajero);
        new CalculadoraPrecioReserva().calcularTotalReserva(reserva, "baja");
        return reserva;
    }

    private static void probarPagos() {
        Reserva reserva = reserva(salida(2), adulto("P1"));
        reserva.confirmarAlta();
        boolean rechazado = false;
        try { reserva.registrarPago("2028-02-01", 101, new Efectivo(), 1); }
        catch (IllegalArgumentException e) { rechazado = true; }
        verificar(rechazado && reserva.getPagos().isEmpty() && reserva.saldoPendiente() == 100,
                "Sobrepago rechazado sin alterar saldo");
        rechazado = false;
        try { reserva.registrarPago("2028-02-01", -1, new Efectivo(), 1); }
        catch (IllegalArgumentException e) { rechazado = true; }
        verificar(rechazado && reserva.getPagos().isEmpty(), "Pago negativo rechazado");
        rechazado = false;
        try { reserva.registrarPago("2028-01-31", 10, new Efectivo(), 1); }
        catch (IllegalArgumentException e) { rechazado = true; }
        verificar(rechazado && reserva.getPagos().isEmpty(), "Pago anterior a la reserva rechazado");
        rechazado = false;
        try { reserva.registrarPago("2028-02-21", 10, new Efectivo(), 1); }
        catch (IllegalArgumentException e) { rechazado = true; }
        verificar(rechazado && reserva.getPagos().isEmpty(), "Pago a nueve dias rechazado");
        Pago pago = reserva.registrarPago("2028-02-20", 30, new TarjetaCredito(), 2);
        verificar(pago.getMontoCobrado() == 32.1 && reserva.saldoPendiente() == 70,
                "El recargo no reduce el capital pendiente");
        verificar(reserva.getEstado().equals("CONFIRMADA"), "Se confirma con el treinta por ciento");
        reserva.cancelar(new Fecha("2028-02-20"), "No viaja", false);
        verificar(reserva.getCancelacion().getImporteDevuelto() == 6.42
                && reserva.getSalida().getCuposReservados() == 0, "Devuelve veinte por ciento y libera cupo");
        rechazado = false;
        try { reserva.cancelar(new Fecha("2028-02-20"), "Repetida", false); }
        catch (RuntimeException e) { rechazado = true; }
        verificar(rechazado && reserva.getCancelacion().getImporteDevuelto() == 6.42,
                "No permite cancelar dos veces");
    }

    private static void probarCupos() {
        Salida salida = salida(1);
        Reserva bebe = reserva(salida, new Pasajero("B1", "Bebe", "2027-01-01", "menor_3", "111", ""));
        bebe.confirmarAlta();
        verificar(bebe.getTotal() == 0 && salida.getCuposReservados() == 1,
                "El bebe ocupa lugar aunque no pague pasaje");
        Reserva otra = reserva(salida, adulto("P2"));
        boolean rechazado = false;
        try { otra.confirmarAlta(); }
        catch (CupoSuperadoException e) { rechazado = true; }
        verificar(rechazado && salida.getReservas().size() == 1 && salida.getCuposReservados() == 1,
                "Sobreventa rechazada sin modificar la salida");
        verificar(!salida.verificarCupoDisponible(0), "No admite reserva sin pasajeros");
    }

    private static void probarOperacion() throws RequisitoPasajeroException {
        Salida salida = salida(2);
        Reserva reserva = reserva(salida, adulto("P1"));
        reserva.confirmarAlta();
        reserva.contratar("P1", new ServicioAdicional("Traslado", 100, false), new Fecha("2028-02-01"));
        reserva.registrarPago("2028-02-01", 200, new Transferencia(), 1);
        Guia guia = new Guia("G1", "Julia", "", "", "M1", 500);
        boolean rechazado = false;
        try { salida.asignarGuia(guia); }
        catch (RuntimeException e) { rechazado = true; }
        verificar(rechazado && salida.getGuia() == null, "Guia sin habilitacion rechazado");
        guia.habilitarCategoria("cultural");
        salida.asignarGuia(guia);
        rechazado = false;
        try { salida.asignarVehiculo(new Vehiculo("AA1", 1, "Auto")); }
        catch (RuntimeException e) { rechazado = true; }
        verificar(rechazado && salida.getVehiculo() == null, "Vehiculo insuficiente rechazado");
        salida.asignarVehiculo(new Vehiculo("AA2", 2, "Auto"));
        rechazado = false;
        try { salida.cerrar(); }
        catch (RuntimeException e) { rechazado = true; }
        verificar(rechazado && salida.abierta(), "No se cierra antes de iniciar");
        rechazado = false;
        try { guia.calcularHonorarios(salida); }
        catch (RuntimeException e) { rechazado = true; }
        verificar(rechazado, "No se liquida antes del cierre");
        salida.emitirListaPasajeros();
        reserva.reemplazar("P1", adulto("P2"), new Fecha("2028-02-27"));
        verificar(reserva.getTotal() == 200 && reserva.buscarPasajero("P2").getContrataciones().size() == 1
                && salida.getCuposReservados() == 1, "Reemplazo a tres dias conserva precio, adicional y cupo");
        rechazado = false;
        try { reserva.reemplazar("P2", adulto("P3"), new Fecha("2028-02-28")); }
        catch (RuntimeException e) { rechazado = true; }
        verificar(rechazado && reserva.buscarPasajero("P2") != null, "Reemplazo a dos dias rechazado");
        rechazado = false;
        try { salida.iniciar(); }
        catch (RuntimeException e) { rechazado = true; }
        verificar(rechazado && salida.abierta(), "Reemplazo obliga a emitir nuevamente la lista");
        String lista = salida.emitirListaPasajeros();
        verificar(lista.contains("P2") && !lista.contains("P1"), "Lista con el pasajero nuevo");
        salida.iniciar();
        verificar(salida.getEstado().equals("EN_CURSO"), "Inicio con requisitos completos");
        salida.cerrar();
        verificar(salida.getEstado().equals("FINALIZADA") && reserva.getEstado().equals("FINALIZADA"),
                "Cierre finaliza salida y reserva");
        verificar(guia.calcularHonorarios(salida) == 505, "Honorarios diarios mas cinco por ciento de adicionales");
    }

    private static void verificar(boolean correcto, String caso) {
        if (!correcto) throw new RuntimeException("Fallo: " + caso);
        System.out.println("[OK] " + caso);
    }
}

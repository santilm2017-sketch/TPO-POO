package Test;

import Dominio.*;
import Controladores.*;
import Persistencia.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

// Ejecutar en una carpeta temporal nueva: esta prueba guarda y recupera archivos.
public class MejorasFinalTest {
    private static int aprobadas;
    private static Pasajero adulto(String doc) { return new Pasajero(doc, "Pasajero", "1990-01-01", "adulto", "111", ""); }
    private static List<Pasajero> lista(Pasajero p) { List<Pasajero> lista = new ArrayList<>(); lista.add(p); return lista; }
    private static void verificar(boolean valor, String caso) {
        if (!valor) throw new RuntimeException("Fallo: " + caso);
        aprobadas++; System.out.println("[OK] " + caso);
    }
    public static void main(String[] args) throws Exception {
        if (new File("datos").exists()) throw new RuntimeException("Use una carpeta nueva sin datos.");
        for (String doc : new String[]{"ABC", "12 34", "12-34", "", "12.3"}) {
            boolean error = false;
            try { new Cliente(doc, "Ana", "ana@ejemplo.com", "111"); } catch (IllegalArgumentException e) { error = true; }
            verificar(error, "Documento inválido rechazado: " + doc);
        }
        for (String telefono : new String[]{"telefono", "12 34", "+", ""}) {
            boolean error = false;
            try { new Cliente("123", "Ana", "ana@ejemplo.com", telefono); } catch (IllegalArgumentException e) { error = true; }
            verificar(error, "Teléfono inválido rechazado: " + telefono);
        }
        verificar(new Cliente("123", "Ana", "ana@ejemplo.com", "+54111234").getTelefono().equals("+54111234"), "Teléfono internacional permitido");
        boolean error = false;
        try { new Cliente("123", "Ana", "sin-arroba", "111"); } catch (IllegalArgumentException e) { error = true; }
        verificar(error, "Email inválido rechazado");
        error = false;
        try { new Pasajero("ABC", "Ana", "1990-01-01", "adulto", "111", ""); } catch (IllegalArgumentException e) { error = true; }
        verificar(error, "Documento del pasajero validado en dominio");
        verificar(new Guia("G-SALTA", "Julia", "", "111", "G1", 100).getDocumento().equals("G-SALTA"), "Código interno del guía conservado");
        error = false;
        try { new PersonalAdministrativo("ABC", "Ana", "", "111", "A1", "administrador"); } catch (IllegalArgumentException e) { error = true; }
        verificar(error, "Documento administrativo numérico");
        Cliente c = new Cliente("123", "Ana", "ana@ejemplo.com", "111", "Argentina");
        error = false;
        try { c.setTelefono("texto"); } catch (IllegalArgumentException e) { error = true; }
        verificar(error && c.getTelefono().equals("111"), "Setter inválido conserva teléfono");
        RepositorioClientes clientes = new RepositorioClientes(); RepositorioTours tours = new RepositorioTours(); RepositorioSalidas salidas = new RepositorioSalidas();
        clientes.guardar(c);
        verificar(new RepositorioClientes().buscarPorId("123").getNombre().equals("Ana"), "Recupera cliente existente");
        ControladorVentas ventas = new ControladorVentas(clientes, salidas);
        error = false;
        try { ventas.registrarCliente(c); } catch (RuntimeException e) { error = true; }
        verificar(error && clientes.obtenerTodos().size() == 1, "Cliente duplicado no se registra");
        Tour tour = new TourCultural("T", "Museos", "", 1, 1, 10);
        tour.agregarTarifa(new Tarifa("baja", "2028-01-01", 100));
        tour.agregarActividad(new Actividad(1, 1, "09:00", "Visita", "Museo", null)); tours.guardar(tour);
        Salida salida = new Salida(tour, "2028-03-01", "2028-03-01", 10); salidas.guardar(salida);
        Reserva r = new Reserva("2028-02-01", c, new Vendedor("V1", "Luis", "", "111", "V1"), salida);
        for (int i = 1; i <= 5; i++) r.agregarPasajero(adulto("100" + i));
        r.getPasajeros().get(0).contratarServicio(new ServicioAdicional("Traslado", 25, false), "2028-02-01");
        ventas.crearReserva(r, "baja"); ventas.cobrar(r.getNumero(), new Fecha("2028-02-01"), 150, new Transferencia(), 1);
        ventas.ajustar(r.getNumero(), "Corrección", 10, new Fecha("2028-02-01"));
        Pago pago = r.getPagos().get(0);
        tour.agregarTarifa(new Tarifa("baja", "2028-02-01", 200)); tours.actualizar(tour);
        ventas.agregarPasajeros(r.getNumero(), lista(adulto("1006")), new Fecha("2028-02-02"), "alta");
        verificar(r.getTotal() == 575, "Grupo de seis usa tarifa histórica y conserva ajuste y traslado");
        verificar(r.getPagos().get(0) == pago && r.calcularMontoPagado() == 150 && r.saldoPendiente() == 425, "Pagos y saldo conservados");
        verificar(salida.getCuposReservados() == 6 && r.getPasajeros().size() == 6, "Cupos actualizados");
        verificar(r.getEstado().equals("PENDIENTE"), "Seña se reevalúa al aumentar el total");
        verificar(r.getNotas().get(0).contains("Corrección") && r.getNotas().size() == 1, "Conserva las notas de ajuste anteriores");
        List<Pasajero> lote = lista(adulto("1007")); lote.add(adulto("1001"));
        error = false;
        try { ventas.agregarPasajeros(r.getNumero(), lote, new Fecha("2028-02-02"), "baja"); } catch (IllegalArgumentException e) { error = true; }
        verificar(error && r.getPasajeros().size() == 6 && salida.getCuposReservados() == 6 && r.getTotal() == 575 && r.getNotas().size() == 1, "Lote duplicado no deja cambios parciales");
        lote = new ArrayList<>(); for (int i = 0; i < 5; i++) lote.add(adulto("200" + i));
        error = false;
        try { ventas.agregarPasajeros(r.getNumero(), lote, new Fecha("2028-02-02"), "baja"); } catch (RuntimeException e) { error = true; }
        verificar(error && r.getTotal() == 575 && salida.getCuposReservados() == 6, "Sin cupo no altera reserva");
        error = false;
        try { ventas.agregarPasajeros(r.getNumero(), lista(adulto("1007")), new Fecha("2028-02-21"), "baja"); } catch (IllegalArgumentException e) { error = true; }
        verificar(error && r.getPasajeros().size() == 6, "A nueve días se rechaza incorporación");
        ventas.agregarPasajeros(r.getNumero(), lista(adulto("1007")), new Fecha("2028-02-20"), "baja");
        verificar(r.getPasajeros().size() == 7 && r.getTotal() == 665, "Hasta diez días inclusive y segunda ampliación");
        Reserva recuperada = new RepositorioReservas(new RepositorioSalidas()).buscarPorId("" + r.getNumero());
        verificar(recuperada.getTotal() == 665 && recuperada.getPasajeros().size() == 7 && new RepositorioCondiciones().buscar(recuperada.getNumero()).getTemporada().equals("baja") && recuperada.getPagos().size() == 1, "Recupera ampliación y contexto de tarifa");
        Tour aventura = new TourAventura("A", "Trekking", "", 1, 1, 5, 18, "Media");
        aventura.agregarTarifa(new Tarifa("baja", "2028-01-01", 100));
        Salida a = new Salida(aventura, "2028-03-01", "2028-03-01", 5);
        Reserva ra = new Reserva("2028-02-01", c, r.getVendedor(), a);
        Pasajero p = adulto("3001"); p.contratarServicio(new ServicioAdicional("Seguro de viaje", 10, true), "2028-02-01");ra.agregarPasajero(p);
        new CalculadoraPrecioReserva().calcularTotalReserva(ra, "baja");ra.confirmarAlta();
        error = false;
        try { AmpliacionReserva.agregar(ra, lista(adulto("3002")), new Fecha("2028-02-02"), 100); } catch (Exception e) { error = true; }
        verificar(error && a.getCuposReservados() == 1 && ra.getTotal() == 110, "Aventura sin seguro rechazada sin cambios");
        Pasajero nuevo = adulto("3002");nuevo.contratarServicio(new ServicioAdicional("Seguro de viaje", 10, true), "2028-02-02");
        AmpliacionReserva.agregar(ra, lista(nuevo), new Fecha("2028-02-02"), 100);
        verificar(a.getCuposReservados() == 2 && ra.getTotal() == 220, "Seguro nuevo incluido y servicio anterior conservado");
        ra.cancelar(new Fecha("2028-02-02"), "No viaja", true);error = false;
        try { AmpliacionReserva.agregar(ra, lista(adulto("3003")), new Fecha("2028-02-02"), 100); } catch (RuntimeException e) { error = true; }
        verificar(error && a.getCuposReservados() == 0 && ra.getPasajeros().size() == 2, "Reserva cancelada no admite pasajeros");
        verificar(new ControladorAdministrativo(tours, salidas).obtenerReportesEconomicos().contains("Ventas por vendedor"), "Reportes disponibles como texto para MVC");
        Reserva anticipada = new Reserva("2027-12-31", c, r.getVendedor(), new Salida(tour, "2028-03-01", "2028-03-01", 10));
        anticipada.agregarPasajero(adulto("5001"));
        new CalculadoraPrecioReserva().calcularTotalReserva(anticipada, "baja"); anticipada.confirmarAlta();
        List<Pasajero> grupo = new ArrayList<>();for (int i = 2; i <= 6; i++) grupo.add(adulto("500" + i));
        AmpliacionReserva.agregar(anticipada, grupo, new Fecha("2028-02-01"), 200);
        verificar(anticipada.getTotal() == 1020, "Anticipada original se conserva y no se acumula con grupo");
        Reserva completa = new Reserva("2028-02-01", c, r.getVendedor(), new Salida(tour, "2028-03-01", "2028-03-01", 10));
        for (int i = 1; i <= 5; i++) completa.agregarPasajero(adulto("600" + i));
        new CalculadoraPrecioReserva().calcularTotalReserva(completa, "baja");completa.confirmarAlta();
        completa.registrarPago("2028-02-01", 1000, new Efectivo(), 1);
        error = false;
        try { AmpliacionReserva.agregar(completa, lista(new Pasajero("6006", "Bebe", "2027-01-01", "menor_3", "111", "")), new Fecha("2028-02-02"), 200); }
        catch (IllegalArgumentException e) { error = true; }
        verificar(error && completa.getTotal() == 1000 && completa.getPasajeros().size() == 5
                && completa.getSalida().getCuposReservados() == 5, "Recálculo menor al pago se rechaza sin cambios");
        ventas.cobrar(r.getNumero(), new Fecha("2028-02-20"), 515, new Efectivo(), 1);
        Guia guia = new Guia("G1", "Julia", "", "111", "G1", 100);guia.habilitarCategoria("cultural");
        salida.asignarGuia(guia);salida.asignarVehiculo(new Vehiculo("TEST", 10, "Combi"));salida.emitirListaPasajeros();salida.iniciar();
        error = false;
        try { AmpliacionReserva.agregar(r, lista(adulto("1008")), new Fecha("2028-02-20"), 100); } catch (RuntimeException e) { error = true; }
        verificar(error && salida.getCuposReservados() == 7, "Salida en curso no admite ampliación");
        salida.cerrar();error = false;
        try { AmpliacionReserva.agregar(r, lista(adulto("1008")), new Fecha("2028-02-20"), 100); } catch (RuntimeException e) { error = true; }
        verificar(error && r.getEstado().equals("FINALIZADA"), "Reserva finalizada no admite ampliación");
        for (String valor : new String[]{"NaN", "Infinity", "1e300"}) {
            error = false;
            try { Importes.leer(valor); } catch (IllegalArgumentException e) { error = true; }
            verificar(error, "Importe inválido rechazado: " + valor);
        }
        System.out.println("TOTAL " + aprobadas + " comprobaciones aprobadas");
    }
}

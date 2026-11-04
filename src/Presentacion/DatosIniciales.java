package Presentacion;

import Dominio.*;
import Dominio.Excepciones.RequisitoPasajeroException;
import Persistencia.Repositorio;

class DatosIniciales {
    static void cargar(Repositorio<Cliente> clientes, Repositorio<Tour> tours,
                       Repositorio<Salida> salidas, Fecha hoy) throws RequisitoPasajeroException {
        String[] nombres = {"Ana Díaz", "Luis Paz", "Eva Ríos", "Juan Vera", "Sara Cruz"};
        for (int i = 0; i < nombres.length; i++) {
            String documento = "" + (30000001 + i);
            if (clientes.buscarPorId(documento) == null) {
                clientes.guardar(new Cliente(documento, nombres[i], "cliente" + (i + 1) + "@example.com",
                        "110000000" + i, "Argentina"));
                System.out.println("Cliente de ejemplo: " + documento + " | " + nombres[i]);
            }
        }

        Tour[] viajes = {
            new TourCultural("RS01", "Paseo por Salta", "Circuito histórico", 1, 2, 12),
            new TourNaturaleza("RS02", "Lagos de Bariloche", "Excursión por los lagos", 1, 2, 10),
            new TourCultural("RS03", "Bodegas de Mendoza", "Visita a una bodega", 1, 2, 12),
            new TourNaturaleza("RS04", "Cataratas del Iguazú", "Recorrido por las pasarelas", 1, 2, 10),
            new TourAventura("RS05", "Trekking en El Chaltén", "Caminata guiada", 1, 2, 8, 16, "Media")
        };
        int anio = Integer.parseInt(hoy.toString().substring(0, 4));
        if (anio < 9999) anio++;
        String cuatroCifras = "000" + anio;
        String enero = cuatroCifras.substring(cuatroCifras.length() - 4, cuatroCifras.length()) + "-01-";
        for (int i = 0; i < viajes.length; i++) {
            Tour ejemplo = viajes[i];
            if (tours.buscarPorId(ejemplo.getCodigo()) == null) {
                // Si el guardado del catálogo se interrumpió, la salida ya contiene el tour.
                Tour recuperado = null;
                for (Salida salida : salidas.obtenerTodos()) {
                    if (salida.getTour().getCodigo().equals(ejemplo.getCodigo())) recuperado = salida.getTour();
                }
                if (recuperado != null) {
                    tours.guardar(recuperado);
                } else {
                    String fecha = enero + (15 + i);
                    ejemplo.agregarActividad(new Actividad(1, 1, "09:00", ejemplo.getDescripcion(), ejemplo.getNombre(), null));
                    ejemplo.agregarTarifa(new Tarifa("baja", fecha, 20000 + 2500 * i));
                    Salida salida = new Salida(ejemplo, fecha, fecha, ejemplo.getCupoMaximo());
                    salidas.guardar(salida);
                    tours.guardar(ejemplo);
                    System.out.println("Salida de ejemplo: " + salida.getId() + " | " + ejemplo.getNombre());
                }
            }
        }
        Tour salta = tours.buscarPorId("RS01");
        if (salta.getNombre().equals("Paseo por Salta") && !salta.getTarifas().isEmpty()) {
            String fecha = salta.getTarifas().get(0).getFechaVigencia();
            Salida salida = salidas.buscarPorId("RS01_" + fecha);
            if (salida != null && salida.verificarCupoDisponible(2) && salida.getReservas().isEmpty()
                    && salida.getGuia() == null && salida.getVehiculo() == null
                    && hoy.diasHasta(new Fecha(fecha)) >= 10 && fecha.compareTo("2010-01-01") >= 0) {
                prepararSalidaSalta(salida, clientes.buscarPorId("30000001"), hoy);
                salidas.actualizar(salida);
            }
        }
    }

    private static void prepararSalidaSalta(Salida salida, Cliente titular, Fecha hoy) throws RequisitoPasajeroException {
        Vendedor vendedor = new Vendedor("V01", "Laura Paz", "ventas@example.com", "1100000020", "V-SALTA");
        Reserva reserva = new Reserva(hoy.toString(), titular, vendedor, salida);
        Pasajero ana = new Pasajero(titular.getDocumento(), titular.getNombre(), "1990-05-10", "adulto", "1100000030", "");
        ana.contratarServicio(new ServicioAdicional("Traslado al hotel", 2000, false), hoy.toString());
        reserva.agregarPasajero(ana);
        reserva.agregarPasajero(new Pasajero("31000002", "Leo Díaz", "1992-01-01", "adulto", "1100000031", ""));
        for (Pasajero pasajero : reserva.getPasajeros()) reserva.validarPasajero(pasajero);
        new CalculadoraPrecioReserva().calcularTotalReserva(reserva, "baja");
        reserva.confirmarAlta();
        reserva.registrarPago(hoy.toString(), Importes.redondear(reserva.getTotal() * 0.30), new Transferencia(), 1);

        Guia guia = new Guia("G-SALTA", "Julia Ríos", "guia@example.com", "1100000040", "GS01", 18000);
        guia.habilitarCategoria("cultural");
        guia.agregarIdioma("español");
        salida.asignarGuia(guia);
        salida.asignarVehiculo(new Vehiculo("RS001AA", 12, "combi"));
        System.out.println("Reserva de ejemplo " + reserva.getNumero() + " | " + salida.getId() + " | saldo $ " + Importes.mostrar(reserva.saldoPendiente()));
    }
}

package Test;

import Dominio.*;
import Dominio.Excepciones.RequisitoPasajeroException;

public class CalculadoraPrecioReservaTest {

    public static void main(String[] args) throws RequisitoPasajeroException {
        System.out.println("=== EJECUTANDO PRUEBAS UNITARIAS DE NEGOCIO ===");

        testPreciosPasajeroPorTipo();
        testPromocionNoAcumulable();
        testPoliticas();
        testAventura();
        testPlazos();
        testItinerarioYTarifa();
        verificar(Fecha.desdePantalla("29/02/2028").toString().equals("2028-02-29"), "Fecha argentina bisiesta");
        verificar(Fecha.mostrar("2027-01-15").equals("15/01/2027"), "Fecha guardada a pantalla");
        verificar(Importes.leer("25200,50") == 25200.50, "Importe con coma");
        verificar(Importes.mostrar(353.1).equals("353,10"), "Dos decimales");
        verificar(Importes.mostrar(-10.05).equals("-10,05"), "Ajuste negativo");
        boolean invalida = false;
        try {
            Fecha.desdePantalla("31/02/2028");
        } catch (IllegalArgumentException e) {
            invalida = true;
        }
        verificar(invalida, "Fecha imposible rechazada");

        System.out.println("=== TODAS LAS PRUEBAS FINALIZARON EXITOSAMENTE ===");
    }

    private static void testPreciosPasajeroPorTipo() {
        CalculadoraPrecioReserva calc = new CalculadoraPrecioReserva();
        double tarifaBase = 1000.0;

        Pasajero adulto = new Pasajero("1", "Adulto", "1990-01-01", "adulto", "Contacto", "Ninguna");
        Pasajero menor = new Pasajero("2", "Menor", "2018-01-01", "menor_12", "Contacto", "Ninguna");
        Pasajero bebe = new Pasajero("3", "Bebe", "2024-01-01", "menor_3", "Contacto", "Ninguna");
        Pasajero jubilado = new Pasajero("4", "Jubilado", "1950-01-01", "jubilado", "Contacto", "Ninguna");

        verificar(calc.calcularPrecioPasajero(adulto, tarifaBase) == 1000.0, "Precio adulto");
        verificar(calc.calcularPrecioPasajero(menor, tarifaBase) == 600.0, "Precio menor");
        verificar(calc.calcularPrecioPasajero(bebe, tarifaBase) == 0.0, "Precio bebe");
        verificar(calc.calcularPrecioPasajero(jubilado, tarifaBase) == 900.0, "Precio jubilado");


    }

    private static void testPromocionNoAcumulable() {
        CalculadoraPrecioReserva calc = new CalculadoraPrecioReserva();
        Tour tour = new TourCultural("T", "Museos", "", 1, 1, 8);
        Salida salida = new Salida(tour, "2028-03-01", "2028-03-01", 8);
        Reserva anticipada = reserva(salida, "2027-12-31"); // 61 días, febrero bisiesto
        Reserva comun = reserva(salida, "2028-01-01");      // 60 días
        for (int i = 1; i <= 6; i++) {
            anticipada.agregarPasajero(adulto("A" + i));
            comun.agregarPasajero(adulto("B" + i));
        }
        verificar(calc.obtenerMejorDescuento(anticipada, 6000) == 900, "Promociones simultáneas");
        verificar(calc.obtenerMejorDescuento(comun, 6000) == 600, "Solo grupo a los 60 días");
    }

    private static Reserva reserva(Salida s, String fecha) {
        Cliente c = new Cliente("1", "Ana", "ana@example.com", "111");
        Vendedor v = new Vendedor("2", "Luis", "", "111", "V1");
        return new Reserva(fecha, c, v, s);
    }

    private static Pasajero adulto(String documento) {
        return new Pasajero(documento, "Eva", "1990-01-01", "adulto", "111", "");
    }

    private static void testPoliticas() {
        PoliticaCancelacion politica = new PoliticaCancelacionCliente();
        int[] dias = {30, 29, 15, 14, 7, 6};
        int[] esperados = {90, 50, 50, 20, 20, 0};
        for (int i = 0; i < dias.length; i++) {
            verificar(politica.calcularDevolucion(100, dias[i]) == esperados[i], "Cancelación a " + dias[i]);
        }
        verificar(new PoliticaCancelacionAgencia().calcularDevolucion(100, 1) == 100, "Devolución agencia");
    }

    private static void testAventura() throws RequisitoPasajeroException {
        Tour tour = new TourAventura("A", "Trekking", "", 1, 1, 5, 18, "Media");
        Pasajero p = new Pasajero("1", "Eva", "2010-03-01", "adulto", "111", "");
        boolean rechazado = false;
        try {
            tour.validarRequisitosPasajero(p, "2028-03-01");
        } catch (RequisitoPasajeroException e) {
            rechazado = true;
        }
        verificar(rechazado, "Sin seguro");
        p.contratarServicio(new ServicioAdicional("Seguro de viaje", 100, true), "2028-01-01");
        verificar(tour.validarRequisitosPasajero(p, "2028-03-01"), "Cumple 18 al salir");
        rechazado = false;
        try {
            tour.validarRequisitosPasajero(p, "2028-02-29");
        } catch (RequisitoPasajeroException e) {
            rechazado = true;
        }
        verificar(rechazado, "Edad insuficiente");
    }

    private static void testPlazos() {
        Tour tour = new TourCultural("T", "Museos", "", 1, 2, 2);
        tour.agregarTarifa(new Tarifa("baja", "2027-01-01", 100));
        Salida s = new Salida(tour, "2028-03-01", "2028-03-01", 2);
        Reserva paga = reserva(s, "2028-02-01");
        Reserva parcial = reserva(s, "2028-02-01");
        CalculadoraPrecioReserva calc = new CalculadoraPrecioReserva();
        for (Reserva r : new Reserva[]{paga, parcial}) {
            r.agregarPasajero(adulto("Doc" + r.getNumero()));
            calc.calcularTotalReserva(r, "baja");
            r.confirmarAlta();
        }
        verificar(s.getEstado().equals("COMPLETA"), "Cupo máximo");
        paga.registrarPago("2028-02-20", 100, new TarjetaCredito(), 2);
        parcial.registrarPago("2028-02-20", 29, new Efectivo(), 1);
        verificar(parcial.getEstado().equals("PENDIENTE"), "29% pagado");
        parcial.registrarPago("2028-02-20", 1, new Transferencia(), 1);
        verificar(parcial.getEstado().equals("CONFIRMADA"), "30% pagado");
        s.controlarPlazos(new Fecha("2028-02-20"));
        verificar(s.getCuposReservados() == 2, "Puede pagar hasta el día 10 inclusive");
        s.controlarPlazos(new Fecha("2028-02-21"));
        verificar(parcial.getEstado().equals("ANULADA") && s.getCuposReservados() == 1, "Saldo vencido");
        s.controlarPlazos(new Fecha("2028-02-23"));
        verificar(s.getEstado().equals("CANCELADA") && s.getCuposReservados() == 0, "Mínimo insuficiente");
        verificar(paga.getCancelacion().getImporteDevuelto() == 107, "Agencia devuelve también recargo");
    }

    private static void testItinerarioYTarifa() {
        Tour tour = new TourNaturaleza("N", "Parque", "", 2, 1, 4);
        tour.agregarActividad(new Actividad(2, 1, "10:00", "Bosque", "Sur", null));
        tour.agregarActividad(new Actividad(1, 2, "11:00", "Visita", "Lago", null));
        tour.agregarActividad(new Actividad(1, 1, "08:00", "Traslado", "Hotel", null));
        verificar(tour.getItinerario().get(0).getLugar().equals("Hotel"), "Actividades por día y orden");
        tour.agregarTarifa(new Tarifa("baja", "2028-01-01", 100));
        tour.agregarTarifa(new Tarifa("baja", "2028-03-01", 120));
        tour.agregarTarifa(new Tarifa("alta", "2028-01-01", 200));
        verificar(tour.getTarifaVigente("2028-02-29", "baja").getPrecioBase() == 100, "Tarifa futura excluida");
        verificar(tour.getTarifaVigente("2028-03-01", "baja").getPrecioBase() == 120, "Cambio de vigencia");
        verificar(tour.getTarifaVigente("2028-03-01", "alta").getPrecioBase() == 200, "Selección de temporada");
    }

    private static void verificar(boolean correcto, String caso) {
        if (!correcto) throw new RuntimeException("Falló: " + caso);
        System.out.println("[OK] " + caso);
    }
}

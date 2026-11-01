package Presentacion;

import Controladores.*;
import Dominio.*;

import java.util.Scanner;

public class MenuPrincipal {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ControladorVentas controladorVentas = new ControladorVentas();
    private static final ControladorOperaciones controladorOperaciones = new ControladorOperaciones();
    private static final ControladorAdministrativo controladorAdmin = new ControladorAdministrativo();

    public static void main(String[] args) {
        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                procesarOpcion(opcion);
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un numero entero valido.");
            } catch (Exception e) {
                System.out.println("Error en la operacion: " + e.getMessage());
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n========================================");
        System.out.println("      SISTEMA RUMBO SUR - AGENCIA      ");
        System.out.println("========================================");
        System.out.println("1. Registrar Cliente");
        System.out.println("2. Registrar Tour");
        System.out.println("3. Programar Salida");
        System.out.println("4. Crear Reserva");
        System.out.println("5. Registrar Pago de Reserva");
        System.out.println("6. Asignar Guia a Salida");
        System.out.println("7. Iniciar / Cerrar Salida");
        System.out.println("8. Emitir Reporte de Ocupacion");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    private static void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> registrarCliente();
            case 2 -> registrarTour();
            case 3 -> programarSalida();
            case 4 -> crearReserva();
            case 5 -> registrarPago();
            case 6 -> asignarGuia();
            case 7 -> gestionarEstadoSalida();
            case 8 -> controladorAdmin.emitirReporteOcupacion();
            case 0 -> System.out.println("Gracias por utilizar el sistema Rumbo Sur.");
            default -> System.out.println("Opcion no valida. Reintente.");
        }
    }

    private static void registrarCliente() {
        System.out.print("Documento: ");
        String doc = scanner.nextLine();
        System.out.print("Nombre completo: ");
        String nom = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefono: ");
        String tel = scanner.nextLine();

        Cliente c = new Cliente(doc, nom, email, tel);
        controladorVentas.registrarCliente(c);
        System.out.println("--> Cliente registrado exitosamente.");
    }

    private static void registrarTour() {
        System.out.print("Codigo del tour: ");
        String cod = scanner.nextLine();
        System.out.print("Nombre: ");
        String nom = scanner.nextLine();
        System.out.print("Descripcion: ");
        String desc = scanner.nextLine();
        System.out.print("Duracion en dias: ");
        int dias = Integer.parseInt(scanner.nextLine());
        System.out.print("Cupo minimo: ");
        int cMin = Integer.parseInt(scanner.nextLine());
        System.out.print("Cupo maximo: ");
        int cMax = Integer.parseInt(scanner.nextLine());

        TourAventura tour = new TourAventura(cod, nom, desc, dias, cMin, cMax, 12, "Media");
        controladorAdmin.registrarTour(tour);
        System.out.println("--> Tour de Aventura registrado exitosamente.");
    }

    private static void programarSalida() {
        System.out.print("Codigo de Tour existente: ");
        String cod = scanner.nextLine();
        Tour tour = controladorAdmin.buscarTour(cod);
        if (tour == null) {
            System.out.println("Error: No existe el tour con codigo " + cod);
            return;
        }

        System.out.print("Fecha de Inicio (YYYY-MM-DD): ");
        String fInicio = scanner.nextLine();
        System.out.print("Fecha de Fin (YYYY-MM-DD): ");
        String fFin = scanner.nextLine();

        Salida salida = new Salida(tour, fInicio, fFin, tour.getCupoMaximo());
        controladorOperaciones.programarSalida(salida);
        System.out.println("--> Salida programada correctamente.");
    }

    private static void crearReserva() {
        System.out.print("Documento del Cliente: ");
        String doc = scanner.nextLine();
        Cliente cliente = controladorVentas.buscarCliente(doc);
        if (cliente == null) {
            System.out.println("Error: Cliente no encontrado.");
            return;
        }

        System.out.print("ID de Salida (Formato: CODIGOTOUR_FECHA, ej: T01_2026-12-01): ");
        String idSalida = scanner.nextLine();
        Salida salida = controladorOperaciones.buscarSalida(idSalida);
        if (salida == null) {
            System.out.println("Error: Salida no encontrada.");
            return;
        }

        System.out.print("Temporada (alta / media / baja): ");
        String temporada = scanner.nextLine();

        // Crear vendedor representativo
        Vendedor vendedor = new Vendedor("V01", "Vendedor Central", "vendedor@rumbosur.com", "11223344", "Legajo-01");
        Reserva reserva = new Reserva("2026-10-01", cliente, vendedor, salida);

        System.out.print("Cantidad de pasajeros a agregar: ");
        int cant = Integer.parseInt(scanner.nextLine());
        for (int i = 1; i <= cant; i++) {
            System.out.println("--- Pasajero " + i + " ---");
            System.out.print("Documento: ");
            String pDoc = scanner.nextLine();
            System.out.print("Nombre: ");
            String pNom = scanner.nextLine();
            System.out.print("Fecha Nacimiento (YYYY-MM-DD): ");
            String pFnac = scanner.nextLine();
            System.out.print("Tipo (adulto / menor_12 / menor_3 / jubilado): ");
            String pTipo = scanner.nextLine();

            Pasajero p = new Pasajero(pDoc, pNom, pFnac, pTipo, "Contacto Emergencia", "Sin restricciones");
            reserva.agregarPasajero(p);
        }

        controladorVentas.crearReserva(reserva, temporada);
        System.out.println("--> Reserva creada exitosamente con Numero: " + reserva.getNumero());
        System.out.println("--> Total a pagar estimado: $" + reserva.getTotal());
    }

    private static void registrarPago() {
        System.out.print("Numero de Reserva: ");
        int numReserva = Integer.parseInt(scanner.nextLine());
        System.out.print("Monto a abonar: ");
        double monto = Double.parseDouble(scanner.nextLine());

        MedioPago medio = new Efectivo();
        Pago pago = new Pago("2026-10-02", monto, medio);

        controladorVentas.registrarPago(numReserva, pago);
        System.out.println("--> Pago registrado correctamente.");
    }

    private static void asignarGuia() {
        System.out.print("ID de Salida: ");
        String idSalida = scanner.nextLine();

        Guia guia = new Guia("G01", "Juan Guia", "guia@rumbosur.com", "15443322", "MAT-99", 5000.0);
        controladorOperaciones.asignarGuia(idSalida, guia);
        System.out.println("--> Guia asignado a la salida correctamente.");
    }

    private static void gestionarEstadoSalida() {
        System.out.print("ID de Salida: ");
        String idSalida = scanner.nextLine();
        System.out.println("1. Iniciar Salida");
        System.out.println("2. Cerrar Salida");
        System.out.print("Opcion: ");
        int op = Integer.parseInt(scanner.nextLine());

        if (op == 1) {
            controladorOperaciones.iniciarSalida(idSalida);
            System.out.println("--> Salida iniciada (EN_CURSO).");
        } else if (op == 2) {
            controladorOperaciones.cerrarSalida(idSalida);
            System.out.println("--> Salida finalizada (FINALIZADA).");
        }
    }
}

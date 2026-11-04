package Presentacion;

import Dominio.Excepciones.RequisitoPasajeroException;
import Controladores.*;
import Dominio.Excepciones.CupoSuperadoException;
import Dominio.*;
import Persistencia.*;
import java.util.Scanner;

public class MenuPrincipal {
    private static final Scanner scanner = new Scanner(System.in);

    private static ControladorVentas controladorVentas;
    private static ControladorOperaciones controladorOperaciones;
    private static ControladorAdministrativo controladorAdmin;

    private static Fecha hoy;

    public static void main(String[] args) {
        try {
            Repositorio<Tour> tourRepo = new RepositorioTours();
            Repositorio<Salida> salidaRepo = new RepositorioSalidas();
            Repositorio<Cliente> clientes = new RepositorioClientes();
            controladorVentas = new ControladorVentas(clientes, salidaRepo);
            controladorOperaciones = new ControladorOperaciones(salidaRepo, tourRepo);
            controladorAdmin = new ControladorAdministrativo(tourRepo, salidaRepo);
            hoy = Fecha.desdePantalla(leer("Fecha de trabajo (DD/MM/AAAA)"));
            DatosIniciales.cargar(clientes, tourRepo, salidaRepo, hoy);
            while (true) {
                controladorOperaciones.controlarPlazos(hoy);
                System.out.println("\nRumbo Sur  |  " + Fecha.mostrar(hoy.toString()));
                System.out.println("1. Catálogo de tours   2. Registrar cliente   3. Programar salida");
                System.out.println("4. Crear reserva       5. Registrar pago      6. Consultar reserva");
                System.out.println("7. Modificar o cancelar reserva   8. Operar salida   9. Reportes   0. Salir");
                if (!scanner.hasNextLine()) return;
                try {
                    int opcion = entero("Opción");
                    if (opcion == 0) return;
                    procesarOpcion(opcion);
                } catch (ErrorArchivo e) {
                    throw e;
                } catch (Exception e) {
                    System.out.println("Operación rechazada: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Sesión interrumpida: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static String leer(String rotulo) {
        System.out.print(rotulo + ": ");
        return scanner.nextLine().trim();
    }

    private static String leerFecha(String rotulo) {
        return Fecha.desdePantalla(leer(rotulo + " (DD/MM/AAAA)")).toString();
    }

    private static int entero(String rotulo) { return Integer.parseInt(leer(rotulo)); }
    private static double importe(String rotulo) { return Importes.leer(leer(rotulo + " (sin separador de miles)")); }

    private static void procesarOpcion(int opcion) throws RequisitoPasajeroException {
        if (opcion == 1) menuCatalogo();
        else if (opcion == 2) registrarCliente();
        else if (opcion == 3) programarSalida();
        else if (opcion == 4) crearReserva();
        else if (opcion == 5) registrarPago();
        else if (opcion == 6) consultarReserva();
        else if (opcion == 7) modificarReserva();
        else if (opcion == 8) menuSalidas();
        else if (opcion == 9) {
            controladorAdmin.emitirReporteOcupacion();
            controladorAdmin.emitirReportesEconomicos();
        }
        else System.out.println("Opción inválida.");
    }

    private static void menuCatalogo() {
        System.out.println("1. Registrar tour   2. Cargar tarifa   3. Agregar actividad   4. Consultar catálogo   0. Volver");
        int opcion = entero("Opción");
        if (opcion == 0) return;
        else if (opcion == 1) registrarTour();
        else if (opcion == 2) cargarTarifa();
        else if (opcion == 3) cargarActividad();
        else if (opcion == 4) catalogo();
        else System.out.println("Opción inválida.");
    }

    private static void modificarReserva() throws RequisitoPasajeroException {
        System.out.println("1. Contratar adicional   2. Reemplazar pasajero   3. Cancelar   4. Nota de ajuste   0. Volver");
        int opcion = entero("Opción");
        if (opcion == 0) return;
        else if (opcion == 1) contratarServicio();
        else if (opcion == 2) reemplazar();
        else if (opcion == 3) cancelar();
        else if (opcion == 4) registrarAjuste();
        else System.out.println("Opción inválida.");
    }

    private static void menuSalidas() {
        System.out.println("1. Asignar guía   2. Asignar vehículo   3. Lista de pasajeros");
        System.out.println("4. Iniciar salida   5. Cerrar salida   6. Liquidar honorarios   0. Volver");
        int opcion = entero("Opción");
        if (opcion == 0) return;
        else if (opcion == 1) asignarGuia();
        else if (opcion == 2) asignarVehiculo();
        else if (opcion == 3) System.out.println(controladorOperaciones.emitirLista(leer("ID salida")));
        else if (opcion == 4) controladorOperaciones.iniciarSalida(leer("ID salida"), hoy);
        else if (opcion == 5) controladorOperaciones.cerrarSalida(leer("ID salida"), hoy);
        else if (opcion == 6) System.out.println("Honorarios: $ " + Importes.mostrar(controladorAdmin.liquidarHonorariosGuia(leer("ID salida"))));
        else System.out.println("Opción inválida.");
    }

    private static void registrarCliente() {
        String doc = leer("Documento");
        String nom = leer("Nombre completo");
        String email = leer("Email");
        String tel = leer("Teléfono");

        String pais = leer("País de residencia");
        Cliente c = new Cliente(doc, nom, email, tel, pais);
        controladorVentas.registrarCliente(c);
        System.out.println("--> Cliente registrado exitosamente.");
    }

    private static void registrarTour() {
        String cod = leer("Código del tour");
        String nom = leer("Nombre");
        String desc = leer("Descripción");
        int dias = entero("Duración en días");
        int cMin = entero("Cupo mínimo");
        int cMax = entero("Cupo máximo");
        String tipo = leer("Tipo de tour (aventura/cultural/naturaleza)").toLowerCase();
        Tour tour;
        if (tipo.equals("aventura")) {
            int edad = entero("Edad mínima");
            String nivel = leer("Nivel de dificultad");
            tour = new TourAventura(cod, nom, desc, dias, cMin, cMax, edad, nivel);
        } else if (tipo.equals("cultural")) {
            tour = new TourCultural(cod, nom, desc, dias, cMin, cMax);
        } else if (tipo.equals("naturaleza")) {
            tour = new TourNaturaleza(cod, nom, desc, dias, cMin, cMax);
        } else {
            throw new IllegalArgumentException("Tipo de tour inválido.");
        }
        controladorAdmin.registrarTour(tour);
        System.out.println("--> Tour registrado exitosamente.");
    }

    private static void programarSalida() {
        Tour tour = controladorAdmin.exigirTour(leer("Código del tour"));
        String fInicio = leerFecha("Fecha de inicio");
        String fFin = leerFecha("Fecha de fin");

        Salida salida = new Salida(tour, fInicio, fFin, entero("Cupo máximo de esta salida"));
        controladorOperaciones.programarSalida(salida);
        System.out.println("Salida programada: " + salida.getId());
    }

    private static void crearReserva() throws RequisitoPasajeroException {
        String doc = leer("Documento del Cliente");
        Cliente cliente = controladorVentas.buscarCliente(doc);
        if (cliente == null) {
            System.out.println("Error: Cliente no encontrado.");
            return;
        }

        String idSalida = leer("ID de Salida (Formato: CODIGOTOUR_FECHA, ej: T01_01/12/2026)");
        Salida salida = controladorOperaciones.buscarSalida(idSalida);
        if (salida == null) {
            System.out.println("Error: Salida no encontrada.");
            return;
        }

        String temporada = leer("Temporada (alta / media / baja)");

        Vendedor vendedor = new Vendedor(leer("Documento vendedor"), leer("Nombre vendedor"), leer("Email"),
                leer("Teléfono"), leer("Legajo"), importe("Comisión (%)"));
        int cantidad = entero("Cantidad de pasajeros");
        if (!salida.verificarCupoDisponible(cantidad)) {
            throw new CupoSuperadoException("No hay cupo para esa cantidad de pasajeros.");
        }
        Reserva reserva = new Reserva(hoy.toString(), cliente, vendedor, salida);
        for (int i = 0; i < cantidad; i++) {
            Pasajero p = leerPasajero();
            if (salida.getTour().getCategoria().equals("aventura")) {
                ServicioAdicional seguro = new ServicioAdicional("Seguro de viaje", importe("Precio del seguro obligatorio"), true);
                p.contratarServicio(seguro, hoy.toString());
            }
            reserva.agregarPasajero(p);
        }

        controladorVentas.crearReserva(reserva, temporada);
        System.out.println("--> Reserva creada exitosamente con Numero: " + reserva.getNumero());
        System.out.println("--> Total a pagar estimado: $" + Importes.mostrar(reserva.getTotal()));
    }

    private static void registrarPago() {
        int numReserva = entero("Numero de Reserva");
        double monto = importe("Monto a abonar");

        String tipo = leer("Medio (efectivo/transferencia/tarjeta)").toLowerCase();
        MedioPago medio;
        if (tipo.equals("efectivo")) medio = new Efectivo();
        else if (tipo.equals("transferencia")) medio = new Transferencia();
        else if (tipo.equals("tarjeta")) medio = new TarjetaCredito();
        else throw new IllegalArgumentException("Medio desconocido.");
        Pago pago = controladorVentas.cobrar(numReserva, hoy, monto, medio, entero("Cuotas"));
        System.out.println("Cobro con recargo incluido: $ " + Importes.mostrar(pago.getMontoCobrado()));
    }

    private static void asignarGuia() {
        String id = leer("ID salida");
        Guia g = new Guia(leer("Documento"), leer("Nombre"), leer("Email"), leer("Teléfono"),
                leer("Matrícula/legajo"), importe("Honorario por día"));
        String otra = "s";
        while (otra.equals("s")) {
            g.habilitarCategoria(leer("Categoría habilitada (aventura/cultural/naturaleza)"));
            otra = leer("¿Otra categoría? (s/n)");
        }
        otra = "s";
        while (otra.equals("s")) {
            g.agregarIdioma(leer("Idioma"));
            otra = leer("¿Otro idioma? (s/n)");
        }
        controladorOperaciones.asignarGuia(id, g);
    }

    private static void asignarVehiculo() {
        String id = leer("ID salida");
        Vehiculo v = new Vehiculo(leer("Patente"), entero("Capacidad"), leer("Tipo"));
        controladorOperaciones.asignarVehiculo(id, v);
    }

    private static void cargarTarifa() {
        String codigo = leer("Código tour");
        Tarifa tarifa = new Tarifa(leer("Temporada"), leerFecha("Vigente desde"), importe("Precio base"));
        controladorAdmin.agregarTarifa(codigo, tarifa);
    }

    private static void cargarActividad() {
        String codigo = leer("Código tour");
        int dia = entero("Día");
        int orden = entero("Orden");
        String horario = leer("Horario");
        String descripcion = leer("Descripción");
        String lugar = leer("Lugar");
        Proveedor proveedor = null;
        if (leer("¿Interviene proveedor? (s/n)").equals("s")) {
            proveedor = new Proveedor(leer("Nombre"), leer("CUIT"), leer("Contacto"), leer("Servicio prestado"));
        }
        controladorAdmin.agregarActividad(codigo, new Actividad(dia, orden, horario, descripcion, lugar, proveedor));
    }

    private static Pasajero leerPasajero() {
        return new Pasajero(leer("Documento pasajero"), leer("Nombre y apellido"), leerFecha("Nacimiento"),
                leer("Tipo (adulto/menor_12/menor_3/jubilado)"), leer("Contacto de emergencia"), leer("Restricciones"));
    }

    private static void contratarServicio() {
        int numero = entero("Reserva");
        String documento = leer("Documento del pasajero");
        ServicioAdicional servicio = new ServicioAdicional(leer("Servicio"), importe("Precio del servicio"), false);
        controladorVentas.contratar(numero, documento, servicio, hoy);
    }

    private static void registrarAjuste() {
        int numero = entero("Reserva");
        String motivo = leer("Motivo");
        double diferencia = importe("Diferencia de precio (+/-)");
        controladorVentas.ajustar(numero, motivo, diferencia, hoy);
    }

    private static void reemplazar() throws RequisitoPasajeroException {
        int numero = entero("Reserva");
        String anterior = leer("Documento que se reemplaza");
        controladorVentas.reemplazar(numero, anterior, leerPasajero(), hoy);
    }

    private static void cancelar() {
        int numero = entero("Reserva");
        String motivo = leer("Motivo");
        String origen = leer("Origen (agencia/cliente)");
        if (!origen.equals("agencia") && !origen.equals("cliente")) throw new IllegalArgumentException("Origen inválido.");
        CancelacionReserva c = controladorVentas.cancelarReserva(numero, motivo, origen.equals("agencia"), hoy);
        System.out.println("Devolución: $ " + Importes.mostrar(c.getImporteDevuelto()) + " " + c.getDevoluciones());
    }

    private static void consultarReserva() {
        Reserva r = controladorVentas.exigirReserva(entero("Reserva"));
        System.out.println(r.getEstado() + " | total $ " + Importes.mostrar(r.getTotal()) + " | saldo $ " + Importes.mostrar(r.saldoPendiente()));
        System.out.println("Notas de ajuste: " + r.getNotas());
    }

    private static void catalogo() {
        for (Tour t : controladorAdmin.obtenerTodosTours()) {
            System.out.println(t.getCodigo() + " " + t.getNombre() + " | " + t.getDuracionDias() + " días");
            for (Actividad a : t.getItinerario()) {
                System.out.println(a.getDia() + "." + a.getOrden() + " " + a.getHorario() + " " + a.getDescripcion() + " " + a.getLugar());
                if (a.getProveedor() != null) System.out.println("Proveedor: " + a.getProveedor().getNombre());
            }
            for (Tarifa tarifa : t.getTarifas()) {
                System.out.println(tarifa.getTemporada() + " desde " + Fecha.mostrar(tarifa.getFechaVigencia()) + " | $ " + Importes.mostrar(tarifa.getPrecioBase()));
            }
        }
    }

}

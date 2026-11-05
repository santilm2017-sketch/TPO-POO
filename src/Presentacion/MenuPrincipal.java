package Presentacion;

import Dominio.Excepciones.RequisitoPasajeroException;
import Controladores.*;
import Dominio.Excepciones.CupoSuperadoException;
import Dominio.*;
import Persistencia.*;
import java.util.Scanner;

public class MenuPrincipal {
    private static final Scanner scanner = new Scanner(System.in);

    private static class CargaCancelada extends RuntimeException { }

    private static ControladorVentas controladorVentas;
    private static ControladorOperaciones controladorOperaciones;
    private static ControladorAdministrativo controladorAdmin;
    private static Repositorio<Reserva> reservas;

    private static Fecha hoy;

    public static void main(String[] args) {
        try {
            Repositorio<Tour> tourRepo = new RepositorioTours();
            Repositorio<Salida> salidaRepo = new RepositorioSalidas();
            reservas = new RepositorioReservas(salidaRepo);
            Repositorio<Cliente> clientes = new RepositorioClientes();
            controladorVentas = new ControladorVentas(clientes, salidaRepo);
            controladorOperaciones = new ControladorOperaciones(salidaRepo, tourRepo);
            controladorAdmin = new ControladorAdministrativo(tourRepo, salidaRepo);
            hoy = new Fecha(leerFecha("Fecha de trabajo"));
            System.out.println("Durante una carga: 0 cancela y vuelve al menú principal. Para un importe cero, escriba 0,00.");
            DatosIniciales.cargar(clientes, tourRepo, salidaRepo, hoy);
            while (true) {
                controladorOperaciones.controlarPlazos(hoy);
                System.out.println("\n=== RUMBO SUR - MENÚ PRINCIPAL ===");
                System.out.println("Fecha de trabajo: " + Fecha.mostrar(hoy.toString()));
                System.out.println("1. Catálogo de tours y personal");
                System.out.println("2. Registrar cliente");
                System.out.println("3. Programar salida");
                System.out.println("4. Crear reserva");
                System.out.println("5. Registrar pago");
                System.out.println("6. Listar reservas");
                System.out.println("7. Modificar o cancelar reserva");
                System.out.println("8. Operar salida");
                System.out.println("9. Reportes");
                System.out.println("0. Salir");
                if (!scanner.hasNextLine()) return;
                try {
                    int opcion = opcion("Seleccione una opción", 9);
                    if (opcion == 0) return;
                    procesarOpcion(opcion);
                } catch (CargaCancelada e) {
                    System.out.println("Carga cancelada.");
                } catch (ErrorArchivo e) {
                    throw e;
                } catch (Exception e) {
                    System.out.println("Operación rechazada: " + e.getMessage());
                }
            }
        } catch (CargaCancelada e) {
            System.out.println("Carga cancelada.");
        } catch (Exception e) {
            System.out.println("Sesión interrumpida: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static String entrada(String rotulo) {
        System.out.print(rotulo + ": ");
        if (!scanner.hasNextLine()) throw new CargaCancelada();
        return scanner.nextLine().trim();
    }

    private static String leer(String rotulo) {
        String texto = entrada(rotulo);
        if (texto.equals("0")) throw new CargaCancelada();
        return texto;
    }

    private static String leerFecha(String rotulo) {
        while (true) {
            String texto = leer(rotulo + " (DD/MM/AAAA, 0 para cancelar)");
            try { return Fecha.desdePantalla(texto).toString(); }
            catch (IllegalArgumentException e) { System.out.println("Fecha inválida. Volvé a intentarlo."); }
        }
    }

    private static int opcion(String rotulo, int maximo) {
        while (true) {
            try {
                int valor = Integer.parseInt(entrada(rotulo));
                if (valor >= 0 && valor <= maximo) return valor;
            } catch (NumberFormatException e) { }
            System.out.println("Ingresá una opción entre 0 y " + maximo + ".");
        }
    }

    private static int entero(String rotulo) {
        while (true) {
            String texto = leer(rotulo + " (0 para cancelar)");
            try {
                int valor = Integer.parseInt(texto);
                if (valor > 0) return valor;
            } catch (NumberFormatException e) { }
            System.out.println("Ingresá un número entero positivo válido.");
        }
    }

    private static double importe(String rotulo) {
        while (true) {
            String texto = leer(rotulo + " (sin separador de miles, 0 para cancelar)");
            try { return Importes.leer(texto); }
            catch (IllegalArgumentException e) { System.out.println("Importe inválido. Volvé a intentarlo."); }
        }
    }

    private static String elegir(String rotulo, String[] valores) {
        while (true) {
            String texto = leer(rotulo + " (0 para cancelar)").toLowerCase();
            for (String valor : valores) if (valor.equals(texto)) return texto;
            System.out.println("Valor inválido. Volvé a intentarlo.");
        }
    }

    private static void ejecutarOperacion(int operacion) {
        while (true) {
            try {
                if (operacion == 2) registrarCliente();
                else if (operacion == 3) programarSalida();
                else if (operacion == 4) crearReserva();
                else if (operacion == 5) registrarPago();
                else if (operacion == 6) listarReservas();
                else if (operacion == 9) {
                    controladorAdmin.emitirReporteOcupacion();
                    controladorAdmin.emitirReportesEconomicos();
                }
                else if (operacion == 11) registrarTour();
                else if (operacion == 12) cargarTarifa();
                else if (operacion == 13) cargarActividad();
                else if (operacion == 14) menuPersonal();
                else if (operacion == 71) contratarServicio();
                else if (operacion == 72) reemplazar();
                else if (operacion == 73) cancelar();
                else if (operacion == 74) registrarAjuste();
                else if (operacion >= 81 && operacion <= 83) operarSalida(operacion - 80);
                return;
            } catch (CargaCancelada e) {
                throw e;
            } catch (ErrorArchivo e) {
                throw e;
            } catch (Exception e) {
                System.out.println("Operación rechazada: " + e.getMessage());
                if (opcion("1. Volver a intentar   0. Menú principal", 1) == 0) throw new CargaCancelada();
            }
        }
    }

    private static void procesarOpcion(int opcion) {
        if (opcion == 1) menuCatalogo();
        else if (opcion == 7) modificarReserva();
        else if (opcion == 8) menuSalidas();
        else ejecutarOperacion(opcion);
    }

    private static void menuPersonal() {
        java.util.List<PersonalAdministrativo> personas = controladorAdmin.obtenerPersonal();
        if (personas.isEmpty()) System.out.println("No hay personal administrativo registrado.");
        for (PersonalAdministrativo persona : personas) {
            System.out.println(persona.getRol() + " | Legajo: " + persona.getLegajo()
                    + " | " + persona.getNombre() + " | Documento: " + persona.getDocumento()
                    + " | Teléfono: " + persona.getTelefono());
        }
        System.out.println("1. Registrar coordinador/administrador   0. Volver");
        int opcion = opcion("Opción", 1);
        if (opcion == 1) {
            PersonalAdministrativo persona = new PersonalAdministrativo(
                    leer("Documento"), leer("Nombre"), leer("Email"), leer("Teléfono"),
                    leer("Legajo"), elegir("Rol (coordinador/administrador)", new String[]{"coordinador", "administrador"}));
            controladorAdmin.registrarPersonal(persona);
            System.out.println("--> Personal registrado exitosamente.");
        }
    }

    private static void menuCatalogo() {
        catalogo();
        System.out.println("1. Registrar tour   2. Cargar tarifa   3. Agregar actividad   4. Personal administrativo   0. Volver");
        int opcion = opcion("Opción", 4);
        if (opcion != 0) ejecutarOperacion(10 + opcion);
    }

    private static void modificarReserva() {
        System.out.println("1. Contratar adicional   2. Reemplazar pasajero   3. Cancelar   4. Nota de ajuste   0. Volver");
        int opcion = opcion("Opción", 4);
        if (opcion != 0) ejecutarOperacion(70 + opcion);
    }

    private static void menuSalidas() {
        System.out.println("1. Asignar guía y vehículo");
        System.out.println("2. Emitir lista e iniciar salida");
        System.out.println("3. Cerrar salida y consultar honorarios   0. Volver");
        int opcion = opcion("Opción", 3);
        if (opcion != 0) ejecutarOperacion(80 + opcion);
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
        String tipo = elegir("Tipo de tour (aventura/cultural/naturaleza)", new String[]{"aventura", "cultural", "naturaleza"});
        Tour tour;
        if (tipo.equals("aventura")) {
            int edad = entero("Edad mínima");
            String nivel = leer("Nivel de dificultad");
            tour = new TourAventura(cod, nom, desc, dias, cMin, cMax, edad, nivel);
        } else if (tipo.equals("cultural")) {
            tour = new TourCultural(cod, nom, desc, dias, cMin, cMax);
        } else {
            tour = new TourNaturaleza(cod, nom, desc, dias, cMin, cMax);
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
            throw new IllegalArgumentException("Cliente no encontrado.");
        }

        String idSalida = leer("ID de Salida (Formato: CODIGOTOUR_FECHA, ej: T01_01/12/2026)");
        Salida salida = controladorOperaciones.buscarSalida(idSalida);
        if (salida == null) {
            throw new IllegalArgumentException("Salida no encontrada.");
        }

        String temporada = elegir("Temporada (alta/media/baja)", new String[]{"alta", "media", "baja"});

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

        String tipo = elegir("Medio (efectivo/transferencia/tarjeta)", new String[]{"efectivo", "transferencia", "tarjeta"});
        MedioPago medio;
        if (tipo.equals("efectivo")) medio = new Efectivo();
        else if (tipo.equals("transferencia")) medio = new Transferencia();
        else medio = new TarjetaCredito();
        Pago pago = controladorVentas.cobrar(numReserva, hoy, monto, medio, entero("Cuotas"));
        System.out.println("Cobro con recargo incluido: $ " + Importes.mostrar(pago.getMontoCobrado()));
    }

    private static void asignarRecursos(String id) {
        Guia g = new Guia(leer("Documento"), leer("Nombre"), leer("Email"), leer("Teléfono"),
                leer("Matrícula/legajo"), importe("Honorario por día"));
        String otra = "s";
        while (otra.equals("s")) {
            g.habilitarCategoria(elegir("Categoría habilitada (aventura/cultural/naturaleza)", new String[]{"aventura", "cultural", "naturaleza"}));
            otra = elegir("¿Otra categoría? (s/n)", new String[]{"s", "n"});
        }
        otra = "s";
        while (otra.equals("s")) {
            g.agregarIdioma(leer("Idioma"));
            otra = elegir("¿Otro idioma? (s/n)", new String[]{"s", "n"});
        }
        Vehiculo v = new Vehiculo(leer("Patente"), entero("Capacidad"), leer("Tipo de vehículo"));
        controladorOperaciones.asignarRecursos(id, g, v);
        System.out.println("Guía y vehículo asignados.");
    }

    private static void operarSalida(int accion) {
        String id = leer("ID salida");
        if (accion == 1) asignarRecursos(id);
        else if (accion == 2) {
            System.out.println(controladorOperaciones.emitirLista(id));
            if (elegir("¿Iniciar esta salida? (s/n)", new String[]{"s", "n"}).equals("s")) {
                controladorOperaciones.iniciarSalida(id, hoy);
                System.out.println("Salida iniciada.");
            }
        } else {
            Salida salida = controladorOperaciones.buscarSalida(id);
            if (salida == null) throw new IllegalArgumentException("Salida inexistente.");
            if (!salida.getEstado().equals("FINALIZADA")) controladorOperaciones.cerrarSalida(id, hoy);
            System.out.println("Honorarios: $ " + Importes.mostrar(controladorAdmin.liquidarHonorariosGuia(id)));
        }
    }

    private static void cargarTarifa() {
        String codigo = leer("Código tour");
        Tarifa tarifa = new Tarifa(elegir("Temporada (alta/media/baja)", new String[]{"alta", "media", "baja"}), leerFecha("Vigente desde"), importe("Precio base"));
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
        if (elegir("¿Interviene proveedor? (s/n)", new String[]{"s", "n"}).equals("s")) {
            proveedor = new Proveedor(leer("Nombre"), leer("CUIT"), leer("Contacto"), leer("Servicio prestado"));
        }
        controladorAdmin.agregarActividad(codigo, new Actividad(dia, orden, horario, descripcion, lugar, proveedor));
    }

    private static Pasajero leerPasajero() {
        return new Pasajero(leer("Documento pasajero"), leer("Nombre y apellido"), leerFecha("Nacimiento"),
                elegir("Tipo (adulto/menor_12/menor_3/jubilado)", new String[]{"adulto", "menor_12", "menor_3", "jubilado"}), leer("Contacto de emergencia"), leer("Restricciones"));
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
        String origen = elegir("Origen (agencia/cliente)", new String[]{"agencia", "cliente"});
        CancelacionReserva c = controladorVentas.cancelarReserva(numero, motivo, origen.equals("agencia"), hoy);
        System.out.println("Devolución: $ " + Importes.mostrar(c.getImporteDevuelto()) + " " + c.getDevoluciones());
    }

    private static void listarReservas() {
        java.util.List<Reserva> lista = reservas.obtenerTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay reservas registradas.");
            return;
        }
        System.out.println("=== RESERVAS REGISTRADAS ===");
        System.out.println("Las reservas PENDIENTES y CONFIRMADAS están activas.");
        for (Reserva reserva : lista) {
            System.out.println("Reserva " + reserva.getNumero()
                    + " | Titular: " + reserva.getCliente().getNombre()
                    + " | Tour: " + reserva.getSalida().getTour().getNombre()
                    + " | Salida: " + reserva.getSalida().getId()
                    + " | Fecha: " + Fecha.mostrar(reserva.getSalida().getFechaInicio())
                    + " | Estado: " + reserva.getEstado()
                    + " | total $ " + Importes.mostrar(reserva.getTotal())
                    + " | saldo $ " + Importes.mostrar(reserva.saldoPendiente()));
            if (!reserva.getNotas().isEmpty()) System.out.println("Notas de ajuste: " + reserva.getNotas());
        }
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

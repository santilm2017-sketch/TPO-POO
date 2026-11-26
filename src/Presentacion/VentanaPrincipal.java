package Presentacion;

import Controladores.*;
import Dominio.*;
import Persistencia.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class VentanaPrincipal extends JFrame implements ActionListener {
    private ControladorVentas ventas;
    private ControladorOperaciones operaciones;
    private ControladorAdministrativo administracion;
    private Fecha hoy;
    private JPanel listado = new JPanel(new GridLayout(0, 1));
    private int pagina;
    private JLabel fecha = new JLabel();
    private List<Reserva> reservas = new ArrayList<>();
    private List<JButton> botonesAccion = new ArrayList<>();
    private List<String> nombresAccion = new ArrayList<>();
    private static class Cancelada extends RuntimeException { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
                try { new VentanaPrincipal().setVisible(true); }
                catch (Cancelada e) { }
                catch (Exception e) { DialogoDatos.mostrarTexto(null, "No se pudo abrir Rumbo Sur", e.getMessage()); }
        });
    }

    public VentanaPrincipal() throws Exception {
        super("Rumbo Sur - Agencia de Turismo");
        Repositorio<Cliente> clientes = new RepositorioClientes();
        Repositorio<Tour> tours = new RepositorioTours();
        Repositorio<Salida> salidas = new RepositorioSalidas();
        ventas = new ControladorVentas(clientes, salidas);
        operaciones = new ControladorOperaciones(salidas, tours);
        administracion = new ControladorAdministrativo(tours, salidas);
        hoy = pedirFecha();
        DatosIniciales.cargar(clientes, tours, salidas, hoy);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 1));
        JTabbedPane secciones = new JTabbedPane();
        secciones.addTab("Clientes", botones(new String[]{"Registrar cliente", "Ver clientes"}));
        secciones.addTab("Tours y salidas", botones(new String[]{"Registrar tour", "Cargar tarifa", "Agregar actividad", "Programar salida", "Ver catálogo"}));
        secciones.addTab("Reservas", botones(new String[]{"Crear reserva", "Ver detalle", "Agregar pasajeros", "Agregar servicio", "Reemplazar pasajero", "Cancelar reserva", "Ajustar precio"}));
        secciones.addTab("Pagos", botones(new String[]{"Registrar pago"}));
        secciones.addTab("Operaciones", botones(new String[]{"Asignar recursos", "Lista de pasajeros", "Iniciar salida", "Cerrar y honorarios", "Registrar personal", "Ver personal"}));
        secciones.addTab("Reportes", botones(new String[]{"Ver reportes", "Cambiar fecha", "Actualizar listado"}));
        add(secciones);
        add(listado);
        JPanel pie = botones(new String[]{"Anterior", "Siguiente"});
        pie.add(fecha);
        add(pie);
        actualizar();
        pack();
    }

    private JPanel botones(String[] nombres) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (String nombre : nombres) {
            JButton boton = new JButton(nombre);
            boton.addActionListener(this);
            botonesAccion.add(boton);
            nombresAccion.add(nombre);
            panel.add(boton);
        }
        return panel;
    }

    public void actionPerformed(ActionEvent evento) {
        try {
            operaciones.controlarPlazos(hoy);
            String accion = "";
            for (int i = 0; i < botonesAccion.size(); i++) {
                if (evento.getSource() == botonesAccion.get(i)) accion = nombresAccion.get(i);
            }
            if (accion.equals("Registrar cliente")) registrarCliente();
            else if (accion.equals("Ver clientes")) mostrarClientes();
            else if (accion.equals("Registrar tour")) registrarTour();
            else if (accion.equals("Cargar tarifa")) tarifa();
            else if (accion.equals("Agregar actividad")) actividad();
            else if (accion.equals("Programar salida")) programar();
            else if (accion.equals("Ver catálogo")) catalogo();
            else if (accion.equals("Crear reserva")) crearReserva();
            else if (accion.equals("Ver detalle")) mostrar("Reserva", detalle(reserva()));
            else if (accion.equals("Agregar pasajeros")) agregarPasajeros();
            else if (accion.equals("Agregar servicio")) servicio();
            else if (accion.equals("Reemplazar pasajero")) reemplazar();
            else if (accion.equals("Cancelar reserva")) cancelar();
            else if (accion.equals("Ajustar precio")) ajustar();
            else if (accion.equals("Registrar pago")) pagar();
            else if (accion.equals("Asignar recursos")) recursos();
            else if (accion.equals("Lista de pasajeros")) mostrar("Pasajeros", operaciones.emitirLista(salida().getId()));
            else if (accion.equals("Iniciar salida")) iniciar();
            else if (accion.equals("Cerrar y honorarios")) cerrar();
            else if (accion.equals("Ver reportes")) mostrar("Reportes", administracion.obtenerReporteOcupacion() + administracion.obtenerReportesEconomicos());
            else if (accion.equals("Registrar personal")) personal();
            else if (accion.equals("Ver personal")) verPersonal();
            else if (accion.equals("Cambiar fecha")) hoy = pedirFecha();
            else if (accion.equals("Anterior") && pagina > 0) pagina--;
            else if (accion.equals("Siguiente") && (pagina + 1) * 3 < reservas.size()) pagina++;
            actualizar();
        } catch (Cancelada e) {
            // Cancelar un formulario no confirma la operación pendiente.
        } catch (ErrorArchivo e) {
            mostrar("Error de archivos", e.getMessage() + "\nLa sesión se cerrará para proteger los datos.");
            dispose();
        } catch (NumberFormatException e) {
            mostrar("Dato inválido", "Revise los campos numéricos: cantidades enteras e importes con coma decimal.");
        } catch (Exception e) {
            mostrar("Operación rechazada", e.getMessage());
        }
    }

    private void actualizar() {
        operaciones.controlarPlazos(hoy);
        reservas = ventas.obtenerReservas();
        if (pagina * 3 >= reservas.size()) pagina = 0;
        listado.removeAll();
        listado.add(new JLabel("RESERVAS REGISTRADAS - página " + (pagina + 1)));
        for (int i = pagina * 3; i < reservas.size() && i < (pagina + 1) * 3; i++) {
            Reserva r = reservas.get(i);
            JPanel fila = new JPanel(new GridLayout(0, 1));
            fila.add(new JLabel("Reserva " + r.getNumero() + " | Titular: " + r.getCliente().getNombre()));
            fila.add(new JLabel(r.getSalida().getTour().getNombre() + " | Salida: " + Fecha.mostrar(r.getSalida().getFechaInicio())));
            fila.add(new JLabel(r.getEstado() + " | Total $ " + Importes.mostrar(r.getTotal()) + " | Saldo $ " + Importes.mostrar(r.saldoPendiente())));
            listado.add(fila);
        }
        if (reservas.isEmpty()) listado.add(new JLabel("No hay reservas registradas."));
        fecha.setText("Fecha: " + Fecha.mostrar(hoy.toString()) + " | Fechas DD/MM/AAAA | Importes con coma");
        pack();
    }

    private String[] formulario(String titulo, String[] campos) {
        return formulario(titulo, campos, new String[campos.length]);
    }

    private String[] formulario(String titulo, String[] campos, String[] anteriores) {
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        JTextField[] entradas = new JTextField[campos.length];
        for (int i = 0; i < campos.length; i++) {
            panel.add(new JLabel(campos[i]));
            entradas[i] = new JTextField(24);
            if (anteriores[i] != null) entradas[i].setText(anteriores[i]);
            panel.add(entradas[i]);
        }
        if (new DialogoDatos(this, titulo, panel, new String[]{"Aceptar", "Cancelar"}).mostrar() != 0) throw new Cancelada();
        String[] valores = new String[campos.length];
        for (int i = 0; i < campos.length; i++) valores[i] = entradas[i].getText().trim();
        return valores;
    }

    private String texto(String titulo) {
        return formulario(titulo, new String[]{titulo})[0];
    }
    private String elegir(String titulo, String[] valores) {
        if (valores.length == 0) throw new IllegalArgumentException("No hay registros para seleccionar.");
        JComboBox<String> lista = new JComboBox<>(valores);
        JPanel panel = new JPanel(new FlowLayout());panel.add(lista);
        if (new DialogoDatos(this, titulo, panel, new String[]{"Aceptar", "Cancelar"}).mostrar() != 0) throw new Cancelada();
        return (String) lista.getSelectedItem();
    }
    private int indice(String titulo, String[] valores) {
        String elegido = elegir(titulo, valores);
        for (int i = 0; i < valores.length; i++) if (valores[i].equals(elegido)) return i;
        throw new Cancelada();
    }
    private boolean confirmar(String mensaje) {
        JPanel panel = new JPanel(new FlowLayout());panel.add(new JLabel(mensaje));
        int respuesta = new DialogoDatos(this, "Rumbo Sur", panel, new String[]{"Sí", "No", "Cancelar"}).mostrar();
        if (respuesta == 2 || respuesta == -1) throw new Cancelada();
        return respuesta == 0;
    }
    private void mostrar(String titulo, String mensaje) { DialogoDatos.mostrarTexto(this, titulo, mensaje); }
    private void exito(String mensaje) { mostrar("Operación realizada", mensaje); }
    private boolean reintentar(String mensaje) {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel(mensaje));
        return new DialogoDatos(this, "Datos incorrectos", panel, new String[]{"Reintentar", "Cancelar"}).mostrar() == 0;
    }
    private Fecha pedirFecha() {
        while (true) {
            try { return Fecha.desdePantalla(texto("Fecha de trabajo DD/MM/AAAA")); }
            catch (IllegalArgumentException e) { mostrar("Fecha inválida", "Use DD/MM/AAAA."); }
        }
    }
    private String temporada() { return elegir("Temporada", new String[]{"baja", "media", "alta"}); }
    private Tour tour() {
        List<Tour> lista = administracion.obtenerTodosTours();
        String[] nombres = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) nombres[i] = lista.get(i).getCodigo() + " | " + lista.get(i).getNombre();
        return lista.get(indice("Seleccionar tour", nombres));
    }
    private Salida salida() {
        List<Salida> lista = ventas.obtenerSalidas();
        String[] nombres = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) nombres[i] = lista.get(i).getId() + " | " + lista.get(i).getTour().getNombre() + " | " + lista.get(i).getEstado();
        return lista.get(indice("Seleccionar salida", nombres));
    }
    private Cliente cliente() {
        List<Cliente> lista = ventas.obtenerClientes();
        String[] nombres = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) nombres[i] = lista.get(i).getDocumento() + " | " + lista.get(i).getNombre();
        return lista.get(indice("Seleccionar cliente", nombres));
    }
    private Reserva reserva() {
        List<Reserva> lista = ventas.obtenerReservas();
        String[] nombres = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            Reserva r = lista.get(i);
            nombres[i] = r.getNumero() + " | " + r.getCliente().getNombre() + " | " + r.getSalida().getId() + " | " + r.getEstado()
                    + " | total $ " + Importes.mostrar(r.getTotal()) + " | saldo $ " + Importes.mostrar(r.saldoPendiente());
        }
        return lista.get(indice("Seleccionar reserva", nombres));
    }
    private Pasajero pasajero(Reserva r) {
        List<Pasajero> lista = r.getPasajeros();
        String[] nombres = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) nombres[i] = lista.get(i).getDocumento() + " | " + lista.get(i).getNombre();
        return lista.get(indice("Seleccionar pasajero", nombres));
    }
    private String detalle(Reserva r) {
        String resultado = "Reserva " + r.getNumero() + " | " + r.getCliente().getNombre() + "\n" + r.getSalida().getId()
                + "\nEstado: " + r.getEstado() + "\nTotal $ " + Importes.mostrar(r.getTotal()) + " | Saldo $ " + Importes.mostrar(r.saldoPendiente()) + "\n";
        for (Pasajero p : r.getPasajeros()) resultado += "\n" + p.getDocumento() + " | " + p.getNombre() + " | " + p.getTipo();
        for (String nota : r.getNotas()) resultado += "\n" + nota;
        return resultado;
    }

    private void registrarCliente() {
        String[] datoDocumento = {""};
        String documento;
        while (true) {
            datoDocumento = formulario("Documento del cliente", new String[]{"Documento del cliente"}, datoDocumento);
            try {
                documento = Validaciones.documento(datoDocumento[0]);
                break;
            } catch (IllegalArgumentException e) {
                if (!reintentar(e.getMessage())) return;
            }
        }
        Cliente existente = ventas.buscarCliente(documento);
        if (existente != null) {
            mostrar("Cliente ya registrado", existente.getNombre() + "\nDocumento: " + existente.getDocumento()
                    + "\nEmail: " + existente.getEmail() + "\nTeléfono: " + existente.getTelefono() + "\nPaís: " + existente.getPaisResidencia());
            return;
        }
        String[] v = new String[4];
        Cliente nuevo;
        while (true) {
            v = formulario("Registrar cliente", new String[]{"Nombre completo", "Email", "Teléfono", "País de residencia"}, v);
            try {
                nuevo = new Cliente(documento, v[0], v[1], v[2], v[3]);
                break;
            } catch (IllegalArgumentException e) {
                if (!reintentar(e.getMessage())) return;
            }
        }
        ventas.registrarCliente(nuevo);
        exito("Cliente registrado.");
    }
    private void mostrarClientes() {
        String resultado = "Clientes registrados\n";
        for (Cliente c : ventas.obtenerClientes()) resultado += c.getDocumento() + " | " + c.getNombre() + " | " + c.getTelefono() + " | " + c.getEmail() + "\n";
        mostrar("Clientes", resultado);
    }
    private void registrarTour() {
        String tipo = elegir("Categoría", new String[]{"cultural", "naturaleza", "aventura"});
        String[] v = formulario("Registrar tour", new String[]{"Código", "Nombre", "Descripción", "Duración en días", "Cupo mínimo", "Cupo máximo"});
        int dias = Integer.parseInt(v[3]), minimo = Integer.parseInt(v[4]), maximo = Integer.parseInt(v[5]);
        Tour t;
        if (tipo.equals("aventura")) {
            String[] a = formulario("Requisitos de aventura", new String[]{"Edad mínima", "Dificultad"});
            t = new TourAventura(v[0], v[1], v[2], dias, minimo, maximo, Integer.parseInt(a[0]), a[1]);
        } else if (tipo.equals("cultural")) t = new TourCultural(v[0], v[1], v[2], dias, minimo, maximo);
        else t = new TourNaturaleza(v[0], v[1], v[2], dias, minimo, maximo);
        administracion.registrarTour(t);
        exito("Tour registrado. Cargue tarifa e itinerario antes de programar una salida.");
    }
    private void tarifa() {
        Tour t = tour(); String temporada = temporada();
        String[] v = formulario("Tarifa", new String[]{"Vigente desde DD/MM/AAAA", "Precio base"});
        administracion.agregarTarifa(t.getCodigo(), new Tarifa(temporada, Fecha.desdePantalla(v[0]).toString(), Importes.leer(v[1])));
        exito("Tarifa registrada.");
    }
    private void actividad() {
        Tour t = tour();
        String[] v = formulario("Actividad", new String[]{"Día", "Orden", "Horario HH:MM", "Descripción", "Lugar"});
        Proveedor proveedor = null;
        if (confirmar("¿La actividad tiene proveedor?")) {
            String[] p = formulario("Proveedor", new String[]{"Nombre", "CUIT", "Contacto", "Servicio"});
            proveedor = new Proveedor(p[0], p[1], p[2], p[3]);
        }
        administracion.agregarActividad(t.getCodigo(), new Actividad(Integer.parseInt(v[0]), Integer.parseInt(v[1]), v[2], v[3], v[4], proveedor));
        exito("Actividad registrada.");
    }
    private void programar() {
        Tour t = tour();
        String[] v = formulario("Programar salida", new String[]{"Inicio DD/MM/AAAA", "Fin DD/MM/AAAA", "Cupo máximo"});
        Salida s = new Salida(t, Fecha.desdePantalla(v[0]).toString(), Fecha.desdePantalla(v[1]).toString(), Integer.parseInt(v[2]));
        operaciones.programarSalida(s); exito("Salida programada: " + s.getId());
    }
    private void catalogo() {
        String texto = "";
        for (Tour t : administracion.obtenerTodosTours()) {
            texto += t.getCodigo() + " | " + t.getNombre() + " | " + t.getCategoria() + "\n";
            for (Tarifa f : t.getTarifas()) texto += f.getTemporada() + " desde " + Fecha.mostrar(f.getFechaVigencia()) + " | $ " + Importes.mostrar(f.getPrecioBase()) + "\n";
            for (Actividad a : t.getItinerario()) {
                texto += "Día " + a.getDia() + ", orden " + a.getOrden() + " | " + a.getDescripcion() + " | " + a.getLugar();
                if (a.getProveedor() != null) texto += " | Proveedor: " + a.getProveedor().getNombre();
                texto += "\n";
            }
            texto += "\n";
        }
        mostrar("Catálogo", texto);
    }
    private Vendedor vendedor() {
        List<Vendedor> lista = new ArrayList<>(); List<String> documentos = new ArrayList<>();
        for (Reserva r : ventas.obtenerReservas()) {
            if (!documentos.contains(r.getVendedor().getDocumento())) { lista.add(r.getVendedor()); documentos.add(r.getVendedor().getDocumento()); }
        }
        String[] nombres = new String[lista.size() + 1]; nombres[0] = "Registrar vendedor para esta venta";
        for (int i = 0; i < lista.size(); i++) nombres[i + 1] = lista.get(i).getLegajo() + " | " + lista.get(i).getNombre();
        int elegido = indice("Vendedor", nombres);
        if (elegido > 0) return lista.get(elegido - 1);
        String[] v = formulario("Vendedor", new String[]{"Documento o código interno", "Nombre", "Email (opcional)", "Teléfono", "Legajo", "Comisión %"});
        return new Vendedor(v[0], v[1], v[2], v[3], v[4], Importes.leer(v[5]));
    }
    private Pasajero nuevoPasajero(Salida s, boolean contratarSeguro) {
        String tipo = elegir("Tipo de pasajero", new String[]{"adulto", "menor_12", "menor_3", "jubilado"});
        String[] v = formulario("Pasajero", new String[]{"Documento numérico", "Nombre", "Nacimiento DD/MM/AAAA", "Teléfono de emergencia", "Restricciones (opcional)"});
        Pasajero p = new Pasajero(v[0], v[1], Fecha.desdePantalla(v[2]).toString(), tipo, v[3], v[4]);
        if (contratarSeguro && s.getTour().getCategoria().equals("aventura")) {
            double precio = Importes.leer(texto("Precio del seguro obligatorio"));
            p.contratarServicio(new ServicioAdicional("Seguro de viaje", precio, true), hoy.toString());
        }
        return p;
    }
    private List<Pasajero> nuevosPasajeros(Salida s) {
        int cantidad = Integer.parseInt(texto("Cantidad de pasajeros nuevos"));
        if (!s.verificarCupoDisponible(cantidad)) throw new IllegalArgumentException("Cantidad inválida o cupo insuficiente.");
        List<Pasajero> lista = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) lista.add(nuevoPasajero(s, true));
        return lista;
    }
    private void crearReserva() throws Exception {
        Cliente c = cliente(); Salida s = salida(); String temporada = temporada(); Vendedor v = vendedor();
        List<Pasajero> pasajeros = nuevosPasajeros(s);
        Reserva r = new Reserva(hoy.toString(), c, v, s);
        for (Pasajero p : pasajeros) r.agregarPasajero(p);
        ventas.crearReserva(r, temporada); exito("Reserva " + r.getNumero() + " creada. Total $ " + Importes.mostrar(r.getTotal()));
    }
    private void agregarPasajeros() throws Exception {
        Reserva r = reserva();
        ventas.comprobarAmpliacion(r.getNumero(), hoy);
        String temporada = ventas.obtenerTemporada(r.getNumero());
        if (temporada.isEmpty()) {
            mostrar("Reserva anterior", "No se guardaba la temporada en esta versión anterior. Seleccione la temporada: se usará su tarifa vigente para calcular solamente la diferencia. El total, los pagos y ajustes previos se conservan.");
            temporada = temporada();
        }
        List<Pasajero> nuevos = nuevosPasajeros(r.getSalida());
        ventas.agregarPasajeros(r.getNumero(), nuevos, hoy, temporada);
        exito("Pasajeros agregados. Total $ " + Importes.mostrar(r.getTotal()) + "; saldo $ " + Importes.mostrar(r.saldoPendiente()));
    }
    private void pagar() {
        Reserva r = reserva(); mostrar("Datos de la reserva", detalle(r));
        String medio = elegir("Medio de pago", new String[]{"efectivo", "transferencia", "tarjeta"});
        String[] v = formulario("Pago", new String[]{"Importe de capital", "Cuotas"});
        MedioPago forma;
        if (medio.equals("efectivo")) forma = new Efectivo();
        else if (medio.equals("transferencia")) forma = new Transferencia();
        else forma = new TarjetaCredito();
        Pago pago = ventas.cobrar(r.getNumero(), hoy, Importes.leer(v[0]), forma, Integer.parseInt(v[1]));
        exito("Cobro $ " + Importes.mostrar(pago.getMontoCobrado()) + "; saldo $ " + Importes.mostrar(r.saldoPendiente()));
    }
    private void servicio() {
        Reserva r = reserva(); Pasajero p = pasajero(r);
        String[] v = formulario("Servicio adicional", new String[]{"Nombre", "Precio"});
        ventas.contratar(r.getNumero(), p.getDocumento(), new ServicioAdicional(v[0], Importes.leer(v[1]), false), hoy);
        exito("Servicio agregado.");
    }
    private void reemplazar() throws Exception {
        Reserva r = reserva(); Pasajero anterior = pasajero(r);
        ventas.reemplazar(r.getNumero(), anterior.getDocumento(), nuevoPasajero(r.getSalida(), false), hoy);
        exito("Pasajero reemplazado.");
    }
    private void cancelar() {
        Reserva r = reserva(); String motivo = texto("Motivo de cancelación");
        String origen = elegir("Origen", new String[]{"cliente", "agencia"});
        if (!confirmar("¿Cancelar la reserva " + r.getNumero() + "?")) return;
        CancelacionReserva c = ventas.cancelarReserva(r.getNumero(), motivo, origen.equals("agencia"), hoy);
        mostrar("Cancelación", "Devolución $ " + Importes.mostrar(c.getImporteDevuelto()) + "\n" + c.getDevoluciones());
    }
    private void ajustar() {
        Reserva r = reserva(); String[] v = formulario("Ajuste", new String[]{"Motivo", "Diferencia (+/-)"});
        ventas.ajustar(r.getNumero(), v[0], Importes.leer(v[1]), hoy); exito("Ajuste registrado.");
    }
    private void recursos() {
        Salida s = salida();
        List<Guia> guias = new ArrayList<>(); List<Vehiculo> vehiculos = new ArrayList<>();
        for (Salida otra : ventas.obtenerSalidas()) {
            if (otra.getGuia() != null && !guias.contains(otra.getGuia())) guias.add(otra.getGuia());
            if (otra.getVehiculo() != null && !vehiculos.contains(otra.getVehiculo())) vehiculos.add(otra.getVehiculo());
        }
        String[] nombres = new String[guias.size() + 1]; nombres[0] = "Cargar guía nuevo";
        for (int i = 0; i < guias.size(); i++) nombres[i + 1] = guias.get(i).getDocumento() + " | " + guias.get(i).getNombre();
        int opcion = indice("Guía", nombres); Guia g;
        if (opcion > 0) g = guias.get(opcion - 1);
        else {
            String[] v = formulario("Guía", new String[]{"Documento o código interno", "Nombre", "Email (opcional)", "Teléfono", "Matrícula/legajo", "Honorario diario"});
            g = new Guia(v[0], v[1], v[2], v[3], v[4], Importes.leer(v[5]));
            do { g.habilitarCategoria(elegir("Categoría habilitada", new String[]{"cultural", "naturaleza", "aventura"})); } while (confirmar("¿Agregar otra categoría?"));
            do { g.agregarIdioma(texto("Idioma")); } while (confirmar("¿Agregar otro idioma?"));
        }
        nombres = new String[vehiculos.size() + 1]; nombres[0] = "Cargar vehículo nuevo";
        for (int i = 0; i < vehiculos.size(); i++) nombres[i + 1] = vehiculos.get(i).getPatente();
        opcion = indice("Vehículo", nombres); Vehiculo vehiculo;
        if (opcion > 0) vehiculo = vehiculos.get(opcion - 1);
        else {
            String[] v = formulario("Vehículo", new String[]{"Patente", "Capacidad", "Tipo"});
            vehiculo = new Vehiculo(v[0], Integer.parseInt(v[1]), v[2]);
        }
        operaciones.asignarRecursos(s.getId(), g, vehiculo); exito("Guía y vehículo asignados.");
    }
    private void iniciar() {
        Salida s = salida(); mostrar("Lista de pasajeros", operaciones.emitirLista(s.getId()));
        if (confirmar("¿Iniciar la salida " + s.getId() + "?")) { operaciones.iniciarSalida(s.getId(), hoy); exito("Salida iniciada."); }
    }
    private void cerrar() {
        Salida s = salida();
        if (!s.getEstado().equals("FINALIZADA")) {
            if (!confirmar("¿Cerrar la salida " + s.getId() + "?")) return;
            operaciones.cerrarSalida(s.getId(), hoy);
        }
        exito("Honorarios $ " + Importes.mostrar(administracion.liquidarHonorariosGuia(s.getId())));
    }
    private void personal() {
        String rol = elegir("Rol", new String[]{"coordinador", "administrador"});
        String[] v = formulario("Personal", new String[]{"Documento numérico", "Nombre", "Email (opcional)", "Teléfono", "Legajo"});
        administracion.registrarPersonal(new PersonalAdministrativo(v[0], v[1], v[2], v[3], v[4], rol)); exito("Personal registrado.");
    }
    private void verPersonal() {
        String texto = "";
        for (PersonalAdministrativo p : administracion.obtenerPersonal()) texto += p.getRol() + " | " + p.getLegajo() + " | " + p.getNombre() + " | " + p.getDocumento() + " | " + p.getTelefono() + "\n";
        mostrar("Personal", texto.isEmpty() ? "No hay personal registrado." : texto);
    }
}

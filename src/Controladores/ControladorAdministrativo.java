package Controladores;

import java.util.Map;
import java.util.HashMap;

import Dominio.*;
import Persistencia.*;
import java.util.List;

public class ControladorAdministrativo {
    private Repositorio<Salida> salidaRepo;
    private Repositorio<Tour> tourRepo;
    private Repositorio<PersonalAdministrativo> personalRepo;

    public ControladorAdministrativo(Repositorio<Tour> tourRepo, Repositorio<Salida> salidaRepo) {
        this.tourRepo = tourRepo;
        this.salidaRepo = salidaRepo;
        this.personalRepo = new RepositorioPersonal();
    }

    public void registrarPersonal(PersonalAdministrativo persona) {
        personalRepo.guardar(persona);
    }

    public List<PersonalAdministrativo> obtenerPersonal() {
        return personalRepo.obtenerTodos();
    }

    public void registrarTour(Tour tour) {
        if (tour == null) {
            throw new IllegalArgumentException("El tour no puede ser nulo.");
        }
        if (tourRepo.buscarPorId(tour.getCodigo()) != null) {
            throw new RuntimeException("Ya existe un tour registrado con el código: " + tour.getCodigo());
        }
        tourRepo.guardar(tour);
    }

    public List<Tour> obtenerTodosTours() {
        return tourRepo.obtenerTodos();
    }

    public double liquidarHonorariosGuia(String idSalida) {
        Salida salida = salidaRepo.buscarPorId(idSalida);
        if (salida == null || salida.getGuia() == null) {
            throw new IllegalArgumentException("Verifique la salida y su guía asignado.");
        }
        return salida.getGuia().calcularHonorarios(salida);
    }

    public void agregarTarifa(String codigo, Tarifa tarifa) {
        Tour t = exigirTour(codigo);
        t.agregarTarifa(tarifa);
        tourRepo.actualizar(t);
    }

    public void agregarActividad(String codigo, Actividad actividad) {
        Tour t = exigirTour(codigo);
        t.agregarActividad(actividad);
        tourRepo.actualizar(t);
    }

    public Tour exigirTour(String codigo) {
        Tour t = tourRepo.buscarPorId(codigo);
        if (t == null) throw new IllegalArgumentException("Tour inexistente.");
        return t;
    }

    public void emitirReportesEconomicos() { System.out.print(obtenerReportesEconomicos()); }

    public String obtenerReportesEconomicos() {
        String texto = "";
        Map<String, Double> ventas = new HashMap<>();
        Map<String, Double> ingresos = new HashMap<>();
        texto += "Cancelaciones (reserva, fecha, motivo, devoluciones)" + "\n";
        for (Salida s : salidaRepo.obtenerTodos()) {
            double ingreso = 0;
            for (Reserva r : s.getReservas()) {
                if (r.vigente()) {
                    String legajo = r.getVendedor().getLegajo();
                    if (!ventas.containsKey(legajo)) ventas.put(legajo, 0.0);
                    ventas.put(legajo, ventas.get(legajo) + r.getTotal());
                }
                for (Pago p : r.getPagos()) ingreso += p.getMontoCobrado();
                CancelacionReserva c = r.getCancelacion();
                if (c != null) {
                    ingreso -= c.getImporteDevuelto();
                    texto += r.getNumero() + " " + Fecha.mostrar(c.getFecha()) + " " + c.getMotivo() + " " + c.getDevoluciones() + "\n";
                }
            }
            String codigo = s.getTour().getCodigo();
            if (!ingresos.containsKey(codigo)) ingresos.put(codigo, 0.0);
            ingresos.put(codigo, Importes.redondear(ingresos.get(codigo) + ingreso));
        }
        texto += "Ventas por vendedor" + "\n";
        for (String legajo : ventas.keySet()) texto += legajo + " | $ " + Importes.mostrar(ventas.get(legajo)) + "\n";
        texto += "Ingresos netos por tour" + "\n";
        for (String codigo : ingresos.keySet()) texto += codigo + " | $ " + Importes.mostrar(ingresos.get(codigo)) + "\n";
        return texto;
    }

    public void emitirReporteOcupacion() { System.out.print(obtenerReporteOcupacion()); }

    public String obtenerReporteOcupacion() {
        String texto = "";
        List<Salida> salidas = salidaRepo.obtenerTodos();
        texto += "=== REPORTE DE OCUPACIÓN DE SALIDAS ===" + "\n";
        if (salidas.isEmpty()) {
            texto += "No hay salidas registradas en el sistema." + "\n";
            return texto;
        }

        for (Salida s : salidas) {
            texto += "Tour: " + s.getTour().getNombre() + " | Fecha Inicio: " + Fecha.mostrar(s.getFechaInicio()) + "\n";
            texto += "Cupo Maximo: " + s.getCupoMaximo() + " | Pasajeros Reservados: " + s.getCuposReservados() + "\n";
            double ocupacion = ((double) s.getCuposReservados() / s.getCupoMaximo()) * 100;
            texto += "Porcentaje de ocupación: " + Importes.mostrar(ocupacion) + "%" + "\n";
            texto += "----------------------------------------" + "\n";
        }
        return texto;
    }
}

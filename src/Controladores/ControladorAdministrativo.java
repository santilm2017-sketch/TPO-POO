package Controladores;

import java.util.Map;
import java.util.HashMap;

import Dominio.*;
import Persistencia.*;
import java.util.List;

public class ControladorAdministrativo {
    private Repositorio<Salida> salidaRepo;
    private Repositorio<Tour> tourRepo;

    public ControladorAdministrativo(Repositorio<Tour> tourRepo, Repositorio<Salida> salidaRepo) {
        this.tourRepo = tourRepo;
        this.salidaRepo = salidaRepo;
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

    public void emitirReportesEconomicos() {
        Map<String, Double> ventas = new HashMap<>();
        Map<String, Double> ingresos = new HashMap<>();
        System.out.println("Cancelaciones (reserva, fecha, motivo, devoluciones)");
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
                    System.out.println(r.getNumero() + " " + Fecha.mostrar(c.getFecha()) + " " + c.getMotivo() + " " + c.getDevoluciones());
                }
            }
            String codigo = s.getTour().getCodigo();
            if (!ingresos.containsKey(codigo)) ingresos.put(codigo, 0.0);
            ingresos.put(codigo, Importes.redondear(ingresos.get(codigo) + ingreso));
        }
        System.out.println("Ventas por vendedor");
        for (String legajo : ventas.keySet()) System.out.println(legajo + " | $ " + Importes.mostrar(ventas.get(legajo)));
        System.out.println("Ingresos netos por tour");
        for (String codigo : ingresos.keySet()) System.out.println(codigo + " | $ " + Importes.mostrar(ingresos.get(codigo)));
    }

    public void emitirReporteOcupacion() {
        List<Salida> salidas = salidaRepo.obtenerTodos();
        System.out.println("=== REPORTE DE OCUPACIÓN DE SALIDAS ===");
        if (salidas.isEmpty()) {
            System.out.println("No hay salidas registradas en el sistema.");
            return;
        }

        for (Salida s : salidas) {
            System.out.println("Tour: " + s.getTour().getNombre() + " | Fecha Inicio: " + Fecha.mostrar(s.getFechaInicio()));
            System.out.println("Cupo Maximo: " + s.getCupoMaximo() + " | Pasajeros Reservados: " + s.getCuposReservados());
            double ocupacion = ((double) s.getCuposReservados() / s.getCupoMaximo()) * 100;
            System.out.println("Porcentaje de ocupación: " + Importes.mostrar(ocupacion) + "%");
            System.out.println("----------------------------------------");
        }
    }
}

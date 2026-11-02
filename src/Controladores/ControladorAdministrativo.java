package Controladores;

import Dominio.*;
import Persistencia.*;
import java.util.List;

public class ControladorAdministrativo {
    private IRepository<Tour> tourRepo;
    private IRepository<Salida> salidaRepo;

    public ControladorAdministrativo() {
        this.tourRepo = new TourRepository();
        this.salidaRepo = new SalidaRepository();
    }

    public void registrarTour(Tour tour) {
        if (tour == null) {
            throw new IllegalArgumentException("El tour no puede ser nulo.");
        }
        if (tourRepo.buscarPorId(tour.getCodigo()) != null) {
            throw new IllegalStateException("Ya existe un tour registrado con el codigo: " + tour.getCodigo());
        }
        tourRepo.guardar(tour);
    }

    public Tour buscarTour(String codigo) {
        return tourRepo.buscarPorId(codigo);
    }

    public List<Tour> obtenerTodosTours() {
        return tourRepo.obtenerTodos();
    }

    public double liquidarHonorariosGuia(Guia guia, String idSalida) {
        if (guia == null) {
            throw new IllegalArgumentException("El guia ingresado no es valido.");
        }
        Salida salida = salidaRepo.buscarPorId(idSalida);
        if (salida == null) {
            throw new IllegalArgumentException("No se encontro la salida indicada.");
        }
        if (!"FINALIZADA".equalsIgnoreCase(salida.getEstado())) {
            throw new IllegalStateException("Solo se pueden liquidar honorarios de salidas en estado FINALIZADA.");
        }

        return guia.calcularHonorarios(salida);
    }

    public void emitirReporteOcupacion() {
        List<Salida> salidas = salidaRepo.obtenerTodos();
        System.out.println("=== REPORTE DE OCUPACION DE SALIDAS ===");
        if (salidas.isEmpty()) {
            System.out.println("No hay salidas registradas en el sistema.");
            return;
        }

        for (Salida s : salidas) {
            System.out.println("Tour: " + s.getTour().getNombre() + " | Fecha Inicio: " + s.getFechaInicio());
            System.out.println("Cupo Maximo: " + s.getCupoMaximo() + " | Pasajeros Reservados: " + s.getCuposReservados());
            double ocupacion = ((double) s.getCuposReservados() / s.getCupoMaximo()) * 100;
            System.out.printf("Porcentaje de Ocupacion: %.2f%%\n", ocupacion);
            System.out.println("----------------------------------------");
        }
    }
}

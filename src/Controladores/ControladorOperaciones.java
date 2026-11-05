package Controladores;

import Dominio.*;
import Persistencia.*;

public class ControladorOperaciones {
    private Repositorio<Salida> salidaRepo;
    private Repositorio<Tour> tourRepo;

    public ControladorOperaciones(Repositorio<Salida> salidaRepo, Repositorio<Tour> tourRepo) {
        this.salidaRepo = salidaRepo;
        this.tourRepo = tourRepo;
        for (Salida s : salidaRepo.obtenerTodos()) {
            Tour t = tourRepo.buscarPorId(s.getTour().getCodigo());
            if (t != null) s.vincularTour(t);
        }
    }
    public void programarSalida(Salida salida) {
        if (salida == null || salida.getTour() == null) {
            throw new IllegalArgumentException("La salida debe tener un tour asociado valido.");
        }

        if (tourRepo.buscarPorId(salida.getTour().getCodigo()) == null) {
            throw new RuntimeException("El tour indicado no se encuentra registrado en el sistema.");
        }

        if (salida.getTour().getItinerario().isEmpty() || salida.getTour().getTarifas().isEmpty()) {
            throw new RuntimeException("Cargue el itinerario y al menos una tarifa antes de programar.");
        }
        salidaRepo.guardar(salida);
    }

    public void asignarRecursos(String idSalida, Guia guia, Vehiculo vehiculo) {
        Salida salida = exigirSalida(idSalida);
        if (guia == null || vehiculo == null) throw new IllegalArgumentException("Guía y vehículo obligatorios.");
        // Validar el transporte antes de cambiar el guía evita una asignación parcial.
        if (!salida.abierta() || !vehiculo.verificarCapacidad(salida.getCupoMaximo())) {
            throw new RuntimeException("Salida cerrada o capacidad insuficiente.");
        }
        for (Salida otra : salidaRepo.obtenerTodos()) {
            if (otra != salida && seSuperponen(salida, otra)) {
                if (otra.getGuia() != null && otra.getGuia().getDocumento().equals(guia.getDocumento())) {
                    throw new RuntimeException("Guía ocupado.");
                }
                if (otra.getVehiculo() != null && otra.getVehiculo().getPatente().toLowerCase().equals(vehiculo.getPatente().toLowerCase())) {
                    throw new RuntimeException("Vehículo ocupado.");
                }
            }
        }
        salida.asignarGuia(guia);
        salida.asignarVehiculo(vehiculo);
        salidaRepo.actualizar(salida);
    }

    public void iniciarSalida(String idSalida, Fecha hoy) {
        Salida salida = exigirSalida(idSalida);

        if (hoy.toString().compareTo(salida.getFechaInicio()) < 0) {
            throw new RuntimeException("Aún no llegó la fecha de inicio.");
        }

        salida.iniciar();
        salidaRepo.actualizar(salida);
    }

    public void cerrarSalida(String idSalida, Fecha hoy) {
        Salida salida = exigirSalida(idSalida);

        if (hoy.toString().compareTo(salida.getFechaFin()) < 0) throw new RuntimeException("Aún no llegó la fecha de fin.");
        salida.cerrar();
        salidaRepo.actualizar(salida);
    }

    private boolean seSuperponen(Salida a, Salida b) {
        return !"CANCELADA".equals(b.getEstado()) && a.getFechaInicio().compareTo(b.getFechaFin()) <= 0
                && b.getFechaInicio().compareTo(a.getFechaFin()) <= 0;
    }

    public String emitirLista(String id) {
        Salida s = exigirSalida(id);
        String lista = s.emitirListaPasajeros();
        salidaRepo.actualizar(s);
        return lista;
    }

    public void controlarPlazos(Fecha hoy) {
        for (Salida s : salidaRepo.obtenerTodos()) {
            String estado = s.getEstado();
            int ocupacion = s.getCuposReservados();
            s.controlarPlazos(hoy);
            if (!estado.equals(s.getEstado()) || ocupacion != s.getCuposReservados()) salidaRepo.actualizar(s);
        }
    }

    private Salida exigirSalida(String id) {
        Salida salida = buscarSalida(id);
        if (salida == null) throw new IllegalArgumentException("Salida inexistente.");
        return salida;
    }

    public Salida buscarSalida(String idSalida) {
        return salidaRepo.buscarPorId(idSalida);
    }

}

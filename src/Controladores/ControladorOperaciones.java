package Controladores;

import Dominio.*;
import Persistencia.*;
import java.util.List;

public class ControladorOperaciones {
    private IRepository<Salida> salidaRepo;
    private IRepository<Tour> tourRepo;

    public ControladorOperaciones() {
        this.salidaRepo = new SalidaRepository();
        this.tourRepo = new TourRepository();
    }

    public void programarSalida(Salida salida) {
        if (salida == null || salida.getTour() == null) {
            throw new IllegalArgumentException("La salida debe tener un tour asociado valido.");
        }

        if (tourRepo.buscarPorId(salida.getTour().getCodigo()) == null) {
            throw new IllegalStateException("El tour indicado no se encuentra registrado en el sistema.");
        }

        salidaRepo.guardar(salida);
    }

    public void asignarGuia(String idSalida, Guia guia) {
        Salida salida = salidaRepo.buscarPorId(idSalida);
        if (salida == null) {
            throw new IllegalArgumentException("No se encontro la salida indicada.");
        }
        if (guia == null) {
            throw new IllegalArgumentException("El guia ingresado no puede ser nulo.");
        }

        salida.asignarGuia(guia);
        salidaRepo.actualizar(salida);
    }

    public void asignarVehiculo(String idSalida, Vehiculo vehiculo) {
        Salida salida = salidaRepo.buscarPorId(idSalida);
        if (salida == null) {
            throw new IllegalArgumentException("No se encontro la salida indicada.");
        }
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehiculo ingresado no puede ser nulo.");
        }

        salida.asignarVehiculo(vehiculo);
        salidaRepo.actualizar(salida);
    }

    public void iniciarSalida(String idSalida) {
        Salida salida = salidaRepo.buscarPorId(idSalida);
        if (salida == null) {
            throw new IllegalArgumentException("No se encontro la salida indicada.");
        }

        if (salida.getGuia() == null) {
            throw new IllegalStateException("No se puede iniciar una salida sin haber asignado un guia previamente.");
        }

        salida.setEstado("EN_CURSO");
        salidaRepo.actualizar(salida);
    }

    public void cerrarSalida(String idSalida) {
        Salida salida = salidaRepo.buscarPorId(idSalida);
        if (salida == null) {
            throw new IllegalArgumentException("No se encontro la salida indicada.");
        }

        salida.setEstado("FINALIZADA");
        salidaRepo.actualizar(salida);
    }

    public Salida buscarSalida(String idSalida) {
        return salidaRepo.buscarPorId(idSalida);
    }

    public List<Salida> obtenerTodasSalidas() {
        return salidaRepo.obtenerTodos();
    }
}

package Persistencia;

import Dominio.Tour;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TourRepository implements IRepository<Tour> {
    private final String rutaArchivo = "tours.dat";
    private List<Tour> tours;

    public TourRepository() {
        this.tours = cargarDesdeArchivo();
    }

    @Override
    public void guardar(Tour tour) {
        if (tour != null) {
            tours.add(tour);
            guardarEnArchivo();
        }
    }

    @Override
    public Tour buscarPorId(String codigo) {
        for (Tour t : tours) {
            if (t.getCodigo().equalsIgnoreCase(codigo)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public List<Tour> obtenerTodos() {
        return new ArrayList<>(tours);
    }

    @Override
    public void actualizar(Tour tour) {
        for (int i = 0; i < tours.size(); i++) {
            if (tours.get(i).getCodigo().equalsIgnoreCase(tour.getCodigo())) {
                tours.set(i, tour);
                guardarEnArchivo();
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Tour> cargarDesdeArchivo() {
        File f = new File(rutaArchivo);
        if (!f.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Tour>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(tours);
        } catch (IOException e) {
            System.err.println("Error al guardar tours en archivo: " + e.getMessage());
        }
    }
}

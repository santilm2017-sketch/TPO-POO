package Persistencia;

import Dominio.Tour;
import java.util.ArrayList;
import java.util.List;

public class RepositorioTours implements Repositorio<Tour> {
    private final ArchivoLista<Tour> archivo = new ArchivoLista<>("tours.dat");
    private List<Tour> datos;

    public RepositorioTours() {
        datos = archivo.leer();
    }

    public Tour buscarPorId(String id) {
        if (id == null) return null;
        for (Tour elemento : datos) {
            if (elemento.getCodigo().toLowerCase().equals(id.trim().toLowerCase())) return elemento;
        }
        return null;
    }

    public List<Tour> obtenerTodos() { return new ArrayList<>(datos); }

    public void guardar(Tour elemento) {
        if (buscarPorId(elemento.getCodigo()) != null) throw new IllegalArgumentException("Identificador duplicado.");
        datos.add(elemento);
        archivo.escribir(datos);
    }

    public void actualizar(Tour elemento) {
        Tour anterior = buscarPorId(elemento.getCodigo());
        if (anterior == null) throw new IllegalArgumentException("Registro inexistente.");
        datos.set(datos.indexOf(anterior), elemento);
        archivo.escribir(datos);
    }
}

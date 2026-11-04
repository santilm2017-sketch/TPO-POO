package Persistencia;

import Dominio.Salida;
import Dominio.Reserva;
import java.util.ArrayList;
import java.util.List;

public class RepositorioSalidas implements Repositorio<Salida> {
    private final ArchivoLista<Salida> archivo = new ArchivoLista<>("salidas-reservas.dat");
    private List<Salida> datos;

    public RepositorioSalidas() {
        datos = archivo.leer();
        for (Salida salida : datos) {
            for (Reserva r : salida.getReservas()) Reserva.continuarNumeracion(r.getNumero());
        }
    }

    public Salida buscarPorId(String id) {
        if (id == null) return null;
        for (Salida elemento : datos) {
            String anterior = elemento.getTour().getCodigo() + "_" + elemento.getFechaInicio();
            if (elemento.getId().toLowerCase().equals(id.trim().toLowerCase())
                    || anterior.toLowerCase().equals(id.trim().toLowerCase())) return elemento;
        }
        return null;
    }

    public List<Salida> obtenerTodos() { return new ArrayList<>(datos); }

    public void guardar(Salida elemento) {
        if (buscarPorId(elemento.getId()) != null) throw new IllegalArgumentException("Identificador duplicado.");
        datos.add(elemento);
        archivo.escribir(datos);
    }

    public void actualizar(Salida elemento) {
        Salida anterior = buscarPorId(elemento.getId());
        if (anterior == null) throw new IllegalArgumentException("Registro inexistente.");
        datos.set(datos.indexOf(anterior), elemento);
        archivo.escribir(datos);
    }
}

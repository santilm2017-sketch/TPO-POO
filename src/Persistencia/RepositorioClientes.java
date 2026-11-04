package Persistencia;

import Dominio.Cliente;
import java.util.ArrayList;
import java.util.List;

public class RepositorioClientes implements Repositorio<Cliente> {
    private final ArchivoLista<Cliente> archivo = new ArchivoLista<>("clientes.dat");
    private List<Cliente> datos;

    public RepositorioClientes() {
        datos = archivo.leer();
    }

    public Cliente buscarPorId(String id) {
        if (id == null) return null;
        for (Cliente elemento : datos) {
            if (elemento.getDocumento().toLowerCase().equals(id.trim().toLowerCase())) return elemento;
        }
        return null;
    }

    public List<Cliente> obtenerTodos() { return new ArrayList<>(datos); }

    public void guardar(Cliente elemento) {
        if (buscarPorId(elemento.getDocumento()) != null) throw new IllegalArgumentException("Identificador duplicado.");
        datos.add(elemento);
        archivo.escribir(datos);
    }

    public void actualizar(Cliente elemento) {
        Cliente anterior = buscarPorId(elemento.getDocumento());
        if (anterior == null) throw new IllegalArgumentException("Registro inexistente.");
        datos.set(datos.indexOf(anterior), elemento);
        archivo.escribir(datos);
    }
}

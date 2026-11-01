package Persistencia;

import Dominio.Cliente;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepository implements IRepository<Cliente> {
    private final String rutaArchivo = "clientes.dat";
    private List<Cliente> clientes;

    public ClienteRepository() {
        this.clientes = cargarDesdeArchivo();
    }

    @Override
    public void guardar(Cliente cliente) {
        if (cliente != null) {
            clientes.add(cliente);
            guardarEnArchivo();
        }
    }

    @Override
    public Cliente buscarPorId(String documento) {
        for (Cliente c : clientes) {
            if (c.getDocumento().equalsIgnoreCase(documento)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Cliente> obtenerTodos() {
        return new ArrayList<>(clientes);
    }

    @Override
    public void actualizar(Cliente cliente) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getDocumento().equalsIgnoreCase(cliente.getDocumento())) {
                clientes.set(i, cliente);
                guardarEnArchivo();
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Cliente> cargarDesdeArchivo() {
        File f = new File(rutaArchivo);
        if (!f.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Cliente>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(clientes);
        } catch (IOException e) {
            System.err.println("Error al guardar clientes en archivo: " + e.getMessage());
        }
    }
}

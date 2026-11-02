package Persistencia;

import Dominio.Salida;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SalidaRepository implements IRepository<Salida> {
    private final String rutaArchivo = "salidas.dat";
    private List<Salida> salidas;

    public SalidaRepository() {
        this.salidas = cargarDesdeArchivo();
    }

    @Override
    public void guardar(Salida salida) {
        if (salida != null) {
            salidas.add(salida);
            guardarEnArchivo();
        }
    }

    @Override
    public Salida buscarPorId(String idSalida) {
        // Busca por código de tour
        if (idSalida == null) {
            return null;
        }
        idSalida = idSalida.trim();
        for (Salida s : salidas) {
            String clave = s.getTour().getCodigo()
                    + "_" + s.getFechaInicio();

            if (clave.equalsIgnoreCase(idSalida)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public List<Salida> obtenerTodos() {
        return new ArrayList<>(salidas);
    }

    @Override
    public void actualizar(Salida salida) {
        for (int i = 0; i < salidas.size(); i++) {
            Salida s = salidas.get(i);
            if (s.getTour().getCodigo().equalsIgnoreCase(salida.getTour().getCodigo())
                    && s.getFechaInicio().equalsIgnoreCase(salida.getFechaInicio())) {
                salidas.set(i, salida);
                guardarEnArchivo();
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Salida> cargarDesdeArchivo() {
        File f = new File(rutaArchivo);
        if (!f.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Salida>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(salidas);
        } catch (IOException e) {
            System.err.println("Error al guardar salidas en archivo: " + e.getMessage());
        }
    }
}

package Persistencia;

import Dominio.Reserva;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaRepository implements IRepository<Reserva> {
    private final String rutaArchivo = "reservas.dat";
    private List<Reserva> reservas;

    public ReservaRepository() {
        this.reservas = cargarDesdeArchivo();
    }

    @Override
    public void guardar(Reserva reserva) {
        if (reserva != null) {
            reservas.add(reserva);
            guardarEnArchivo();
        }
    }

    @Override
    public Reserva buscarPorId(String numeroReserva) {
        try {
            int numero = Integer.parseInt(numeroReserva);
            for (Reserva r : reservas) {
                if (r.getNumero() == numero) {
                    return r;
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    @Override
    public List<Reserva> obtenerTodos() {
        return new ArrayList<>(reservas);
    }

    @Override
    public void actualizar(Reserva reserva) {
        for (int i = 0; i < reservas.size(); i++) {
            if (reservas.get(i).getNumero() == reserva.getNumero()) {
                reservas.set(i, reserva);
                guardarEnArchivo();
                return;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Reserva> cargarDesdeArchivo() {
        File f = new File(rutaArchivo);
        if (!f.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (List<Reserva>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void guardarEnArchivo() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(reservas);
        } catch (IOException e) {
            System.err.println("Error al guardar reservas en archivo: " + e.getMessage());
        }
    }
}

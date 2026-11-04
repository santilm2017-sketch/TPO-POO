package Persistencia;

import Dominio.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioReservas implements Repositorio<Reserva> {
    private final Repositorio<Salida> salidas;

    public RepositorioReservas(Repositorio<Salida> salidas) { this.salidas = salidas; }

    public void guardar(Reserva r) {
        if (buscarPorId(("" + r.getNumero())) != null) throw new RuntimeException("Número duplicado.");
        r.confirmarAlta();
        actualizar(r);
    }

    public Reserva buscarPorId(String id) {
        for (Reserva r : obtenerTodos()) if (("" + r.getNumero()).equals(id)) return r;
        return null;
    }

    public List<Reserva> obtenerTodos() {
        List<Reserva> todas = new ArrayList<>();
        for (Salida salida : salidas.obtenerTodos()) {
            for (Reserva reserva : salida.getReservas()) todas.add(reserva);
        }
        return todas;
    }

    public void actualizar(Reserva r) { salidas.actualizar(r.getSalida()); }
}

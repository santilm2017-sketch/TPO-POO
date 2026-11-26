package Persistencia;

import Dominio.CondicionesReserva;
import java.util.List;

public class RepositorioCondiciones {
    private ArchivoLista<CondicionesReserva> archivo = new ArchivoLista<>("condiciones-reservas.dat");
    private List<CondicionesReserva> datos = archivo.leer();
    public RepositorioCondiciones() {
        for (CondicionesReserva c : datos) Dominio.Reserva.continuarNumeracion(c.getNumero());
    }
    public CondicionesReserva buscar(int numero) {
        for (CondicionesReserva c : datos) if (c.getNumero() == numero) return c;
        return null;
    }
    public void guardar(CondicionesReserva condiciones) {
        if (buscar(condiciones.getNumero()) != null) throw new IllegalArgumentException("La reserva ya tiene condiciones de precio.");
        datos.add(condiciones);
        archivo.escribir(datos);
    }
}

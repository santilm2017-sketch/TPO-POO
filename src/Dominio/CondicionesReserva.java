package Dominio;

import java.io.Serializable;

// Datos de precio nuevos, separados del formato de Reserva ya utilizado.
public class CondicionesReserva implements Serializable {
    private int numero;
    private String temporada;
    private double tarifa;
    public CondicionesReserva(int numero, String temporada, double tarifa) {
        if (numero < 1 || !Importes.esValido(tarifa)) throw new IllegalArgumentException("Número de reserva o tarifa inválidos.");
        temporada = Validaciones.obligatorio(temporada, "Temporada").toLowerCase();
        if (!"baja".equals(temporada) && !"media".equals(temporada) && !"alta".equals(temporada))
            throw new IllegalArgumentException("Temporada inválida.");
        this.numero = numero;
        this.temporada = temporada;
        this.tarifa = tarifa;
    }
    public int getNumero() { return numero; }
    public String getTemporada() { return temporada; }
    public double getTarifa() { return tarifa; }
}


package Dominio;

import java.io.Serializable;

public class Tarifa implements Serializable {
    private String temporada;
    private String fechaVigencia; // Formato AAAA-MM-DD
    private double precioBase;

    public Tarifa(String temporada, String fechaVigencia, double precioBase) {
        if (temporada == null ||
                (!temporada.equalsIgnoreCase("Baja") &&
                        !temporada.equalsIgnoreCase("Media") &&
                        !temporada.equalsIgnoreCase("Alta"))) {
            throw new IllegalArgumentException("La temporada debe ser Baja, Media o Alta.");
        }

        if (fechaVigencia == null || fechaVigencia.length() != 10) {
            throw new IllegalArgumentException("La fecha debe tener formato AAAA-MM-DD.");
        }

        if (precioBase < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }

        this.temporada = temporada;
        this.fechaVigencia = fechaVigencia;
        this.precioBase = precioBase;
    }

    public boolean esVigente(String fecha) {
        if (fecha == null || fecha.length() != 10) {
            throw new IllegalArgumentException("La fecha debe tener formato AAAA-MM-DD.");
        }

        // Las fechas en formato AAAA-MM-DD pueden compararse como texto.
        return fecha.compareTo(fechaVigencia) >= 0;
    }

    public String getTemporada() {
        return temporada;
    }

    public String getFechaVigencia() {
        return fechaVigencia;
    }

    public double getPrecioBase() {
        return precioBase;
    }
}

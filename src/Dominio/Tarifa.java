package Dominio;

import java.io.Serializable;

public class Tarifa implements Serializable {
    private String temporada;
    private String fechaVigencia; // Cadena de texto tipo "YYYY-MM-DD"
    private double precioBase;

    public Tarifa(String temporada, String fechaVigencia, double precioBase) {
        this.temporada = temporada;
        this.fechaVigencia = fechaVigencia;
        this.precioBase = precioBase;
    }
    public boolean esVigente(String fecha) {
        return (fecha != null && fecha.equals(fechaVigencia));
    }
}


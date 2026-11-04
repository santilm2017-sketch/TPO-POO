package Dominio;

public class Vehiculo implements java.io.Serializable {
    private String patente;
    private int capacidad;
    private String tipo; // Micro, Combi, Camioneta

    public Vehiculo(String patente, int capacidad, String tipo) {
        if (patente == null || patente.isBlank() || capacidad <= 0) {
            throw new IllegalArgumentException("Patente y capacidad válida son obligatorias.");
        }
        this.patente = patente;
        this.capacidad = capacidad;
        this.tipo = tipo;
    }

    public boolean verificarCapacidad(int cupoRequerido) {
        return capacidad >= cupoRequerido;
    }

    public String getPatente() {
        return patente;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getTipo() {
        return tipo;
    }
}

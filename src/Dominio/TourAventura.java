package Dominio;

import Dominio.Excepciones.RequisitoPasajeroException;

public class TourAventura extends Tour {
    private int edadMinima;
    private String nivelDificultad;

    public TourAventura(String codigo, String nombre, String descripcion,
                        int duracionDias, int cupoMinimo, int cupoMaximo,
                        int edadMinima, String nivelDificultad) {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
        if (edadMinima < 0) throw new IllegalArgumentException("Edad mínima inválida.");
        this.edadMinima = edadMinima;
        if (nivelDificultad == null || nivelDificultad.isBlank()) throw new IllegalArgumentException("Falta dificultad.");
        this.nivelDificultad = nivelDificultad;
    }

    @Override
    public boolean validarRequisitosPasajero(Pasajero pasajero, String fechaSalida) throws RequisitoPasajeroException {
        if (pasajero == null || pasajero.edadEn(fechaSalida) < edadMinima) {
            throw new RequisitoPasajeroException("No cumple la edad mínima al inicio del tour.");
        }
        for (ContratacionServicio servicio : pasajero.getContrataciones()) {
            if ("seguro de viaje".equals(servicio.getServicio().getNombre().toLowerCase())) return true;
        }
        throw new RequisitoPasajeroException("Cada pasajero de aventura necesita seguro de viaje.");
    }

    public String getCategoria() { return "aventura"; }

    public int getEdadMinima() {
        return edadMinima;
    }

    public void setEdadMinima(int edadMinima) {
        if (edadMinima < 0) throw new IllegalArgumentException("Edad mínima inválida.");
        this.edadMinima = edadMinima;
    }

    public String getNivelDificultad() {
        return nivelDificultad;
    }

    public void setNivelDificultad(String nivelDificultad) {
        if (nivelDificultad == null || nivelDificultad.isBlank()) throw new IllegalArgumentException("Falta dificultad.");
        this.nivelDificultad = nivelDificultad;
    }
}
package Dominio;

import Dominio.Excepciones.RequisitoPasajeroException;

public class TourAventura extends Tour {
    private int edadMinima;
    private String nivelDificultad;

    public TourAventura(String codigo, String nombre, String descripcion,
                        int duracionDias, int cupoMinimo, int cupoMaximo,
                        int edadMinima, String nivelDificultad) {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
        this.edadMinima = edadMinima;
        this.nivelDificultad = nivelDificultad;
    }

    @Override
    public boolean validarRequisitosPasajero(Pasajero pasajero) throws RequisitoPasajeroException {
        if (pasajero == null) {
            return false;
        }
        return true;
    }

    public int getEdadMinima() {
        return edadMinima;
    }

    public void setEdadMinima(int edadMinima) {
        this.edadMinima = edadMinima;
    }

    public String getNivelDificultad() {
        return nivelDificultad;
    }

    public void setNivelDificultad(String nivelDificultad) {
        this.nivelDificultad = nivelDificultad;
    }
}
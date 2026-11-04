package Dominio;

import Dominio.Excepciones.RequisitoPasajeroException;

public class TourNaturaleza extends Tour{

    public TourNaturaleza(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo, int cupoMaximo) {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
    }

    public String getCategoria() { return "naturaleza"; }

    @Override
    public boolean validarRequisitosPasajero(Pasajero pasajero, String fechaSalida) throws RequisitoPasajeroException {
        return pasajero != null;
    }
}

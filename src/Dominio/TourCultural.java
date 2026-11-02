package Dominio;

import Dominio.Excepciones.RequisitoPasajeroException;

public class TourCultural extends Tour{
    public TourCultural(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo, int cupoMaximo) {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
    }

    @Override
    public boolean validarRequisitosPasajero(Pasajero pasajero) throws RequisitoPasajeroException {
        if (pasajero == null) {
            return false;
        }
        return true;
    }
}

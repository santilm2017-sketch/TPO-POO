package Dominio;

import Dominio.Excepciones.RequisitoPasajeroException;


import java.util.ArrayList;
import java.util.List;

public abstract class Tour {
    protected String codigo;
    protected String nombre;
    protected String descripcion;
    protected int duracionDias;
    protected int cupoMinimo;
    protected int cupoMaximo;
    protected List<Actividad> itinerario;
    protected List<Tarifa> tarifas;

    public Tour(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo, int cupoMaximo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionDias = duracionDias;
        this.cupoMinimo = cupoMinimo;
        this.cupoMaximo = cupoMaximo;
        this.itinerario = new ArrayList<>();
        this.tarifas = new ArrayList<>();
    }

    public abstract void validarRequisitosPasajero(Pasajero pasajero) throws RequisitoPasajeroException;

    public Tarifa getTarifaVigente(String fecha) {
        for (Tarifa t : tarifas) {
            if (t.esVigente(fecha)) return t;
        }
        return !tarifas.isEmpty() ? tarifas.get(0) : new Tarifa("Baja", "2026-01-01", 1000.0);
    }

    public void agregarActividad(Actividad actividad) {
        itinerario.add(actividad);
    }

    public void agregarTarifa(Tarifa tarifa) {
        tarifas.add(tarifa);
    }
}

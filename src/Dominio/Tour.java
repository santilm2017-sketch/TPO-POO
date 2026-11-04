 package Dominio;

import Dominio.Excepciones.RequisitoPasajeroException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Tour implements java.io.Serializable {
    protected String codigo;
    protected String nombre;
    protected String descripcion;
    protected int duracionDias;
    protected int cupoMinimo;
    protected int cupoMaximo;
    protected List<Actividad> itinerario;
    protected List<Tarifa> tarifas;

    public Tour(String codigo, String nombre, String descripcion,
                int duracionDias, int cupoMinimo, int cupoMaximo) {

        if (duracionDias < 1 || cupoMinimo < 1 || cupoMaximo < cupoMinimo) {
            throw new IllegalArgumentException("La duración o los cupos no son validos.");
        }

        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Código y nombre de tour obligatorios.");
        }
        this.codigo = codigo.trim();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionDias = duracionDias;
        this.cupoMinimo = cupoMinimo;
        this.cupoMaximo = cupoMaximo;
        this.itinerario = new ArrayList<>();
        this.tarifas = new ArrayList<>();
    }

    public abstract boolean validarRequisitosPasajero(Pasajero pasajero, String fechaSalida)
            throws RequisitoPasajeroException;

    public abstract String getCategoria();

    public Tarifa getTarifaVigente(String fecha, String temporada) {
        Tarifa elegida = null;

        for (Tarifa tarifa : tarifas) {
            if (tarifa.getTemporada().toLowerCase().equals(temporada.toLowerCase())
                    && tarifa.esVigente(fecha)) {

                if (elegida == null ||
                        tarifa.getFechaVigencia().compareTo(elegida.getFechaVigencia()) > 0) {
                    elegida = tarifa;
                }
            }
        }

        if (elegida == null) {
            throw new RuntimeException("No hay tarifa vigente para esa fecha y temporada.");
        }

        return elegida;
    }

    public Tarifa getTarifaVigente(String fecha) {
        Tarifa elegida = null;

        for (Tarifa tarifa : tarifas) {
            if (tarifa.esVigente(fecha)) {

                if (elegida != null &&
                        !elegida.getTemporada().toLowerCase().equals(tarifa.getTemporada().toLowerCase())) {
                    throw new RuntimeException("Hay varias temporadas: indique la temporada.");
                }

                if (elegida == null ||
                        tarifa.getFechaVigencia().compareTo(elegida.getFechaVigencia()) > 0) {
                    elegida = tarifa;
                }
            }
        }

        if (elegida == null) {
            throw new RuntimeException("No hay tarifas vigentes para esa fecha.");
        }

        return elegida;
    }

    public void agregarActividad(Actividad actividad) {
        if (actividad == null || actividad.getDia() > duracionDias) {
            throw new IllegalArgumentException("La actividad tiene un dia no valido para el tour.");
        }

        for (Actividad existente : itinerario) {
            if (existente.getDia() == actividad.getDia() &&
                    existente.getOrden() == actividad.getOrden()) {
                throw new IllegalArgumentException("Ya existe una actividad en ese dia y orden.");
            }
        }

        itinerario.add(actividad);
        Collections.sort(itinerario);
    }

    public void agregarTarifa(Tarifa tarifa) {
        if (tarifa == null) {
            throw new IllegalArgumentException("No se puede agregar una tarifa nula.");
        }

        for (Tarifa anterior : tarifas) {
            if (anterior.getFechaVigencia().equals(tarifa.getFechaVigencia())
                    && anterior.getTemporada().toLowerCase().equals(tarifa.getTemporada().toLowerCase())) {
                throw new IllegalArgumentException("Ya hay tarifa para esa temporada y vigencia.");
            }
        }
        tarifas.add(tarifa);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public int getCupoMinimo() {
        return cupoMinimo;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public List<Actividad> getItinerario() {
        return new ArrayList<>(itinerario);
    }

    public List<Tarifa> getTarifas() {
        return new ArrayList<>(tarifas);
    }
}
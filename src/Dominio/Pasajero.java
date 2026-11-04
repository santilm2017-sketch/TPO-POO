package Dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pasajero implements Serializable {
    private String documento;
    private String nombre;
    private String fechaNacimiento;
    private String tipo;
    private String contactoEmergencia;
    private String restricciones;
    private List<ContratacionServicio> contrataciones;

    public Pasajero(String documento, String nombre, String fechaNacimiento, String tipo, String contactoEmergencia, String restricciones) {
        if (documento == null || documento.isBlank() || nombre == null || nombre.isBlank()
                || tipo == null || tipo.isBlank() || contactoEmergencia == null || contactoEmergencia.isBlank()) {
            throw new IllegalArgumentException("Documento, nombre y contacto de emergencia obligatorios.");
        }
        new Fecha(fechaNacimiento);
        this.documento = documento.trim();
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.tipo = tipo;
        this.contactoEmergencia = contactoEmergencia;
        this.restricciones = restricciones;
        this.contrataciones = new ArrayList<>();
    }

    public int edadEn(String fecha) {
        return new Fecha(fechaNacimiento).edadEn(new Fecha(fecha));
    }

    public void contratarServicio(ServicioAdicional servicio, String fecha) {
        agregarContratacion(new ContratacionServicio(this, servicio, fecha));
    }

    public void agregarContratacion(ContratacionServicio cs) {
        if (cs != null && cs.getPasajero() == this) {
            this.contrataciones.add(cs);
        }
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getTipo() {
        return tipo;
    }

    public String getContactoEmergencia() {
        return contactoEmergencia;
    }

    public String getRestricciones() {
        return restricciones;
    }

    public List<ContratacionServicio> getContrataciones() {
        return new ArrayList<>(contrataciones);
    }
}
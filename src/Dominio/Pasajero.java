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
        this.documento = documento;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.tipo = tipo;
        this.contactoEmergencia = contactoEmergencia;
        this.restricciones = restricciones;
        this.contrataciones = new ArrayList<>();
    }

    public void agregarContratacion(ContratacionServicio cs) {
        if (cs != null) {
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
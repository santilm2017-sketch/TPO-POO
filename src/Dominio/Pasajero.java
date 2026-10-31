
package Dominio;

import java.util.ArrayList;
import java.util.List;

public class Pasajero {
    private String nombre;
    private int edad;
    private int documento;
    private String fechaNacimiento;
    private String tipo;
    private String contactoEmergencia;
    private String restricciones;
    private List<ContratacionServicio> contrataciones;

    public Pasajero(String nombre, int edad, int documento, String fechaNacimiento,
                    String tipo, String contactoEmergencia, String restricciones) {

        if (edad < 0 || documento <= 0) {
            throw new IllegalArgumentException("La edad o el documento no son validos.");
        }

        if (fechaNacimiento == null || fechaNacimiento.length() != 10) {
            throw new IllegalArgumentException("La fecha debe tener formato AAAA-MM-DD.");
        }

        this.nombre = nombre;
        this.edad = edad;
        this.documento = documento;
        this.fechaNacimiento = fechaNacimiento;
        this.tipo = tipo;
        this.contactoEmergencia = contactoEmergencia;
        this.restricciones = restricciones;
        this.contrataciones = new ArrayList<>();
    }

    public void contratarServicio(ServicioAdicional servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo.");
        }

        ContratacionServicio contratacion = new ContratacionServicio(this, servicio);
        contrataciones.add(contratacion);
    }

    public int getEdadEnFecha(String fecha) {
        if (fecha == null || fecha.length() != 10) {
            throw new IllegalArgumentException("La fecha debe tener formato AAAA-MM-DD.");
        }

        int anioNac = Integer.parseInt(fechaNacimiento.substring(0, 4));
        int mesNac = Integer.parseInt(fechaNacimiento.substring(5, 7));
        int diaNac = Integer.parseInt(fechaNacimiento.substring(8, 10));

        int anio = Integer.parseInt(fecha.substring(0, 4));
        int mes = Integer.parseInt(fecha.substring(5, 7));
        int dia = Integer.parseInt(fecha.substring(8, 10));

        int edadEnFecha = anio - anioNac;

        if (mes < mesNac || (mes == mesNac && dia < diaNac)) {
            edadEnFecha--;
        }

        if (edadEnFecha < 0) {
            throw new IllegalArgumentException("La fecha es anterior al nacimiento.");
        }

        return edadEnFecha;
    }

    public List<ContratacionServicio> getContrataciones() {
        return new ArrayList<>(contrataciones);
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public int getDocumento() {
        return documento;
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
}

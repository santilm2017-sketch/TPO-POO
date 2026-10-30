package Dominio;

public class Pasajero {
    private String nombre;
    private int edad;
    private int documento;
    private String fechaNacimiento;
    private String tipo;
    private String contactoEmergencia;
    private String restricciones;

    public Pasajero(String nombre, int edad, int documento, String fechaNacimiento, String tipo, String contactoEmergencia, String restricciones) {
        this.nombre = nombre;
        this.edad = edad;
        this.documento = documento;
        this.fechaNacimiento = fechaNacimiento;
        this.tipo = tipo;
        this.contactoEmergencia = contactoEmergencia;
        this.restricciones = restricciones;
    }

    public void contratarServicio(ServicioAdicional servicio){

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

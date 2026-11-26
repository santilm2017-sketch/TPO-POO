package Dominio;

import java.io.Serializable;

public class Cliente implements Serializable {
    private String paisResidencia;
    private String documento;
    private String nombre;
    private String email;
    private String telefono;

    public Cliente(String documento, String nombre, String email, String telefono) {
        this.documento = Validaciones.documento(documento);
        this.nombre = Validaciones.nombrePersona(nombre);
        this.email = Validaciones.email(email);
        this.telefono = Validaciones.telefono(telefono);
    }

    public Cliente(String documento, String nombre, String email, String telefono, String paisResidencia) {
        this(documento, nombre, email, telefono);
        this.paisResidencia = obligatorio(paisResidencia, "País de residencia");
    }

    private static String obligatorio(String valor, String dato) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(dato + " obligatorio.");
        }
        return valor.trim();
    }

    public String getPaisResidencia() { return paisResidencia; }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setDocumento(String documento) {
        this.documento = Validaciones.documento(documento);
    }

    public void setNombre(String nombre) {
        this.nombre = Validaciones.nombrePersona(nombre);
    }

    public void setEmail(String email) {
        this.email = Validaciones.email(email);
    }

    public void setTelefono(String telefono) {
        this.telefono = Validaciones.telefono(telefono);
    }
}

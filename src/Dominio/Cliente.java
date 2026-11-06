package Dominio;

import java.io.Serializable;

public class Cliente implements Serializable {
    private String paisResidencia;
    private String documento;
    private String nombre;
    private String email;
    private String telefono;

    public Cliente(String documento, String nombre, String email, String telefono) {
        this.documento = obligatorio(documento, "Documento");
        this.nombre = obligatorio(nombre, "Nombre");
        this.email = obligatorio(email, "Email");
        this.telefono = obligatorio(telefono, "Teléfono");
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
        this.documento = obligatorio(documento, "Documento");
    }

    public void setNombre(String nombre) {
        this.nombre = obligatorio(nombre, "Nombre");
    }

    public void setEmail(String email) {
        this.email = obligatorio(email, "Email");
    }

    public void setTelefono(String telefono) {
        this.telefono = obligatorio(telefono, "Teléfono");
    }
}

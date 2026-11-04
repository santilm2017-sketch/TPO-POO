package Dominio;

import java.io.Serializable;

public class Cliente implements Serializable {
    private String paisResidencia;
    private String documento;
    private String nombre;
    private String email;
    private String telefono;

    public Cliente(String documento, String nombre, String email, String telefono) {
        if (documento == null || documento.isBlank()) throw new IllegalArgumentException("Documento obligatorio.");
        this.documento = documento.trim();
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    public Cliente(String documento, String nombre, String email, String telefono, String paisResidencia) {
        this(documento, nombre, email, telefono);
        this.paisResidencia = paisResidencia;
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
        if (documento == null || documento.isBlank()) throw new IllegalArgumentException("Documento obligatorio.");
        this.documento = documento.trim();
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
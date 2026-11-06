package Dominio;

import java.io.Serializable;

public abstract class Personal implements Serializable {
    protected String documento;
    protected String nombre;
    protected String email;
    protected String telefono;

    public Personal(String documento, String nombre, String email, String telefono) {
        this.documento = obligatorio(documento, "Documento");
        this.nombre = obligatorio(nombre, "Nombre");
        this.email = email;
        this.telefono = obligatorio(telefono, "Teléfono");
    }

    private static String obligatorio(String valor, String dato) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(dato + " obligatorio.");
        }
        return valor.trim();
    }

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
}

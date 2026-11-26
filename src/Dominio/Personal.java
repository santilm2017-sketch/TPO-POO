package Dominio;

import java.io.Serializable;

public abstract class Personal implements Serializable {
    protected String documento;
    protected String nombre;
    protected String email;
    protected String telefono;

    public Personal(String documento, String nombre, String email, String telefono) {
        this.documento = Validaciones.identificadorPersonal(documento);
        this.nombre = Validaciones.nombrePersona(nombre);
        this.email = email;
        this.telefono = Validaciones.telefono(telefono);
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

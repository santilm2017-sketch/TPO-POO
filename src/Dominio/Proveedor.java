package Dominio;

public class Proveedor implements java.io.Serializable {
    private String nombre;
    private String cuit;
    private String contacto;
    private String servicioQuePresta;

    public Proveedor(String nombre, String cuit, String contacto, String servicioQuePresta) {
        if (nombre == null || cuit == null) {
            throw new IllegalArgumentException("El nombre y el CUIT son obligatorios.");
        }
        this.nombre = nombre;
        this.cuit = cuit;
        this.contacto = contacto;
        this.servicioQuePresta = servicioQuePresta;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCuit() {
        return cuit;
    }

    public String getContacto() {
        return contacto;
    }

    public String getServicioQuePresta() {
        return servicioQuePresta;
    }
}

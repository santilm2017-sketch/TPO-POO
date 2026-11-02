package Dominio;

public class ServicioAdicional implements java.io.Serializable {
    private String nombre;
    private double precio;
    private boolean esObligatorio;

    public ServicioAdicional(String nombre, double precio, boolean esObligatorio) {
        if (nombre == null || precio < 0) {
            throw new IllegalArgumentException("Nombre y precio válido son obligatorios.");
        }
        this.nombre = nombre;
        this.precio = precio;
        this.esObligatorio = esObligatorio;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public boolean isEsObligatorio() {
        return esObligatorio;
    }
}
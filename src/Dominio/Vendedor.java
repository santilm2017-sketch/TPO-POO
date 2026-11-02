package Dominio;

public class Vendedor extends Personal {
    private String legajo;

    public Vendedor(String documento, String nombre, String email, String telefono, String legajo) {
        super(documento, nombre, email, telefono);
        this.legajo = legajo;
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        this.legajo = legajo;
    }
}
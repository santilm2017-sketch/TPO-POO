package Dominio;

public class Vendedor extends Personal {
    private double comision;
    private String legajo;

    public Vendedor(String documento, String nombre, String email, String telefono, String legajo, double comision) {
        this(documento, nombre, email, telefono, legajo);
        if (!Importes.esValido(comision)) throw new IllegalArgumentException("Comisión inválida.");
        this.comision = comision;
    }

    public double getComision() { return comision; }

    public Vendedor(String documento, String nombre, String email, String telefono, String legajo) {
        super(documento, nombre, email, telefono);
        setLegajo(legajo);
    }

    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        if (legajo == null || legajo.isBlank()) throw new IllegalArgumentException("Legajo obligatorio.");
        this.legajo = legajo.trim();
    }
}

package Dominio;

public class Actividad {
    private int dia;
    private int orden;
    private String horario;
    private String descripcion;
    private String lugar;
    private Proveedor proveedor;

    public Actividad(int dia, int orden, String horario, String descripcion, String lugar, Proveedor proveedor) {
        this.dia = dia;
        this.orden = orden;
        this.horario = horario;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.proveedor = proveedor;
    }

    public void asignarProveedor(Proveedor asignado){
        this.proveedor = asignado;
    }

    public String getDescripcion() {
        return descripcion;
    }
}

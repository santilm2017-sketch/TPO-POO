
package Dominio;

public class Actividad implements Comparable<Actividad>, java.io.Serializable {
    private int dia;
    private int orden;
    private String horario;
    private String descripcion;
    private String lugar;
    private Proveedor proveedor;

    public Actividad(int dia, int orden, String horario, String descripcion,
                     String lugar, Proveedor proveedor) {

        if (dia < 1 || orden < 1) {
            throw new IllegalArgumentException("El dia y el orden deben ser positivos.");
        }

        this.dia = dia;
        this.orden = orden;
        this.horario = horario;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.proveedor = proveedor;
    }

    public void asignarProveedor(Proveedor asignado) {
        this.proveedor = asignado;
    }

    @Override
    public int compareTo(Actividad otra) {
        int resultado = Integer.compare(this.dia, otra.dia);

        if (resultado == 0) {
            return Integer.compare(this.orden, otra.orden);
        }

        return resultado;
    }

    public int getDia() {
        return dia;
    }

    public int getOrden() {
        return orden;
    }

    public String getHorario() {
        return horario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getLugar() {
        return lugar;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }
}

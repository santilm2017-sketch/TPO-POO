package Dominio;

import java.util.List;
import java.util.ArrayList;

public class CancelacionReserva implements java.io.Serializable {
    private String fecha;
    private String motivo;
    private double importeDevuelto;

    public CancelacionReserva(String fecha, String motivo, double importeDevuelto) {
        if (importeDevuelto < 0) {
            throw new IllegalArgumentException("El importe devuelto no puede ser negativo.");
        }
        this.fecha = fecha;
        this.motivo = motivo;
        this.importeDevuelto = importeDevuelto;
    }

    private List<String> devoluciones = new ArrayList<>();

    void devolver(Pago pago, double importe) {
        importeDevuelto = Importes.redondear(importeDevuelto + importe);
        devoluciones.add(pago.getMedioPago().getNombre() + " " + importe);
    }

    public List<String> getDevoluciones() {
        List<String> resultado = new ArrayList<>();
        for (String devolucion : devoluciones) {
            int espacio = devolucion.indexOf(" ");
            String medio = devolucion.substring(0, espacio);
            double monto = Double.parseDouble(devolucion.substring(espacio + 1, devolucion.length()));
            resultado.add(medio + " $ " + Importes.mostrar(monto));
        }
        return resultado;
    }

    public String getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public double getImporteDevuelto() {
        return importeDevuelto;
    }
}

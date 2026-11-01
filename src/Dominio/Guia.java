package Dominio;

public class Guia extends Personal {
    private String matricula;
    private double tarifaDiaria;

    public Guia(String documento, String nombre, String email, String telefono, String matricula, double tarifaDiaria) {
        super(documento, nombre, email, telefono);
        this.matricula = matricula;
        this.tarifaDiaria = tarifaDiaria;
    }

    public String getMatricula() {
        return matricula;
    }

    public double getTarifaDiaria() {
        return tarifaDiaria;
    }

    /**
     * Calcula los honorarios del guía considerando la duración de la salida
     * y las comisiones sobre adicionales (según diseño GRASP del proyecto)
     */
    public double calcularHonorarios(Salida salida) {
        if (salida == null || salida.getTour() == null) {
            return 0.0;
        }
        int dias = salida.getTour().getDuracionDias();
        double honorarioBase = dias * tarifaDiaria;
        double adicionalesVendidos = salida.calcularTotalAdicionalesVendidos();

        // Honorario base + 5% sobre servicios adicionales vendidos en la salida
        return honorarioBase + (adicionalesVendidos * 0.05);
    }
}
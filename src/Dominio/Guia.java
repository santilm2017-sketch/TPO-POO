package Dominio;

import java.util.List;
import java.util.ArrayList;

public class Guia extends Personal {
    private List<String> categorias = new ArrayList<>();
    private List<String> idiomas = new ArrayList<>();
    private String matricula;
    private double tarifaDiaria;

    public Guia(String documento, String nombre, String email, String telefono, String matricula, double tarifaDiaria) {
        super(documento, nombre, email, telefono);
        this.matricula = matricula;
        if (!Importes.esValido(tarifaDiaria)) throw new IllegalArgumentException("Honorario inválido.");
        this.tarifaDiaria = tarifaDiaria;
    }

    public void habilitarCategoria(String categoria) {
        String valor = categoria.trim().toLowerCase();
        if (!valor.equals("aventura") && !valor.equals("naturaleza") && !valor.equals("cultural")) {
            throw new IllegalArgumentException("Categoría desconocida.");
        }
        if (!categorias.contains(valor)) categorias.add(valor);
    }

    public void agregarIdioma(String idioma) {
        if (idioma.isBlank()) throw new IllegalArgumentException("Idioma vacío.");
        idiomas.add(idioma.trim());
    }

    public boolean estaHabilitado(Tour tour) {
        return categorias.contains(tour.getCategoria());
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
        if (salida == null || !"FINALIZADA".equals(salida.getEstado()) || salida.getGuia() != this) {
            throw new RuntimeException("Solo se liquida al guía asignado a una salida finalizada.");
        }
        int dias = salida.getTour().getDuracionDias();
        double honorarioBase = dias * tarifaDiaria;
        double adicionalesVendidos = salida.calcularTotalAdicionalesVendidos();

        // Honorario base + 5% sobre servicios adicionales vendidos en la salida
        return honorarioBase + (adicionalesVendidos * 0.05);
    }
}
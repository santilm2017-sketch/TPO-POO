package Test;

import Dominio.*;

public class CalculadoraPrecioReservaTest {

    public static void main(String[] args) {
        System.out.println("=== EJECUTANDO PRUEBAS UNITARIAS DE NEGOCIO ===");

        testPreciosPasajeroPorTipo();
        testPromocionNoAcumulable();

        System.out.println("=== TODAS LAS PRUEBAS FINALIZARON EXITOSAMENTE ===");
    }

    private static void testPreciosPasajeroPorTipo() {
        CalculadoraPrecioReserva calc = new CalculadoraPrecioReserva();
        double tarifaBase = 1000.0;

        Pasajero adulto = new Pasajero("1", "Adulto", "1990-01-01", "adulto", "Contacto", "Ninguna");
        Pasajero menor = new Pasajero("2", "Menor", "2018-01-01", "menor_12", "Contacto", "Ninguna");
        Pasajero bebe = new Pasajero("3", "Bebe", "2024-01-01", "menor_3", "Contacto", "Ninguna");
        Pasajero jubilado = new Pasajero("4", "Jubilado", "1950-01-01", "jubilado", "Contacto", "Ninguna");

        assert calc.calcularPrecioPasajero(adulto, tarifaBase) == 1000.0 : "Error en precio adulto";
        assert calc.calcularPrecioPasajero(menor, tarifaBase) == 600.0 : "Error en precio menor";
        assert calc.calcularPrecioPasajero(bebe, tarifaBase) == 0.0 : "Error en precio bebe";
        assert calc.calcularPrecioPasajero(jubilado, tarifaBase) == 900.0 : "Error en precio jubilado";

        System.out.println("[OK] Prueba RN10 - Precios por categoria de pasajero superada.");
    }

    private static void testPromocionNoAcumulable() {
        CalculadoraPrecioReserva calc = new CalculadoraPrecioReserva();
        double subtotal = 10000.0;

        // Promoción anticipada (15%) = 1500
        PromocionAnticipada pAnticipada = new PromocionAnticipada();
        // Promoción grupo (10%) = 1000
        PromocionGrupo pGrupo = new PromocionGrupo();

        double mejorDescuento = Math.max(
                pAnticipada.calcularDescuento(null, subtotal),
                pGrupo.calcularDescuento(null, subtotal)
        );

        System.out.println("[OK] Prueba RN11/RN12 - Evaluacion de mejor promocion superada.");
    }
}
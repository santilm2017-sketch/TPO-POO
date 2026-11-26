package Test;

import Dominio.PersonalAdministrativo;
import Dominio.Cliente;
import Dominio.Guia;
import Dominio.Vendedor;
import Dominio.Pasajero;
import Dominio.Validaciones;

public class PersonalAdministrativoTest {
    public static void main(String[] args) {
        PersonalAdministrativo coordinador = new PersonalAdministrativo(
                "123", "Ana", "ana@example.com", "111", "C001", "coordinador");
        verificar(coordinador.getLegajo().equals("C001"), "Legajo del coordinador");
        verificar(coordinador.getTelefono().equals("111"), "Teléfono del coordinador");
        verificar(coordinador.getRol().equals("coordinador"), "Rol coordinador");

        PersonalAdministrativo admin = new PersonalAdministrativo(
                "456", "Luis", "luis@example.com", "222", "A001", "ADMINISTRADOR");
        verificar(admin.getRol().equals("administrador"), "Rol administrador");
        verificar(admin.getDocumento().equals("456"), "Documento del administrador");

        boolean rechazado = false;
        try { new PersonalAdministrativo("789", "Eva", "", "333", "X1", "guia"); }
        catch (IllegalArgumentException e) { rechazado = true; }
        verificar(rechazado, "Rol incorrecto rechazado");

        String[][] datos = {
                {"", "Ana", "111", "C1"},
                {"123", " ", "111", "C1"},
                {"123", "Ana", "", "C1"},
                {"123", "Ana", "111", null}
        };
        for (int i = 0; i < datos.length; i++) {
            rechazado = false;
            try {
                new PersonalAdministrativo(datos[i][0], datos[i][1], "", datos[i][2], datos[i][3], "coordinador");
            } catch (IllegalArgumentException e) {
                rechazado = true;
            }
            verificar(rechazado, "Dato obligatorio rechazado: " + i);
        }

        probarDatosObligatorios();
        probarNombres();
        System.out.println("=== PRUEBAS DE PERSONAL COMPLETADAS ===");
    }

    private static void probarDatosObligatorios() {
        Cliente cliente = new Cliente("123", "Ana", "ana@example.com", "111", "Argentina");
        Vendedor vendedor = new Vendedor("456", "Luis", "", "222", "V1");
        for (String vacio : new String[]{null, "", "   "}) {
            Runnable[] operaciones = {
                    () -> new Cliente(vacio, "Ana", "ana@example.com", "111"),
                    () -> new Cliente("123", vacio, "ana@example.com", "111"),
                    () -> new Cliente("123", "Ana", vacio, "111"),
                    () -> new Cliente("123", "Ana", "ana@example.com", vacio),
                    () -> new Cliente("123", "Ana", "ana@example.com", "111", vacio),
                    () -> new Guia(vacio, "Julia", "", "111", "M1", 500),
                    () -> new Guia("G1", vacio, "", "111", "M1", 500),
                    () -> new Guia("G1", "Julia", "", vacio, "M1", 500),
                    () -> new Guia("G1", "Julia", "", "111", vacio, 500),
                    () -> new Vendedor("456", "Luis", "", "222", vacio),
                    () -> cliente.setDocumento(vacio),
                    () -> cliente.setNombre(vacio),
                    () -> cliente.setEmail(vacio),
                    () -> cliente.setTelefono(vacio),
                    () -> vendedor.setLegajo(vacio)
            };
            for (int i = 0; i < operaciones.length; i++) {
                boolean rechazado = false;
                try { operaciones[i].run(); }
                catch (IllegalArgumentException e) { rechazado = true; }
                verificar(rechazado, "Campo obligatorio nulo o vacío rechazado: " + i);
            }
        }
        verificar(cliente.getDocumento().equals("123") && cliente.getNombre().equals("Ana")
                && cliente.getEmail().equals("ana@example.com") && cliente.getTelefono().equals("111")
                && vendedor.getLegajo().equals("V1"), "Cambios inválidos conservan los datos anteriores");
        verificar(new Guia("G1", "Julia", "", "111", "M1", 500).getEmail().equals("")
                && vendedor.getEmail().equals(""), "Email del personal sigue siendo opcional");
    }

    private static void probarNombres() {
        Cliente cliente = new Cliente("123", "Ana", "ana@example.com", "111");
        for (String nombre : new String[]{"María José", "Juan Pérez", "Ana-María", "Iñaki Muñoz", "  José Núñez  "}) {
            String esperado = nombre.trim();
            verificar(Validaciones.nombrePersona(nombre).equals(esperado), "Nombre válido: " + nombre);
            verificar(new Cliente("123", nombre, "ana@example.com", "111").getNombre().equals(esperado), "Nombre del cliente");
            verificar(new Pasajero("123", nombre, "1990-01-01", "adulto", "111", "").getNombre().equals(esperado), "Nombre del pasajero");
            verificar(new Guia("G1", nombre, "", "111", "M1", 500).getNombre().equals(esperado), "Nombre del guía por herencia");
            verificar(new Vendedor("V1", nombre, "", "111", "V1").getNombre().equals(esperado), "Nombre del vendedor por herencia");
            verificar(new PersonalAdministrativo("123", nombre, "", "111", "C1", "coordinador").getNombre().equals(esperado), "Nombre del coordinador por herencia");
            verificar(new PersonalAdministrativo("123", nombre, "", "111", "A1", "administrador").getNombre().equals(esperado), "Nombre del administrador por herencia");
            cliente.setNombre(nombre);
            verificar(cliente.getNombre().equals(esperado), "Setter acepta nombre válido");
        }
        String nombreAnterior = cliente.getNombre();
        for (String nombre : new String[]{null, "", "   ", "Thomas123", "12345", "Ana1 María", "Juan-2", "3Ana"}) {
            Runnable[] operaciones = {
                    () -> Validaciones.nombrePersona(nombre),
                    () -> new Cliente("123", nombre, "ana@example.com", "111"),
                    () -> new Cliente("123", nombre, "ana@example.com", "111", "Argentina"),
                    () -> new Pasajero("123", nombre, "1990-01-01", "adulto", "111", ""),
                    () -> new Guia("G1", nombre, "", "111", "M1", 500),
                    () -> new Vendedor("V1", nombre, "", "111", "V1"),
                    () -> new Vendedor("V1", nombre, "", "111", "V1", 5),
                    () -> new PersonalAdministrativo("123", nombre, "", "111", "C1", "coordinador"),
                    () -> new PersonalAdministrativo("123", nombre, "", "111", "A1", "administrador"),
                    () -> cliente.setNombre(nombre)
            };
            for (int i = 0; i < operaciones.length; i++) {
                boolean rechazado = false;
                try { operaciones[i].run(); }
                catch (IllegalArgumentException e) {
                    rechazado = e.getMessage() != null && !e.getMessage().isEmpty();
                }
                verificar(rechazado, "Nombre inválido rechazado en operación " + i + ": " + nombre);
            }
            verificar(cliente.getNombre().equals(nombreAnterior), "Setter inválido conserva el nombre anterior");
        }
    }

    private static void verificar(boolean correcto, String caso) {
        if (!correcto) throw new RuntimeException("Fallo: " + caso);
        System.out.println("[OK] " + caso);
    }
}

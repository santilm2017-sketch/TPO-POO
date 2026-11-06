package Test;

import Dominio.PersonalAdministrativo;
import Dominio.Cliente;
import Dominio.Guia;
import Dominio.Vendedor;

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

    private static void verificar(boolean correcto, String caso) {
        if (!correcto) throw new RuntimeException("Fallo: " + caso);
        System.out.println("[OK] " + caso);
    }
}

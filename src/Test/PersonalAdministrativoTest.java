package Test;

import Dominio.PersonalAdministrativo;

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

        System.out.println("=== PRUEBAS DE PERSONAL COMPLETADAS ===");
    }

    private static void verificar(boolean correcto, String caso) {
        if (!correcto) throw new RuntimeException("Fallo: " + caso);
        System.out.println("[OK] " + caso);
    }
}

package Test;

import Dominio.PersonalAdministrativo;
import Persistencia.RepositorioPersonal;

// Ejecutar en una carpeta de trabajo nueva: esta prueba escribe archivos.
public class RepositorioPersonalTest {
    public static void main(String[] args) {
        RepositorioPersonal repo = new RepositorioPersonal();
        if (!repo.obtenerTodos().isEmpty()) throw new RuntimeException("Use una carpeta nueva sin personal guardado.");
        repo.guardar(new PersonalAdministrativo("123", "Ana", "", "111", "C1", "coordinador"));
        repo.guardar(new PersonalAdministrativo("456", "Luis", "", "222", "A1", "administrador"));
        verificar(new RepositorioPersonal().obtenerTodos().size() == 2, "Recupera ambos roles");
        verificar(repo.buscarPorId(" c1 ").getDocumento().equals("123"), "Busca legajo sin distinguir mayusculas");
        boolean rechazado = false;
        try { repo.guardar(new PersonalAdministrativo("789", "Eva", "", "333", "c1", "administrador")); }
        catch (IllegalArgumentException e) { rechazado = true; }
        verificar(rechazado && repo.obtenerTodos().size() == 2, "Legajo duplicado rechazado");
        rechazado = false;
        try { repo.guardar(new PersonalAdministrativo("123", "Eva", "", "333", "A2", "administrador")); }
        catch (IllegalArgumentException e) { rechazado = true; }
        verificar(rechazado && repo.obtenerTodos().size() == 2, "Documento duplicado rechazado");
        rechazado = false;
        try { repo.actualizar(new PersonalAdministrativo("456", "Ana", "", "333", "C1", "coordinador")); }
        catch (IllegalArgumentException e) { rechazado = true; }
        verificar(rechazado && new RepositorioPersonal().buscarPorId("C1").getDocumento().equals("123"),
                "Actualizacion duplicada conserva datos");
        repo.actualizar(new PersonalAdministrativo("123", "Ana", "", "999", "C1", "coordinador"));
        verificar(new RepositorioPersonal().buscarPorId("C1").getTelefono().equals("999"), "Recupera actualizacion");
    }

    private static void verificar(boolean correcto, String caso) {
        if (!correcto) throw new RuntimeException("Fallo: " + caso);
        System.out.println("[OK] " + caso);
    }
}

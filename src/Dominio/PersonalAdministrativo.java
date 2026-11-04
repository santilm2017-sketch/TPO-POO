package Dominio;

// Un mismo tipo representa los dos roles internos que no tienen operaciones propias.
public class PersonalAdministrativo extends Personal {
    private String legajo;
    private String rol;

    public PersonalAdministrativo(String documento, String nombre, String email,
                                 String telefono, String legajo, String rol) {
        super(obligatorio(documento, "Documento"), obligatorio(nombre, "Nombre"),
                email, obligatorio(telefono, "Teléfono"));
        this.legajo = obligatorio(legajo, "Legajo");
        String valor = obligatorio(rol, "Rol").toLowerCase();
        if (!valor.equals("coordinador") && !valor.equals("administrador")) {
            throw new IllegalArgumentException("El rol debe ser coordinador o administrador.");
        }
        this.rol = valor;
    }

    private static String obligatorio(String valor, String dato) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(dato + " obligatorio.");
        }
        return valor.trim();
    }

    public String getLegajo() { return legajo; }
    public String getRol() { return rol; }
}

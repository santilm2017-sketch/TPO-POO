package Dominio;

public class Validaciones {
    public static String obligatorio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) throw new IllegalArgumentException(campo + " obligatorio.");
        return valor.trim();
    }
    public static String nombrePersona(String valor) {
        String nombre = obligatorio(valor, "Nombre");
        for (int i = 0; i < nombre.length(); i++) {
            if (nombre.charAt(i) >= '0' && nombre.charAt(i) <= '9')
                throw new IllegalArgumentException("El nombre de la persona no puede contener números.");
        }
        return nombre;
    }
    public static String documento(String valor) {
        String texto = obligatorio(valor, "Documento");
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) < '0' || texto.charAt(i) > '9')
                throw new IllegalArgumentException("El documento personal debe contener solamente números.");
        }
        return texto;
    }
    public static String email(String valor) {
        String texto = obligatorio(valor, "Email");
        int arroba = -1;
        for (int i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) == ' ') throw new IllegalArgumentException("El email no puede contener espacios.");
            if (texto.charAt(i) == '@') {
                if (arroba != -1) throw new IllegalArgumentException("El email debe tener una sola arroba.");
                arroba = i;
            }
        }
        if (arroba < 1 || arroba == texto.length() - 1)
            throw new IllegalArgumentException("Email inválido: indique usuario@dominio.");
        return texto;
    }
    public static String identificadorPersonal(String valor) {
        String texto = obligatorio(valor, "Documento o código interno");
        boolean codigo = false;
        boolean numero = false;
        boolean letra = false;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c >= '0' && c <= '9') numero = true;
            else if (c == '-') codigo = true;
            else if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) letra = true;
            else
                throw new IllegalArgumentException("Documento o código interno inválido.");
        }
        if ((!codigo && !numero) || (!letra && !numero)) throw new IllegalArgumentException("Use un documento numérico o un código interno como G1 o G-SALTA.");
        return texto;
    }
    public static String horario(String valor) {
        String texto = obligatorio(valor, "Horario");
        try {
            if (texto.length() == 5 && texto.charAt(2) == ':') {
                int hora = Integer.parseInt(texto.substring(0, 2));
                int minuto = Integer.parseInt(texto.substring(3, 5));
                if (hora >= 0 && hora <= 23 && minuto >= 0 && minuto <= 59) return texto;
            }
        } catch (NumberFormatException e) { }
        throw new IllegalArgumentException("Horario inválido: use HH:MM, por ejemplo 09:30.");
    }
    public static String telefono(String valor) {
        String texto = obligatorio(valor, "Teléfono");
        int inicio = texto.charAt(0) == '+' ? 1 : 0;
        if (inicio == texto.length()) throw new IllegalArgumentException("El teléfono debe contener números.");
        for (int i = inicio; i < texto.length(); i++) {
            if (texto.charAt(i) < '0' || texto.charAt(i) > '9')
                throw new IllegalArgumentException("Teléfono inválido: use números, con + inicial opcional.");
        }
        return texto;
    }
}

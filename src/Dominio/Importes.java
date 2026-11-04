package Dominio;

public class Importes {
    public static String mostrar(double monto) {
        long centavos = (long) (redondear(monto) * 100 + (monto < 0 ? -0.5 : 0.5));
        String signo = "";
        if (centavos < 0) {
            signo = "-";
            centavos = -centavos;
        }
        long decimales = centavos % 100;
        String ceros = decimales < 10 ? "0" : "";
        return signo + centavos / 100 + "," + ceros + decimales;
    }

    public static double leer(String texto) {
        String numero = "";
        for (int i = 0; i < texto.length(); i++) {
            char caracter = texto.charAt(i);
            if (caracter == ',') numero += ".";
            else numero += caracter;
        }
        return Double.parseDouble(numero);
    }

    public static boolean esValido(double monto) {
        return monto >= 0 && monto <= Double.MAX_VALUE;
    }

    // Se convierte a centavos enteros. Sumar medio centavo antes de truncar
    // elige el centavo más próximo para montos positivos.
    public static double redondear(double monto) {
        double valor = monto * 100;
        if (valor < 0) valor -= 0.5;
        else valor += 0.5;
        long centavos = (long) valor;
        return centavos / 100.0;
    }
}

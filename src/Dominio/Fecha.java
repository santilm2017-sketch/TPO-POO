package Dominio;

// Se guarda año-mes-día para ordenar y leer los archivos existentes.
// La consola usa día/mes/año.
public class Fecha {
    private final String texto;
    private final int dia;
    private final int mes;
    private final int anio;

    public Fecha(String texto) {
        if (texto == null || texto.length() != 10 || texto.charAt(4) != '-' || texto.charAt(7) != '-') {
            throw new IllegalArgumentException("Use AAAA-MM-DD.");
        }
        for (int i = 0; i < texto.length(); i++) {
            if (i != 4 && i != 7 && (texto.charAt(i) < '0' || texto.charAt(i) > '9')) {
                throw new IllegalArgumentException("Fecha inválida.");
            }
        }
        anio = Integer.parseInt(texto.substring(0, 4));
        mes = Integer.parseInt(texto.substring(5, 7));
        dia = Integer.parseInt(texto.substring(8, 10));
        if (anio < 1 || mes < 1 || mes > 12 || dia < 1 || dia > diasDelMes(mes)) {
            throw new IllegalArgumentException("Fecha inexistente.");
        }
        this.texto = texto;
    }

    public static Fecha desdePantalla(String texto) {
        if (texto == null || texto.length() != 10 || texto.charAt(2) != '/' || texto.charAt(5) != '/') {
            throw new IllegalArgumentException("Use DD/MM/AAAA, por ejemplo 03/11/2026.");
        }
        return new Fecha(texto.substring(6, 10) + "-" + texto.substring(3, 5) + "-" + texto.substring(0, 2));
    }

    public static String mostrar(String fecha) {
        return fecha.substring(8, 10) + "/" + fecha.substring(5, 7) + "/" + fecha.substring(0, 4);
    }

    private int diasDelMes(int numeroMes) {
        if (numeroMes == 2) {
            boolean bisiesto = anio % 4 == 0 && (anio % 100 != 0 || anio % 400 == 0);
            return bisiesto ? 29 : 28;
        }
        if (numeroMes == 4 || numeroMes == 6 || numeroMes == 9 || numeroMes == 11) return 30;
        return 31;
    }

    private int numeroDeDia() {
        int completos = anio - 1;
        // Sumar los 29 de febrero de los años ya terminados.
        int dias = completos * 365 + completos / 4 - completos / 100 + completos / 400;
        for (int m = 1; m < mes; m++) dias += diasDelMes(m);
        return dias + dia;
    }

    public int diasHasta(Fecha otra) { return otra.numeroDeDia() - numeroDeDia(); }

    public int edadEn(Fecha otra) {
        int edad = otra.anio - anio;
        if (otra.mes < mes || (otra.mes == mes && otra.dia < dia)) edad--;
        return edad;
    }

    public String toString() { return texto; }
}

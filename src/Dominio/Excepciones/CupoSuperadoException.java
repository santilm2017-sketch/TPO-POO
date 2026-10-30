package Dominio.Excepciones;

public class CupoSuperadoException extends RuntimeException {
    public CupoSuperadoException(String message) {
        super(message);
    }
}

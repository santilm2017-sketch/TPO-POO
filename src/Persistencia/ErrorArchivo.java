package Persistencia;

public class ErrorArchivo extends RuntimeException {
    public ErrorArchivo(String mensaje, Exception causa) { super(mensaje, causa); }
}

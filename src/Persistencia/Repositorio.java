package Persistencia;

import java.util.List;

public interface Repositorio<T> {
    void guardar(T entidad);
    T buscarPorId(String id);
    List<T> obtenerTodos();
    void actualizar(T entidad);
}

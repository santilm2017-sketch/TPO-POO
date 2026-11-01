package Persistencia;

import java.util.List;

public interface IRepository<T> {
    void guardar(T entidad);
    T buscarPorId(String id);
    List<T> obtenerTodos();
    void actualizar(T entidad);
}

package integrado.prog2.interfaces;

import java.util.List;

/** Contrato común para las operaciones de persistencia. */
public interface GenericDAO<T> {

    List<T> listarActivos();

    T buscarPorId(long id);

    T insertar(T entidad);

    void actualizar(T entidad);

    void eliminar(long id);
}

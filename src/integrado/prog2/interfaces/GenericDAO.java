package integrado.prog2.interfaces;

import java.util.List;

/**
 * Contrato común para consultar, actualizar y dar de baja entidades.
 * El alta se define en cada DAO: Producto requiere además el ID de Categoria.
 */
public interface GenericDAO<T> {

    List<T> listarActivos();

    T buscarPorId(long id);

    void actualizar(T entidad);

    void eliminar(long id);
}

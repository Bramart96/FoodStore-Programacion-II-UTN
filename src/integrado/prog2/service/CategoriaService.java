package integrado.prog2.service;

import integrado.prog2.dao.CategoriaDAO;
import integrado.prog2.entities.Categoria;
import java.util.List;

/** Casos de uso de categorías, sin sentencias SQL ni lectura de consola. */
public class CategoriaService {

    private final CategoriaDAO dao;

    public CategoriaService(CategoriaDAO dao) {
        if (dao == null) {
            throw new IllegalArgumentException("El DAO es obligatorio.");
        }
        this.dao = dao;
    }

    public List<Categoria> listar() {
        return dao.listarActivos();
    }

    public Categoria buscar(long id) {
        return dao.buscarPorId(id);
    }

    public Categoria crear(String nombre, String descripcion) {
        return dao.insertar(new Categoria(nombre, descripcion));
    }

    /** Los campos vacíos conservan su valor, como exige la historia de usuario. */
    public void editar(long id, String nombre, String descripcion) {
        Categoria categoria = dao.buscarPorId(id);
        if (nombre != null && !nombre.isBlank()) {
            categoria.setNombre(nombre);
        }
        if (descripcion != null && !descripcion.isBlank()) {
            categoria.setDescripcion(descripcion);
        }
        dao.actualizar(categoria);
    }

    public void eliminar(long id) {
        dao.eliminar(id);
    }
}

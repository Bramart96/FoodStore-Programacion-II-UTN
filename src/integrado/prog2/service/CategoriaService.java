package integrado.prog2.service;

import integrado.prog2.dao.CategoriaDAO;
import integrado.prog2.entities.Categoria;
import java.util.List;
import integrado.prog2.validation.ValidacionTexto;

/** Casos de uso de categorías, sin sentencias SQL ni lectura de consola. */
public class CategoriaService {

    private final CategoriaDAO dao;

    public CategoriaService(CategoriaDAO dao) {
        if (dao == null) {
            throw new IllegalArgumentException("El DAO es obligatorio.");
        }
        this.dao = dao;
    }

    /** Consulta las categorías activas del catálogo. */
    public List<Categoria> listar() {
        return dao.listarActivos();
    }

    /** Recupera una categoría activa por su identificador. */
    public Categoria buscar(long id) {
        return dao.buscarPorId(id);
    }

    /** Valida los textos y registra una categoría nueva mediante JDBC. */
    public Categoria crear(String nombre, String descripcion) {
        return dao.insertar(new Categoria(
                ValidacionTexto.obligatorio(nombre, "nombre de categoría", 100),
                ValidacionTexto.obligatorio(descripcion, "descripción de categoría", 255)));
    }

    /** Los campos vacíos conservan su valor, como exige la historia de usuario. */
    public void editar(long id, String nombre, String descripcion) {
        Categoria categoria = dao.buscarPorId(id);
        if (nombre != null && !nombre.isBlank()) {
            categoria.setNombre(ValidacionTexto.obligatorio(
                    nombre, "nombre de categoría", 100));
        }
        if (descripcion != null && !descripcion.isBlank()) {
            categoria.setDescripcion(ValidacionTexto.obligatorio(
                    descripcion, "descripción de categoría", 255));
        }
        dao.actualizar(categoria);
    }

    /** Solicita la baja lógica; el DAO impide eliminar categorías con productos activos. */
    public void eliminar(long id) {
        dao.eliminar(id);
    }
}

package integrado.prog2.service;

import integrado.prog2.dao.CategoriaDAO;
import integrado.prog2.dao.ProductoDAO;
import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Producto;
import java.util.List;
import integrado.prog2.validation.ValidacionTexto;

/**
 * Coordina el catálogo sin guardar la categoría dentro de Producto.
 */
public class ProductoService {

    private final ProductoDAO productoDAO;
    private final CategoriaDAO categoriaDAO;

    public ProductoService(ProductoDAO productoDAO, CategoriaDAO categoriaDAO) {
        if (productoDAO == null || categoriaDAO == null) {
            throw new IllegalArgumentException("Los DAO son obligatorios.");
        }
        this.productoDAO = productoDAO;
        this.categoriaDAO = categoriaDAO;
    }

    /** Consulta el catálogo de productos activos. */
    public List<Producto> listar() {
        return productoDAO.listarActivos();
    }

    public Producto buscar(long id) {
        return productoDAO.buscarPorId(id);
    }

    public List<Categoria> listarCategorias() {
        return categoriaDAO.listarActivos();
    }

    public Categoria buscarCategoria(long id) {
        return categoriaDAO.buscarPorId(id);
    }

    /** Carga la agregación unidireccional Categoria -> Productos. */
    public Categoria obtenerCategoriaConProductos(long idCategoria) {
        Categoria categoria = categoriaDAO.buscarPorId(idCategoria);
        for (Producto producto : productoDAO.listarPorCategoria(idCategoria)) {
            categoria.agregarProducto(producto);
        }
        return categoria;
    }

    /** Valida los datos del producto y la categoría activa antes de guardarlo. */
    public Producto crear(String nombre, Double precio, String descripcion,
                         int stock, String imagen, Boolean disponible, long idCategoria) {
        String nombreValido = ValidacionTexto.obligatorio(nombre, "nombre del producto", 120);
        String descripcionValida = ValidacionTexto.obligatorio(
                descripcion, "descripción del producto", 255);
        String imagenValida = ValidacionTexto.opcional(imagen, "imagen", 255);
        validarPrecio(precio);
        categoriaDAO.buscarPorId(idCategoria);
        Producto producto = new Producto(nombreValido, precio, descripcionValida,
                                        stock, imagenValida, disponible);
        return productoDAO.insertar(producto, idCategoria);
    }

    /** Los valores vacíos o nulos conservan los datos existentes. */
    public void editar(long id, String nombre, Double precio, String descripcion,
                       Integer stock, String imagen, Boolean disponible) {
        Producto producto = productoDAO.buscarPorId(id);
        if (nombre != null && !nombre.isBlank()) {
            producto.setNombre(ValidacionTexto.obligatorio(
                    nombre, "nombre del producto", 120));
        }
        if (precio != null) {
            validarPrecio(precio);
            producto.setPrecio(precio);
        }
        if (descripcion != null && !descripcion.isBlank()) {
            producto.setDescripcion(ValidacionTexto.obligatorio(
                    descripcion, "descripción del producto", 255));
        }
        if (stock != null) {
            producto.setStock(stock);
        }
        if (imagen != null && !imagen.isBlank()) {
            producto.setImagen(ValidacionTexto.opcional(imagen, "imagen", 255));
        }
        if (disponible != null) {
            producto.setDisponible(disponible);
        }
        productoDAO.actualizar(producto);
    }

    /** Evita valores que no caben en el DECIMAL(10,2) del esquema. */
    private void validarPrecio(Double precio) {
        if (precio == null || !Double.isFinite(precio) || precio < 0
                || precio > 99999999.99) {
            throw new IllegalArgumentException(
                    "El precio debe estar entre 0 y 99999999,99.");
        }
    }

    /** Modifica la asociación en la base de datos sin añadir una referencia inversa a Producto. */
    public void reasignarCategoria(long idProducto, long idCategoria) {
        productoDAO.reasignarCategoria(idProducto, idCategoria);
    }

    /** Da de baja lógicamente un producto, conservando su registro en la base. */
    public void eliminar(long id) {
        productoDAO.eliminar(id);
    }
}

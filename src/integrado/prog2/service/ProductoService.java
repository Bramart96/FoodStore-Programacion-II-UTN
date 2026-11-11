package integrado.prog2.service;

import integrado.prog2.dao.CategoriaDAO;
import integrado.prog2.dao.ProductoDAO;
import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Producto;
import java.util.List;

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

    public Producto crear(String nombre, Double precio, String descripcion,
                         int stock, String imagen, Boolean disponible, long idCategoria) {
        categoriaDAO.buscarPorId(idCategoria);
        Producto producto = new Producto(nombre, precio, descripcion,
                                        stock, imagen, disponible);
        return productoDAO.insertar(producto, idCategoria);
    }

    /** Los valores vacíos o nulos conservan los datos existentes. */
    public void editar(long id, String nombre, Double precio, String descripcion,
                       Integer stock, String imagen, Boolean disponible) {
        Producto producto = productoDAO.buscarPorId(id);
        if (nombre != null && !nombre.isBlank()) {
            producto.setNombre(nombre);
        }
        if (precio != null) {
            producto.setPrecio(precio);
        }
        if (descripcion != null && !descripcion.isBlank()) {
            producto.setDescripcion(descripcion);
        }
        if (stock != null) {
            producto.setStock(stock);
        }
        if (imagen != null && !imagen.isBlank()) {
            producto.setImagen(imagen);
        }
        if (disponible != null) {
            producto.setDisponible(disponible);
        }
        productoDAO.actualizar(producto);
    }

    public void reasignarCategoria(long idProducto, long idCategoria) {
        productoDAO.reasignarCategoria(idProducto, idCategoria);
    }

    public void eliminar(long id) {
        productoDAO.eliminar(id);
    }
}

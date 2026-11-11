package integrado.prog2.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Categoría de productos. La relación se recorre solo de Categoria a Producto.
 */
public class Categoria extends Base {

    private String nombre;
    private String descripcion;
    private final List<Producto> productos = new ArrayList<>();

    public Categoria(String nombre, String descripcion) {
        super();
        setNombre(nombre);
        setDescripcion(descripcion);
    }

    /** Construye una categoría recuperada de la base de datos. */
    public Categoria(Long id, boolean eliminado, LocalDateTime createdAt,
                     String nombre, String descripcion) {
        super(id, eliminado, createdAt);
        setNombre(nombre);
        setDescripcion(descripcion);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /** Devuelve una copia no modificable de la agregación. */
    public List<Producto> getProductos() {
        return List.copyOf(productos);
    }

    public void agregarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (!productos.contains(producto)) {
            productos.add(producto);
        }
    }

    @Override
    public String toString() {
        return "Categoría {id=" + getId() + ", nombre='" + nombre
                + "', descripción='" + descripcion + "'}";
    }
}

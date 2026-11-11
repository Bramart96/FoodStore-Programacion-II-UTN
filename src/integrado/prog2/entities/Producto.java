package integrado.prog2.entities;

import integrado.prog2.exception.PrecioInvalidoException;
import integrado.prog2.exception.StockInvalidoException;
import java.time.LocalDateTime;

/**
 * Producto del catálogo. No guarda una referencia inversa a Categoria:
 * la clave foránea categoria_id se administra en la capa DAO.
 */
public class Producto extends Base {

    private String nombre;
    private Double precio;
    private String descripcion;
    private int stock;
    private String imagen;
    private Boolean disponible;

    public Producto(String nombre, Double precio, String descripcion,
                    int stock, String imagen, Boolean disponible) {
        super();
        setNombre(nombre);
        setPrecio(precio);
        setDescripcion(descripcion);
        setStock(stock);
        setImagen(imagen);
        setDisponible(disponible);
    }

    /** Construye un producto recuperado de la base de datos. */
    public Producto(Long id, boolean eliminado, LocalDateTime createdAt,
                    String nombre, Double precio, String descripcion,
                    int stock, String imagen, Boolean disponible) {
        super(id, eliminado, createdAt);
        setNombre(nombre);
        setPrecio(precio);
        setDescripcion(descripcion);
        setStock(stock);
        setImagen(imagen);
        setDisponible(disponible);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        if (precio == null || !Double.isFinite(precio) || precio < 0) {
            throw new PrecioInvalidoException("El precio debe ser un número mayor o igual a cero.");
        }
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new StockInvalidoException("El stock no puede ser negativo.");
        }
        this.stock = stock;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        if (disponible == null) {
            throw new IllegalArgumentException("Debe indicar la disponibilidad del producto.");
        }
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "Producto {id=" + getId() + ", nombre='" + nombre
                + "', precio=" + precio + ", stock=" + stock
                + ", disponible=" + disponible + "}";
    }
}

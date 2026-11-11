package integrado.prog2.entities;

/**
 * Línea de pedido en memoria. Conoce a su Producto, pero Producto
 * no conoce los detalles de los pedidos en los que aparece.
 */
public class DetallePedido extends Base {

    private int cantidad;
    private Double subtotal;
    private final Producto producto;

    public DetallePedido(int cantidad, Double subtotal, Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto del detalle es obligatorio.");
        }
        this.producto = producto;
        actualizarValores(cantidad, subtotal);
    }

    public int getCantidad() {
        return cantidad;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public Producto getProducto() {
        return producto;
    }

    /** Modifica los datos de un detalle antes de confirmar el pedido. */
    public void actualizarValores(int cantidad, Double subtotal) {
        if (isEliminado()) {
            throw new IllegalStateException("No se puede modificar un detalle eliminado.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (subtotal == null || !Double.isFinite(subtotal) || subtotal < 0) {
            throw new IllegalArgumentException("El subtotal debe ser un número válido.");
        }
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "Detalle {id=" + getId() + ", producto='" + producto.getNombre()
                + "', cantidad=" + cantidad + ", subtotal=" + subtotal + "}";
    }
}

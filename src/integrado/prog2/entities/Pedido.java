package integrado.prog2.entities;

import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.interfaces.Calculable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido mantenido en memoria. Contiene los detalles, pero no guarda
 * una referencia inversa al Usuario que lo administra.
 */
public class Pedido extends Base implements Calculable {

    private final LocalDate fecha;
    private Estado estado;
    private Double total;
    private FormaPago formaPago;
    private final List<DetallePedido> detalles = new ArrayList<>();

    public Pedido(FormaPago formaPago) {
        this.fecha = LocalDate.now();
        this.estado = Estado.PENDIENTE;
        this.total = 0.0;
        setFormaPago(formaPago);
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Estado getEstado() {
        return estado;
    }

    /** El servicio de pedidos coordinará primero cualquier reposición de stock. */
    public void setEstado(Estado nuevoEstado) {
        validarModificable();
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        this.estado = nuevoEstado;
    }

    public Double getTotal() {
        return total;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        validarModificable();
        if (formaPago == null) {
            throw new IllegalArgumentException("La forma de pago es obligatoria.");
        }
        this.formaPago = formaPago;
    }

    /** Incluye los detalles dados de baja, para conservar el historial. */
    public List<DetallePedido> getDetalles() {
        return List.copyOf(detalles);
    }

    public List<DetallePedido> getDetallesActivos() {
        List<DetallePedido> activos = new ArrayList<>();
        for (DetallePedido detalle : detalles) {
            if (!detalle.isEliminado()) {
                activos.add(detalle);
            }
        }
        return activos;
    }

    /** Firma exigida por el UML: cantidad, precio unitario y producto. */
    public void addDetallePedido(int cantidad, Double precio, Producto producto) {
        validarPendiente();
        validarProductoYPrecio(producto, precio);
        if (findeDetallePedidoByProducto(producto) != null) {
            throw new IllegalArgumentException("El producto ya está incluido en este pedido.");
        }
        Double subtotal = calcularSubtotal(cantidad, precio);
        detalles.add(new DetallePedido(cantidad, subtotal, producto));
        calcularTotal();
    }

    /** Conserva el nombre exacto del método indicado en la consigna. */
    public DetallePedido findeDetallePedidoByProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("Debe indicar un producto.");
        }
        for (DetallePedido detalle : detalles) {
            if (!detalle.isEliminado() && detalle.getProducto().equals(producto)) {
                return detalle;
            }
        }
        return null;
    }

    public void deleteDetallePedidoByProducto(Producto producto) {
        validarPendiente();
        DetallePedido detalle = findeDetallePedidoByProducto(producto);
        if (detalle == null) {
            throw new IllegalArgumentException("No se encontró el producto en el pedido.");
        }
        detalle.eliminar();
        calcularTotal();
    }

    /** Permite editar la cantidad antes de confirmar el pedido. */
    public void actualizarCantidadDetalle(Producto producto, int cantidad) {
        validarPendiente();
        DetallePedido detalle = findeDetallePedidoByProducto(producto);
        if (detalle == null) {
            throw new IllegalArgumentException("No se encontró el producto en el pedido.");
        }
        Double subtotal = calcularSubtotal(cantidad, detalle.getProducto().getPrecio());
        detalle.actualizarValores(cantidad, subtotal);
        calcularTotal();
    }

    @Override
    public void calcularTotal() {
        double suma = 0.0;
        for (DetallePedido detalle : detalles) {
            if (!detalle.isEliminado()) {
                suma += detalle.getSubtotal();
            }
        }
        if (!Double.isFinite(suma)) {
            throw new IllegalArgumentException("El total calculado no es válido.");
        }
        total = suma;
    }

    /** La composición implica dar de baja también los detalles. */
    @Override
    public void eliminar() {
        super.eliminar();
        for (DetallePedido detalle : detalles) {
            detalle.eliminar();
        }
    }

    private void validarModificable() {
        if (isEliminado() || estado == Estado.TERMINADO || estado == Estado.CANCELADO) {
            throw new IllegalStateException("El pedido ya no admite modificaciones.");
        }
    }

    private void validarPendiente() {
        validarModificable();
        if (estado != Estado.PENDIENTE) {
            throw new IllegalStateException("Los detalles solo se modifican antes de confirmar.");
        }
    }

    private void validarProductoYPrecio(Producto producto, Double precio) {
        if (producto == null || producto.isEliminado() || !Boolean.TRUE.equals(producto.getDisponible())) {
            throw new IllegalArgumentException("El producto debe existir y estar disponible.");
        }
        if (precio == null || !Double.isFinite(precio) || precio < 0) {
            throw new IllegalArgumentException("El precio no es válido.");
        }
        if (Double.compare(precio, producto.getPrecio()) != 0) {
            throw new IllegalArgumentException("El precio debe coincidir con el del producto.");
        }
    }

    private Double calcularSubtotal(int cantidad, Double precio) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        double subtotal = cantidad * precio;
        if (!Double.isFinite(subtotal)) {
            throw new IllegalArgumentException("El subtotal calculado no es válido.");
        }
        return subtotal;
    }

    @Override
    public String toString() {
        return "Pedido {id=" + getId() + ", fecha=" + fecha
                + ", estado=" + estado + ", formaPago=" + formaPago
                + ", total=" + total + "}";
    }
}

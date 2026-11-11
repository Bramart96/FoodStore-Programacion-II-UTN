package integrado.prog2.service;

import integrado.prog2.dao.ProductoDAO;
import integrado.prog2.dao.StockDAO;
import integrado.prog2.entities.DetallePedido;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.StockInvalidoException;
import integrado.prog2.repository.PedidoRepository;
import integrado.prog2.repository.UsuarioRepository;
import java.util.List;

/** Casos de uso de pedidos; la gestión de SQL se delega a StockDAO. */
public class PedidoService {

    private final UsuarioRepository usuarios;
    private final PedidoRepository pedidos;
    private final ProductoDAO productos;
    private final StockDAO stock;

    public PedidoService(UsuarioRepository usuarios, PedidoRepository pedidos,
                         ProductoDAO productos, StockDAO stock) {
        if (usuarios == null || pedidos == null || productos == null || stock == null) {
            throw new IllegalArgumentException("Faltan repositorios o DAO de pedidos.");
        }
        this.usuarios = usuarios;
        this.pedidos = pedidos;
        this.productos = productos;
        this.stock = stock;
    }

    public List<Usuario> listarUsuariosActivos() {
        return usuarios.listarActivos();
    }

    public List<Usuario> listarUsuariosConPedidos() {
        return pedidos.listarUsuariosConPedidos();
    }

    public Usuario consultarHistorial(long idUsuario) {
        return pedidos.buscarUsuarioConHistorial(idUsuario);
    }

    public Pedido buscar(long idPedido) {
        return pedidos.buscarActivo(idPedido);
    }

    /** Crea un borrador en memoria; no lo registra hasta descontar todo el stock. */
    public Pedido iniciar(long idUsuario, FormaPago pago) {
        usuarios.buscarActivo(idUsuario);
        return new Pedido(pago);
    }

    public List<Producto> listarProductos() {
        return productos.listarActivos();
    }

    public void agregarDetalle(Pedido borrador, long idProducto, int cantidad) {
        if (cantidad <= 0) {
            throw new StockInvalidoException("La cantidad debe ser mayor a cero.");
        }
        Producto producto = productos.buscarPorId(idProducto);
        verificarProductoParaPedido(producto, cantidad);
        borrador.addDetallePedido(cantidad, producto.getPrecio(), producto);
    }

    public void cambiarCantidad(Pedido borrador, long idProducto, int cantidad) {
        if (cantidad <= 0) {
            throw new StockInvalidoException("La cantidad debe ser mayor a cero.");
        }
        Producto producto = productos.buscarPorId(idProducto);
        verificarProductoParaPedido(producto, cantidad);
        borrador.actualizarCantidadDetalle(producto, cantidad);
    }

    public void eliminarDetalle(Pedido borrador, long idProducto) {
        Producto producto = productos.buscarPorId(idProducto);
        borrador.deleteDetallePedidoByProducto(producto);
    }

    private void verificarProductoParaPedido(Producto producto, int cantidad) {
        if (producto.isEliminado() || !Boolean.TRUE.equals(producto.getDisponible())) {
            throw new IllegalArgumentException("El producto no está disponible.");
        }
        if (cantidad > producto.getStock()) {
            throw new StockInvalidoException(
                    "Stock insuficiente. Disponibles: " + producto.getStock() + ".");
        }
    }

    public Pedido confirmar(long idUsuario, Pedido borrador) {
        Usuario usuario = usuarios.buscarActivo(idUsuario);
        if (borrador == null || borrador.getEstado() != Estado.PENDIENTE
                || borrador.isEliminado() || borrador.getDetallesActivos().isEmpty()) {
            throw new IllegalArgumentException("El pedido no tiene detalles para confirmar.");
        }

        borrador.calcularTotal();
        // Si falla un solo producto, el DAO hace rollback y no se registra el pedido.
        stock.descontar(borrador.getDetallesActivos());
        borrador.setEstado(Estado.CONFIRMADO);
        pedidos.registrar(usuario, borrador);
        return borrador;
    }

    public void actualizarPago(long idPedido, FormaPago pago) {
        Pedido pedido = pedidos.buscarActivo(idPedido);
        if (pedido.getEstado() != Estado.CONFIRMADO) {
            throw new IllegalStateException("Solo se puede editar un pedido confirmado.");
        }
        pedido.setFormaPago(pago);
    }

    public void actualizarEstado(long idPedido, Estado nuevoEstado) {
        Pedido pedido = pedidos.buscarActivo(idPedido);
        if (pedido.getEstado() != Estado.CONFIRMADO) {
            throw new IllegalStateException("El pedido no admite cambios de estado.");
        }
        if (nuevoEstado == Estado.CANCELADO) {
            stock.reponer(pedido.getDetallesActivos());
            pedido.setEstado(Estado.CANCELADO);
        } else if (nuevoEstado == Estado.TERMINADO) {
            pedido.setEstado(Estado.TERMINADO);
        } else if (nuevoEstado != Estado.CONFIRMADO) {
            throw new IllegalArgumentException(
                    "Un pedido confirmado solo puede terminarse o cancelarse.");
        }
    }

    /** No se repone dos veces el stock de un pedido ya cancelado. */
    public void eliminar(long idPedido) {
        Pedido pedido = pedidos.buscarActivo(idPedido);
        if (pedido.getEstado() == Estado.CONFIRMADO) {
            stock.reponer(pedido.getDetallesActivos());
        }
        pedido.eliminar();
    }

    public List<DetallePedido> verDetalles(long idPedido) {
        return pedidos.buscarActivo(idPedido).getDetalles();
    }
}

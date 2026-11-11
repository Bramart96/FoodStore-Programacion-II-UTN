package integrado.prog2.repository;

import integrado.prog2.entities.DetallePedido;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Usuario;
import integrado.prog2.exception.EntidadNoEncontradaException;
import java.util.ArrayList;
import java.util.List;

/**
 * Administra los IDs de pedidos y detalles en memoria.
 * No almacena una relación inversa: navega desde Usuario hacia Pedido.
 */
public class PedidoRepository {

    private final UsuarioRepository usuarios;
    private long siguientePedidoId = 1;
    private long siguienteDetalleId = 1;

    public PedidoRepository(UsuarioRepository usuarios) {
        if (usuarios == null) {
            throw new IllegalArgumentException("El repositorio de usuarios es obligatorio.");
        }
        this.usuarios = usuarios;
    }

    public void registrar(Usuario usuario, Pedido pedido) {
        if (usuario == null || usuario.isEliminado()
                || pedido == null || pedido.getId() != null
                || pedido.isEliminado() || pedido.getDetallesActivos().isEmpty()) {
            throw new IllegalArgumentException("El pedido o el usuario no son válidos.");
        }

        pedido.asignarId(siguientePedidoId++);
        for (DetallePedido detalle : pedido.getDetallesActivos()) {
            detalle.asignarId(siguienteDetalleId++);
        }
        usuario.agregarPedido(pedido);
    }

    public Pedido buscarActivo(long idPedido) {
        for (Usuario usuario : usuarios.listarTodos()) {
            for (Pedido pedido : usuario.getPedidos()) {
                if (pedido.getId() != null && pedido.getId().equals(idPedido)
                        && !pedido.isEliminado()) {
                    return pedido;
                }
            }
        }
        throw new EntidadNoEncontradaException(
                "No existe un pedido activo con ID " + idPedido + ".");
    }

    public List<Usuario> listarUsuariosConPedidos() {
        List<Usuario> encontrados = new ArrayList<>();
        for (Usuario usuario : usuarios.listarTodos()) {
            boolean tienePedidos = false;
            for (Pedido pedido : usuario.getPedidos()) {
                if (!pedido.isEliminado()) {
                    tienePedidos = true;
                    break;
                }
            }
            if (tienePedidos) {
                encontrados.add(usuario);
            }
        }
        return List.copyOf(encontrados);
    }

    /** Incluye usuarios eliminados: sus pedidos permanecen como historial. */
    public Usuario buscarUsuarioConHistorial(long idUsuario) {
        return usuarios.buscarIncluidoEliminado(idUsuario);
    }
}

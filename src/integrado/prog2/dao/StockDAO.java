package integrado.prog2.dao;

import integrado.prog2.config.ConfiguracionDB;
import integrado.prog2.entities.DetallePedido;
import integrado.prog2.exception.PersistenciaException;
import integrado.prog2.exception.StockInvalidoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Actualiza el stock de todos los detalles dentro de una única transacción.
 * Los pedidos siguen en memoria; solo se modifica la tabla producto.
 */
public class StockDAO {

    private final ConexionDB conexionDB;
    private final ConfiguracionDB configuracion;

    /**
     * Prepara el acceso al stock con la conexión y configuración externa.
     * @param conexionDB responsable de abrir conexiones JDBC
     * @param configuracion datos de la unidad de persistencia
     */
    public StockDAO(ConexionDB conexionDB, ConfiguracionDB configuracion) {
        if (conexionDB == null || configuracion == null) {
            throw new IllegalArgumentException("La conexión y su configuración son obligatorias.");
        }
        this.conexionDB = conexionDB;
        this.configuracion = configuracion;
    }

    /**
     * Descuenta las cantidades de todas las líneas de una compra.
     * Si alguna línea no puede descontarse, se revierte toda la operación.
     * @param detalles líneas activas del pedido a confirmar
     */
    public void descontar(List<DetallePedido> detalles) {
        ejecutarTransaccion(detalles, false);
    }

    /**
     * Restituye las cantidades de todas las líneas de un pedido cancelado.
     * La reposición también es atómica para evitar ajustes parciales.
     * @param detalles líneas activas del pedido
     */
    public void reponer(List<DetallePedido> detalles) {
        ejecutarTransaccion(detalles, true);
    }

    private void ejecutarTransaccion(List<DetallePedido> detalles, boolean reposicion) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener detalles para actualizar stock.");
        }

        // Se impide el desbordamiento al sumar stock y se valida el saldo al restar.
        String sql = reposicion
                ? "UPDATE producto SET stock = stock + ? "
                + "WHERE id = ? AND stock <= 2147483647 - ?"
                : "UPDATE producto SET stock = stock - ? "
                + "WHERE id = ? AND eliminado = FALSE "
                + "AND disponible = TRUE AND stock >= ?";

        try (Connection conexion = conexionDB.abrir(configuracion)) {
            conexion.setAutoCommit(false);
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                for (DetallePedido detalle : detalles) {
                    if (detalle == null || detalle.isEliminado()
                            || detalle.getProducto() == null
                            || detalle.getProducto().getId() == null
                            || detalle.getCantidad() <= 0) {
                        throw new IllegalArgumentException("Se encontró un detalle de pedido inválido.");
                    }

                    sentencia.setInt(1, detalle.getCantidad());
                    sentencia.setLong(2, detalle.getProducto().getId());
                    sentencia.setInt(3, detalle.getCantidad());

                    if (sentencia.executeUpdate() != 1) {
                        if (reposicion) {
                            throw new StockInvalidoException(
                                    "No se pudo reponer stock del producto ID "
                                    + detalle.getProducto().getId() + ".");
                        }
                        throw new StockInvalidoException(
                                "Stock insuficiente o producto no disponible (ID "
                                + detalle.getProducto().getId() + "). Pedido cancelado.");
                    }
                }
                conexion.commit();
            } catch (SQLException ex) {
                intentarRollback(conexion, ex);
                throw new PersistenciaException("Falló la transacción de stock. Se revirtieron los cambios.", ex);
            } catch (RuntimeException ex) {
                intentarRollback(conexion, ex);
                throw ex;
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo completar la transacción JDBC.", ex);
        }
    }

    /** Evita que falle la reversión sin conservar la causa original. */
    private void intentarRollback(Connection conexion, Exception original) {
        try {
            conexion.rollback();
        } catch (SQLException ex) {
            original.addSuppressed(ex);
        }
    }
}

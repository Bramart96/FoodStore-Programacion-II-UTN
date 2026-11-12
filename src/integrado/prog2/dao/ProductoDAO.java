package integrado.prog2.dao;

import integrado.prog2.config.ConfiguracionDB;
import integrado.prog2.entities.Producto;
import integrado.prog2.exception.EntidadNoEncontradaException;
import integrado.prog2.exception.PersistenciaException;
import integrado.prog2.interfaces.GenericDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso JDBC a producto. La categoria_id pertenece solo a esta capa,
 * porque Producto no tiene la relación inversa en el UML.
 */
public class ProductoDAO implements GenericDAO<Producto> {

    private final ConexionDB conexionDB;
    private final ConfiguracionDB configuracion;

    public ProductoDAO(ConexionDB conexionDB, ConfiguracionDB configuracion) {
        if (conexionDB == null || configuracion == null) {
            throw new IllegalArgumentException("Falta la configuración de la base.");
        }
        this.conexionDB = conexionDB;
        this.configuracion = configuracion;
    }

    /** Recupera los productos activos y los convierte en objetos del dominio. */
    @Override
    public List<Producto> listarActivos() {
        String sql = "SELECT id, nombre, precio, descripcion, stock, imagen, "
                   + "disponible, eliminado, created_at FROM producto "
                   + "WHERE eliminado = FALSE ORDER BY id";
        try (Connection conexion = conexionDB.abrir(configuracion);
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            return leerProductos(resultado);
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudieron listar los productos.", ex);
        }
    }

    /** Filtra productos activos por la clave foránea de su categoría. */
    public List<Producto> listarPorCategoria(long idCategoria) {
        String sql = "SELECT id, nombre, precio, descripcion, stock, imagen, "
                   + "disponible, eliminado, created_at FROM producto "
                   + "WHERE categoria_id = ? AND eliminado = FALSE ORDER BY id";
        try (Connection conexion = conexionDB.abrir(configuracion);
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setLong(1, idCategoria);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return leerProductos(resultado);
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudieron listar los productos de la categoría.", ex);
        }
    }

    /** Recupera un producto activo por su ID mediante SQL parametrizado. */
    @Override
    public Producto buscarPorId(long id) {
        String sql = "SELECT id, nombre, precio, descripcion, stock, imagen, "
                   + "disponible, eliminado, created_at FROM producto "
                   + "WHERE id = ? AND eliminado = FALSE";
        try (Connection conexion = conexionDB.abrir(configuracion);
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setLong(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapear(resultado);
                }
            }
            throw new EntidadNoEncontradaException(
                    "No existe un producto activo con ID " + id + ".");
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo buscar el producto.", ex);
        }
    }

    /** El ID de categoría es un parámetro, nunca un atributo de Producto. */
    public Producto insertar(Producto producto, long idCategoria) {
        if (producto == null || producto.getId() != null) {
            throw new IllegalArgumentException("Debe indicar un producto nuevo.");
        }
        String sql = "INSERT INTO producto "
                   + "(nombre, precio, descripcion, stock, imagen, disponible, categoria_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexion = conexionDB.abrir(configuracion)) {
            verificarCategoriaActiva(conexion, idCategoria);
            try (PreparedStatement sentencia = conexion.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {
                cargarParametros(sentencia, producto);
                sentencia.setLong(7, idCategoria);
                sentencia.executeUpdate();
                try (ResultSet claves = sentencia.getGeneratedKeys()) {
                    if (!claves.next()) {
                        throw new PersistenciaException("MySQL no devolvió el ID del producto.");
                    }
                    producto.asignarId(claves.getLong(1));
                }
            }
            return buscarPorId(producto.getId());
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo crear el producto.", ex);
        }
    }

    /** Actualiza los datos propios del producto sin cambiar la clave foránea de categoría. */
    @Override
    public void actualizar(Producto producto) {
        if (producto == null || producto.getId() == null) {
            throw new IllegalArgumentException("Debe indicar un producto con ID.");
        }
        String sql = "UPDATE producto SET nombre = ?, precio = ?, descripcion = ?, "
                   + "stock = ?, imagen = ?, disponible = ? "
                   + "WHERE id = ? AND eliminado = FALSE";
        try (Connection conexion = conexionDB.abrir(configuracion)) {
            verificarProductoActivo(conexion, producto.getId());
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                cargarParametros(sentencia, producto);
                sentencia.setLong(7, producto.getId());
                sentencia.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo actualizar el producto.", ex);
        }
    }

    /** Reasigna únicamente categoria_id, sin modificar los demás atributos. */
    public void reasignarCategoria(long idProducto, long idCategoria) {
        String sql = "UPDATE producto SET categoria_id = ? "
                   + "WHERE id = ? AND eliminado = FALSE";
        try (Connection conexion = conexionDB.abrir(configuracion)) {
            verificarProductoActivo(conexion, idProducto);
            verificarCategoriaActiva(conexion, idCategoria);
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setLong(1, idCategoria);
                sentencia.setLong(2, idProducto);
                sentencia.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo reasignar la categoría.", ex);
        }
    }

    /** Aplica la baja lógica para conservar el registro y sus datos históricos. */
    @Override
    public void eliminar(long id) {
        String sql = "UPDATE producto SET eliminado = TRUE "
                   + "WHERE id = ? AND eliminado = FALSE";
        try (Connection conexion = conexionDB.abrir(configuracion);
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setLong(1, id);
            if (sentencia.executeUpdate() == 0) {
                throw new EntidadNoEncontradaException(
                        "No existe un producto activo con ID " + id + ".");
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo eliminar el producto.", ex);
        }
    }

    private void verificarCategoriaActiva(Connection conexion, long id) throws SQLException {
        String sql = "SELECT id FROM categoria WHERE id = ? AND eliminado = FALSE";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setLong(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    throw new EntidadNoEncontradaException(
                            "No existe una categoría activa con ID " + id + ".");
                }
            }
        }
    }

    private void verificarProductoActivo(Connection conexion, long id) throws SQLException {
        String sql = "SELECT id FROM producto WHERE id = ? AND eliminado = FALSE";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setLong(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    throw new EntidadNoEncontradaException(
                            "No existe un producto activo con ID " + id + ".");
                }
            }
        }
    }

    /** Orden común de columnas para INSERT y UPDATE. */
    private void cargarParametros(PreparedStatement sentencia, Producto producto)
            throws SQLException {
        sentencia.setString(1, producto.getNombre());
        sentencia.setDouble(2, producto.getPrecio());
        sentencia.setString(3, producto.getDescripcion());
        sentencia.setInt(4, producto.getStock());
        sentencia.setString(5, producto.getImagen());
        sentencia.setBoolean(6, producto.getDisponible());
    }

    private List<Producto> leerProductos(ResultSet resultado) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        while (resultado.next()) {
            productos.add(mapear(resultado));
        }
        return productos;
    }

    private Producto mapear(ResultSet resultado) throws SQLException {
        Timestamp creado = resultado.getTimestamp("created_at");
        return new Producto(
                resultado.getLong("id"),
                resultado.getBoolean("eliminado"),
                creado.toLocalDateTime(),
                resultado.getString("nombre"),
                resultado.getDouble("precio"),
                resultado.getString("descripcion"),
                resultado.getInt("stock"),
                resultado.getString("imagen"),
                resultado.getBoolean("disponible")
        );
    }
}

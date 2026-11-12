package integrado.prog2.dao;

import integrado.prog2.config.ConfiguracionDB;
import integrado.prog2.entities.Categoria;
import integrado.prog2.exception.CategoriaDuplicadaException;
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
 * Operaciones JDBC sobre la tabla categoria.
 * Las consultas SQL se mantienen en esta capa.
 */
public class CategoriaDAO implements GenericDAO<Categoria> {

    private final ConexionDB conexionDB;
    private final ConfiguracionDB configuracion;

    public CategoriaDAO(ConexionDB conexionDB, ConfiguracionDB configuracion) {
        if (conexionDB == null || configuracion == null) {
            throw new IllegalArgumentException("Falta la conexión o su configuración.");
        }
        this.conexionDB = conexionDB;
        this.configuracion = configuracion;
    }

    /** Recupera solamente las categorías que no fueron dadas de baja. */
    @Override
    public List<Categoria> listarActivos() {
        String sql = "SELECT id, nombre, descripcion, eliminado, created_at "
                   + "FROM categoria WHERE eliminado = FALSE ORDER BY id";
        List<Categoria> categorias = new ArrayList<>();

        try (Connection conexion = conexionDB.abrir(configuracion);
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                categorias.add(mapear(resultado));
            }
            return categorias;
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudieron consultar las categorías.", ex);
        }
    }

    /** Busca una categoría activa con una consulta parametrizada. */
    @Override
    public Categoria buscarPorId(long id) {
        String sql = "SELECT id, nombre, descripcion, eliminado, created_at "
                   + "FROM categoria WHERE id = ? AND eliminado = FALSE";

        try (Connection conexion = conexionDB.abrir(configuracion);
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setLong(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapear(resultado);
                }
            }
            throw new EntidadNoEncontradaException("No existe una categoría activa con ID " + id + ".");
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo buscar la categoría.", ex);
        }
    }

    /** Comprueba la unicidad del nombre, inserta la categoría y recupera el ID generado. */
    public Categoria insertar(Categoria categoria) {
        if (categoria == null || categoria.getId() != null) {
            throw new IllegalArgumentException("Debe indicar una categoría nueva.");
        }
        validarDatos(categoria);
        String sql = "INSERT INTO categoria (nombre, descripcion) VALUES (?, ?)";

        try (Connection conexion = conexionDB.abrir(configuracion)) {
            validarNombreUnico(conexion, categoria.getNombre(), null);
            try (PreparedStatement sentencia = conexion.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {
                sentencia.setString(1, categoria.getNombre());
                sentencia.setString(2, categoria.getDescripcion());
                sentencia.executeUpdate();
                try (ResultSet claves = sentencia.getGeneratedKeys()) {
                    if (!claves.next()) {
                        throw new PersistenciaException("MySQL no devolvió el ID de la categoría.");
                    }
                    categoria.asignarId(claves.getLong(1));
                }
            }
            return buscarPorId(categoria.getId());
        } catch (SQLException ex) {
            throw traducirError("No se pudo crear la categoría.", ex);
        }
    }

    /** Actualiza una categoría activa respetando la unicidad de su nombre. */
    @Override
    public void actualizar(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            throw new IllegalArgumentException("Debe indicar una categoría con ID.");
        }
        validarDatos(categoria);
        String sql = "UPDATE categoria SET nombre = ?, descripcion = ? "
                   + "WHERE id = ? AND eliminado = FALSE";

        try (Connection conexion = conexionDB.abrir(configuracion)) {
            verificarActiva(conexion, categoria.getId());
            validarNombreUnico(conexion, categoria.getNombre(), categoria.getId());
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setString(1, categoria.getNombre());
                sentencia.setString(2, categoria.getDescripcion());
                sentencia.setLong(3, categoria.getId());
                if (sentencia.executeUpdate() == 0) {
                    throw new EntidadNoEncontradaException("La categoría ya no está disponible.");
                }
            }
        } catch (SQLException ex) {
            throw traducirError("No se pudo actualizar la categoría.", ex);
        }
    }

    /** Impide la baja cuando existen productos activos asociados; no realiza borrado físico. */
    @Override
    public void eliminar(long id) {
        String sql = "UPDATE categoria SET eliminado = TRUE "
                   + "WHERE id = ? AND eliminado = FALSE";
        try (Connection conexion = conexionDB.abrir(configuracion)) {
            verificarActiva(conexion, id);
            int asociados = contarProductosActivos(conexion, id);
            if (asociados > 0) {
                throw new IllegalStateException(
                        "No se puede eliminar la categoría: tiene " + asociados
                        + " producto(s) activo(s).");
            }
            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
                sentencia.setLong(1, id);
                if (sentencia.executeUpdate() == 0) {
                    throw new EntidadNoEncontradaException("La categoría ya no está disponible.");
                }
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo eliminar la categoría.", ex);
        }
    }

    private void validarDatos(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es obligatorio.");
        }
        if (categoria.getDescripcion() == null || categoria.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripción de la categoría es obligatoria.");
        }
    }

    private void validarNombreUnico(Connection conexion, String nombre, Long idExcluido)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM categoria WHERE eliminado = FALSE "
                   + "AND nombre = ? AND (? IS NULL OR id <> ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nombre);
            if (idExcluido == null) {
                sentencia.setNull(2, java.sql.Types.BIGINT);
                sentencia.setNull(3, java.sql.Types.BIGINT);
            } else {
                sentencia.setLong(2, idExcluido);
                sentencia.setLong(3, idExcluido);
            }
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next() && resultado.getInt(1) > 0) {
                    throw new CategoriaDuplicadaException(
                            "Ya existe una categoría activa con ese nombre.");
                }
            }
        }
    }

    private void verificarActiva(Connection conexion, long id) throws SQLException {
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

    private int contarProductosActivos(Connection conexion, long categoriaId)
            throws SQLException {
        String sql = "SELECT COUNT(*) FROM producto "
                   + "WHERE categoria_id = ? AND eliminado = FALSE";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setLong(1, categoriaId);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? resultado.getInt(1) : 0;
            }
        }
    }

    private Categoria mapear(ResultSet resultado) throws SQLException {
        Timestamp fecha = resultado.getTimestamp("created_at");
        return new Categoria(
                resultado.getLong("id"),
                resultado.getBoolean("eliminado"),
                fecha.toLocalDateTime(),
                resultado.getString("nombre"),
                resultado.getString("descripcion")
        );
    }

    private PersistenciaException traducirError(String mensaje, SQLException ex) {
        if (ex.getErrorCode() == 1062) {
            return new PersistenciaException(
                    "No se pudo guardar: el nombre ya existe entre las categorías activas.", ex);
        }
        return new PersistenciaException(mensaje, ex);
    }
}

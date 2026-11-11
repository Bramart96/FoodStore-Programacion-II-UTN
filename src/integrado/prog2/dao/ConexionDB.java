package integrado.prog2.dao;

import integrado.prog2.config.ConfiguracionDB;
import integrado.prog2.exception.PersistenciaException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Abre y verifica conexiones JDBC, sin colocar credenciales en el código. */
public class ConexionDB {

    public Connection abrir(ConfiguracionDB configuracion) {
        if (configuracion == null) {
            throw new PersistenciaException("Falta la configuración de conexión.");
        }
        try {
            Class.forName(configuracion.getDriver());
        } catch (ClassNotFoundException ex) {
            throw new PersistenciaException(
                    "No se encontró el driver JDBC. Verificá el archivo .jar en lib/ y el classpath.", ex);
        }
        try {
            return DriverManager.getConnection(
                    configuracion.getUrl(),
                    configuracion.getUsuario(),
                    configuracion.getContrasena());
        } catch (SQLException ex) {
            String detalle;
            if (ex.getErrorCode() == 1045) {
                detalle = "Usuario o contraseña de MySQL incorrectos.";
            } else if (ex.getErrorCode() == 1049) {
                detalle = "La base de datos indicada no existe. Revisá db/01_esquema.sql.";
            } else {
                detalle = "No se pudo conectar a MySQL. Revisá el servidor, puerto y URL.";
            }
            throw new PersistenciaException(detalle, ex);
        }
    }

    /** Prueba la conexión y la cierra automáticamente. */
    public void verificar(ConfiguracionDB configuracion) {
        try (Connection conexion = abrir(configuracion)) {
            if (!conexion.isValid(2)) {
                throw new PersistenciaException("MySQL no respondió a la prueba de conexión.");
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("No se pudo completar la prueba JDBC.", ex);
        }
    }
}

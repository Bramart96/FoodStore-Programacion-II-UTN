package integrado.prog2.service;

import integrado.prog2.config.ConfiguracionDB;
import integrado.prog2.dao.ConexionDB;

/** Coordina el diagnóstico inicial de acceso a la base de datos. */
public class ConexionService {

    private final ConexionDB conexionDB;

    public ConexionService(ConexionDB conexionDB) {
        this.conexionDB = conexionDB;
    }

    /** Comprueba el acceso a la base de datos mediante el DAO de conexión. */
    public void verificar(ConfiguracionDB configuracion) {
        conexionDB.verificar(configuracion);
    }
}

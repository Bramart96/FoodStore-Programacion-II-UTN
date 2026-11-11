package integrado.prog2.exception;

/** Error al buscar un registro inexistente o dado de baja. */
public class EntidadNoEncontradaException extends RuntimeException {

    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}

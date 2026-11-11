package integrado.prog2.exception;

/** Se produce cuando ya existe una categoría activa con el mismo nombre. */
public class CategoriaDuplicadaException extends IllegalArgumentException {

    public CategoriaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}

package integrado.prog2.exception;

/** Indica un precio inválido al crear o modificar un producto. */
public class PrecioInvalidoException extends IllegalArgumentException {

    public PrecioInvalidoException(String mensaje) {
        super(mensaje);
    }
}

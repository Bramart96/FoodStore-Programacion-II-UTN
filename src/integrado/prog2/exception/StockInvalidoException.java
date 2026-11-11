package integrado.prog2.exception;

/** Indica un stock inválido al crear o modificar un producto. */
public class StockInvalidoException extends IllegalArgumentException {

    public StockInvalidoException(String mensaje) {
        super(mensaje);
    }
}

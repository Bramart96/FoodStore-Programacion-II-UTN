package integrado.prog2.exception;

/** Indica que otro usuario ya tiene registrado el correo electrónico. */
public class MailDuplicadoException extends IllegalArgumentException {

    public MailDuplicadoException(String mensaje) {
        super(mensaje);
    }
}

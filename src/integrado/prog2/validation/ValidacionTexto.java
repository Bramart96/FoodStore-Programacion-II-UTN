package integrado.prog2.validation;

/**
 * Validación básica de campos textuales recibidos desde consola.
 * Acepta letras con acentos y nombres alfanuméricos como "7UP".
 */
public final class ValidacionTexto {

    private ValidacionTexto() {
    }

    public static String obligatorio(String valor, String campo, int longitudMaxima) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
        return verificar(valor.trim(), campo, longitudMaxima);
    }

    /** Permite vacío en imagen o cuando Enter significa conservar el dato. */
    public static String opcional(String valor, String campo, int longitudMaxima) {
        if (valor == null || valor.isBlank()) {
            return "";
        }
        return verificar(valor.trim(), campo, longitudMaxima);
    }

    private static String verificar(String valor, String campo, int longitudMaxima) {
        if (valor.length() > longitudMaxima) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " no puede superar " + longitudMaxima + " caracteres.");
        }

        boolean contieneLetras = false;
        for (int i = 0; i < valor.length(); i++) {
            char caracter = valor.charAt(i);
            if (Character.isISOControl(caracter)) {
                throw new IllegalArgumentException(
                        "El campo " + campo + " contiene caracteres no permitidos.");
            }
            if (Character.isLetter(caracter)) {
                contieneLetras = true;
            }
        }
        if (!contieneLetras) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " debe contener letras, no solo números o símbolos.");
        }
        return valor;
    }
}

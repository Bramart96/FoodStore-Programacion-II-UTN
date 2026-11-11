package integrado.prog2.config;

import integrado.prog2.exception.PersistenciaException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.XMLConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Lee los datos de conexión desde META-INF/persistence.xml con DOM.
 * No abre conexiones ni utiliza clases JDBC.
 */
public class ConfiguracionDB {

    private final String unidad;
    private final String driver;
    private final String url;
    private final String usuario;
    private final String contrasena;

    private ConfiguracionDB(String unidad, String driver, String url,
                            String usuario, String contrasena) {
        this.unidad = unidad;
        this.driver = driver;
        this.url = url;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public static ConfiguracionDB cargar() {
        try (InputStream archivo = ConfiguracionDB.class
                .getResourceAsStream("/META-INF/persistence.xml")) {
            if (archivo == null) {
                throw new PersistenciaException(
                        "No se encontró META-INF/persistence.xml. Copiá el archivo a bin/META-INF.");
            }

            DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
            fabrica.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document documento = fabrica.newDocumentBuilder().parse(archivo);
            NodeList unidades = documento.getElementsByTagName("persistence-unit");
            if (unidades.getLength() != 1) {
                throw new PersistenciaException(
                        "Debe existir una unidad de persistencia en persistence.xml.");
            }
            Element unidad = (Element) unidades.item(0);
            return new ConfiguracionDB(
                    unidad.getAttribute("name"),
                    propiedad(unidad, "jakarta.persistence.jdbc.driver"),
                    propiedad(unidad, "jakarta.persistence.jdbc.url"),
                    propiedad(unidad, "jakarta.persistence.jdbc.user"),
                    propiedad(unidad, "jakarta.persistence.jdbc.password")
            );
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            throw new PersistenciaException(
                    "No se pudo leer persistence.xml. Revisá que sea un XML válido.", ex);
        }
    }

    private static String propiedad(Element unidad, String nombre) {
        NodeList propiedades = unidad.getElementsByTagName("property");
        for (int i = 0; i < propiedades.getLength(); i++) {
            Element elemento = (Element) propiedades.item(i);
            if (nombre.equals(elemento.getAttribute("name"))) {
                return elemento.getAttribute("value");
            }
        }
        throw new PersistenciaException("Falta la propiedad " + nombre + " en persistence.xml.");
    }

    public String getUnidad() {
        return unidad;
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }
}

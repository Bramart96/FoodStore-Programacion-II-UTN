# Food Store — Programación II (UTN)

Aplicación de consola para administrar categorías, productos, usuarios y pedidos. Desarrollada en **Java 21** con programación orientada a objetos, colecciones y **JDBC puro** para MySQL 8.

## Tecnologías y alcance

- **Java 21:** herencia, encapsulamiento, interfaces, enumeraciones y excepciones propias.
- **MySQL 8 + JDBC:** persistencia de `Categoria` y `Producto` mediante consultas SQL parametrizadas.
- **Colecciones en memoria:** `Usuario`, `Pedido` y `DetallePedido`; desaparecen al cerrar la aplicación.
- **Transacciones:** el stock de todos los detalles se descuenta dentro de una transacción; ante un error se ejecuta `rollback`.
- **Sin** Maven, Gradle, JPA, Hibernate ni otros ORM.

La aplicación utiliza exclusivamente las tablas `categoria` y `producto`. No existen tablas de usuarios, pedidos ni detalles.

## Organización del código

```text
db/
  01_esquema.sql
src/
  META-INF/persistence.xml
  integrado/prog2/
    Main.java
    config/       Configuración de conexión
    dao/          JDBC y consultas SQL
    entities/     Modelo del dominio
    enums/        Rol, Estado y FormaPago
    exception/    Excepciones propias
    interfaces/   Calculable y GenericDAO
    repository/   Colecciones en memoria
    service/      Reglas de negocio
    ui/           Menús de consola
    validation/   Validación de datos textuales
```

Las relaciones respetan el sentido del UML: `Categoria → Productos`, `Usuario → Pedidos`, `Pedido → Detalles` y `DetallePedido → Producto`, sin referencias inversas en las entidades.

## Requisitos

- **JDK 21** o un JDK posterior que permita compilar con `--release 21`.
- **MySQL Server 8** en funcionamiento y acceso a MySQL Workbench u otra herramienta SQL.
- Driver **MySQL Connector/J** (`.jar`), que debe colocarse localmente en `lib/`. Se obtiene desde el sitio oficial de MySQL; el binario no está versionado.

## Instalación y ejecución (Windows PowerShell)

1. Abrir el archivo `db/01_esquema.sql` en MySQL Workbench y ejecutarlo. Crea la base `foodstore_prog2` y solamente las tablas `categoria` y `producto`. El script es no destructivo.
2. Crear `lib/` si no existe y copiar allí el `.jar` de MySQL Connector/J.
3. Desde la **raíz del proyecto**, preparar una copia local de la configuración:

```powershell
New-Item -ItemType Directory -Path .\bin\META-INF -Force | Out-Null
if (!(Test-Path .\bin\META-INF\persistence.xml)) {
    Copy-Item .\src\META-INF\persistence.xml .\bin\META-INF\persistence.xml
}
notepad .\bin\META-INF\persistence.xml
```

Editar en esa **copia local** la URL, el usuario y la contraseña de MySQL según el entorno. La URL puede ser, por ejemplo, `jdbc:mysql://127.0.0.1:3306/foodstore_prog2`. No guardar credenciales reales en el XML de `src/`, que forma parte del repositorio.

4. Compilar y ejecutar:

```powershell
javac --release 21 -encoding UTF-8 -d bin (Get-ChildItem -Path .\src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
java -cp "bin;lib/*" integrado.prog2.Main
```

Para ejecuciones posteriores no es necesario copiar nuevamente `persistence.xml`; basta con compilar después de cambios de código y ejecutar. La carpeta `bin/` no está versionada.

## Funcionalidades

- **Categorías [BASE DE DATOS]:** alta, listado, edición, baja lógica, control de nombres duplicados e impedimento de baja cuando hay productos activos.
- **Productos [BASE DE DATOS]:** alta, listado general y por categoría, edición, reasignación de categoría, baja lógica y controles de precio/stock.
- **Usuarios [MEMORIA]:** alta, listado, edición, baja lógica, formato y unicidad de correo.
- **Pedidos y detalles [MEMORIA]:** selección de usuario y forma de pago, alta/modificación/baja de detalles en un borrador, cálculo de subtotales y total, confirmación, cambios de estado y pago, cancelación, baja lógica e historial.
- **Configuración:** muestra unidad, URL y driver, sin imprimir la contraseña.

Al confirmar un pedido se descuenta stock mediante JDBC. Si un producto no tiene existencias suficientes, se revierten todos los descuentos y el pedido no se registra. Al cancelar un pedido confirmado se restituye el stock, evitando reposiciones duplicadas.

## Verificaciones realizadas

Se comprobó localmente la creación y consulta de catálogo, el rechazo de categorías duplicadas, el bloqueo de baja de una categoría con productos, la reasignación de productos, el rechazo de stock negativo, la edición de detalles, el cambio de forma de pago, la confirmación y cancelación de pedidos, y un `rollback` real ante falta de stock en el segundo detalle.

También se verificó que, tras reiniciar Java, las categorías y los productos permanecen en MySQL, mientras que los usuarios y los pedidos desaparecen de memoria.

Para el detalle de escenarios y cobertura, consultar [la matriz de historias](docs/matriz_historias.md) y [las pruebas de pedidos](docs/pruebas_pedidos.md).

## Errores habituales

- **No se encontró el driver JDBC:** comprobar que el `.jar` esté en `lib/` y ejecutar con `-cp "bin;lib/*"`.
- **No se pudo conectar a MySQL:** verificar que el servidor esté iniciado, la URL, el puerto y las credenciales locales.
- **No se encontró persistence.xml:** copiar la plantilla a `bin/META-INF/`, sin sobrescribir una configuración local que ya funcione.

## Video demostrativo

[Ver demostración de Food Store en YouTube](https://www.youtube.com/watch?v=WyZeQiygnSw).

**Seguridad:** utilizar datos ficticios para las pruebas; no publicar contraseñas de MySQL, datos de acceso ni información personal real.

# Food Store — Programación II (UTN)

Trabajo Práctico Integrador de Programación II: aplicación de consola con Java 21, POO y JDBC puro.

## Alcance

- `Categoria` y `Producto`: persistencia en base de datos relacional mediante JDBC.
- `Usuario`, `Pedido` y `DetallePedido`: colecciones en memoria.
- Bajas lógicas, excepciones propias, cálculo de pedidos y transacciones para stock.
- Sin Maven, Gradle, JPA ni Hibernate.

## Estado del desarrollo — 11/11/2026

**Implementado y compilado en entorno de asistencia:** `Base`, enums `Rol`, `Estado`, `FormaPago`, interfaz `Calculable`, las cinco entidades (`Categoria`, `Producto`, `Usuario`, `Pedido`, `DetallePedido`), excepciones de precio y stock y Main provisional. Las entidades se probaron en el entorno de asistencia; falta prueba local.

**Implementado y probado en el equipo del estudiante:** CRUD de usuarios en memoria mediante UsuarioRepository, UsuarioService y MenuUsuarios, con alta, listado, edición del correo, rechazo de duplicados, baja lógica y reinicio con memoria vacía.

**Implementado, pendiente de prueba local:** lectura DOM de persistence.xml, conexión JDBC, diagnóstico al iniciar y opción 5 del menú.

**Verificado por el estudiante:** CRUD JDBC de categorías, incluida la permanencia de categorías después de reiniciar el programa. Quedan casos límite y prueba de bloqueo de baja con productos activos.

**Publicado, pendiente de prueba local:** ProductoDAO, ProductoService y MenuProductos para CRUD JDBC, consulta por categoría y reasignación.

**Implementado en código, pendiente de prueba local:** PedidoRepository, PedidoService, StockDAO y MenuPedidos para crear pedidos en memoria, modificar detalles del borrador, confirmar con transacción JDBC, cancelar, terminar y dar de baja. **Aún no se afirma que el flujo de pedidos esté validado por el estudiante.**

## Base de datos (instalación verificada)

El archivo `db/01_esquema.sql` crea la base `foodstore_prog2` y las tablas `categoria` y `producto` para MySQL 8, sin eliminar registros. Su ejecución fue verificada en MySQL Workbench el 11/11/2026: ambas tablas creadas, definiciones consultadas y recuentos iniciales de 0 registros. **Conexión JDBC verificada desde Java con mensaje de éxito al iniciar.**

## Compilar y ejecutar desde PowerShell (Windows)

Requiere JDK 21 (o compilación con `--release 21` desde un JDK posterior), MySQL Server 8 en funcionamiento y el driver MySQL Connector/J `.jar` copiado a `lib/`.

Desde la raíz del proyecto:

```powershell
New-Item -ItemType Directory -Path .\\bin\\META-INF -Force | Out-Null
if (!(Test-Path .\\bin\\META-INF\\persistence.xml)) {
    Copy-Item .\\src\\META-INF\\persistence.xml .\\bin\\META-INF\\persistence.xml
}
# Solo la primera vez, editar la contraseña local con:
# notepad .\\bin\\META-INF\\persistence.xml
javac --release 21 -encoding UTF-8 -d bin (Get-ChildItem -Path .\\src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
java -cp "bin;lib/*" integrado.prog2.Main
```

En el archivo local de `bin/META-INF/` cambiar únicamente los valores de usuario y contraseña si corresponde a la instalación. **No modificar el XML de `src/` con credenciales reales** porque está versionado; `bin/` queda excluido por .gitignore. Nunca compartir capturas de credenciales. Para posteriores ejecuciones no es necesario volver a copiar el XML (solo recompilar y ejecutar).

## Estado de la conexión

Al iniciar, la aplicación realiza una prueba JDBC, informa si hay acceso y muestra errores controlados si no encuentra el driver, la base o las credenciales. La opción 5 informa unidad y URL. La contraseña no se imprime. El diagnóstico y la conexión fueron comprobados correctamente en el equipo del estudiante.

## Pedidos y stock

Los pedidos y sus detalles viven únicamente en memoria. La creación requiere usuario activo, producto disponible y cantidad válida. Al confirmar, StockDAO descuenta todas las unidades dentro de una sola transacción MySQL. Si falla alguna actualización, se hace rollback y no se agrega el pedido al usuario.

La opción 4 permite listar pedidos, consultar historial por usuario, crear un borrador con detalles, confirmar, actualizar pago/estado y realizar una baja lógica. La cancelación repone stock de forma transaccional; se impide una segunda reposición para pedidos cancelados. La baja de un pedido terminado no repone stock porque ya fue cumplido.

La demostración y los casos negativos deben realizarse en MySQL local antes de dar el módulo por verificado.

## Documentación y decisiones

Ver [`docs/bitacora.md`](docs/bitacora.md) para distinguir decisiones, archivos implementados, pruebas ejecutadas y tareas pendientes. El informe PDF y el video demostrativo se prepararán con evidencias reales, antes de la entrega.

**Seguridad:** nunca subir credenciales reales ni archivos locales sensibles.

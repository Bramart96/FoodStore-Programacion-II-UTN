# Bitácora técnica — Food Store, Programación II

## 11/11/2026 — Inicio del desarrollo

### DEC-001 — Plataforma y persistencia
- **Decisión:** Java 21, JDBC puro y MySQL 8, con el esquema de aplicación `foodstore_prog2`.
- **Justificación:** tecnología permitida por la consigna; permite CRUD persistente y pruebas de integridad.
- **Estado:** decidida; conexión todavía no implementada ni probada.

### DEC-002 — Modelo y UML
- **Decisión:** comenzar por `Base`, los tres enums y `Calculable`, sin referencias inversas entre entidades.
- **Detalle:** el diagrama UML muestra `calcularTotal():void`; los nombres de los métodos propios de `Pedido` deberán respetarse literalmente.
- **Estado:** implementados `Base`, `Rol`, `Estado`, `FormaPago` y `Calculable`; resto del modelo pendiente.

### DEC-003 — Capas y dependencias
- **Decisión:** utilizar paquetes `entities`, `enums`, `interfaces`, `dao`, `repository`, `service`, `ui`, `config` y `exception`.
- **Detalle:** el SQL y las importaciones de `java.sql` se concentrarán en DAO. Configuración XML separada de conexiones JDBC.
- **Estado:** organización definida, capas funcionales pendientes.

### Uso de IA
Se consultaron consigna, rúbrica, UML, guía de continuidad, ZIP del TP08 y devolución del docente (100/100) para orientar este primer bloque. Las conversaciones y las decisiones reales servirán para el anexo de uso de IA.

### Pruebas
- **Compilación y ejecución:** comprobadas en el entorno de asistencia con `javac 21.0.12.1`, mostrando el mensaje de inicialización.
- **Verificación en equipo del estudiante:** realizada y confirmada el 11/11/2026 desde Windows PowerShell con Java 25, compilando con `javac --release 21` y ejecutando el Main inicial.
- **Modelo Categoria y Producto:** clases y excepciones de validación compiladas con JDK 21 y probadas en el entorno de asistencia (nombre, agregación, lista protegida, precio/stock inválidos, igualdad por ID y toString). Pendiente prueba local del estudiante.
- **Conexión, CRUD y transacciones:** pendientes de implementación y prueba.

### Registro Git
- `Iniciar repositorio de Food Store`: README inicial.
- `Agregar clase Base, enumeraciones y contrato Calculable`: modelo base.
- `Organizar punto de entrada y registrar decisiones iniciales`: Main, .gitignore, README y bitácora.
- `Agregar Categoria y Producto con validaciones del modelo`: entidades y excepciones; igualdad en Base.

### DEC-004 — Entidades persistentes y validaciones
- **Decisión:** implementar Categoria y Producto según UML; `categoria_id` pertenece al DAO y `nombre_activo` al esquema SQL, no a las entidades Java.
- **Detalle:** Categoria expone una lista no modificable de productos; Producto valida precio, stock y disponibilidad. Base compara por ID cuando existe.
- **Estado:** implementado y probado en asistencia; prueba local pendiente.

### DEC-005 — Usuario, Pedido y DetallePedido (11/11/2026)
- **Implementado:** las tres entidades en memoria, siguiendo las relaciones Usuario → Pedido → DetallePedido → Producto; Pedido implementa Calculable y sus tres métodos obligatorios.
- **Detalles:** total calculado a partir de subtotales, bajas lógicas de detalles, enum de estados y forma de pago; edición de cantidades solo en pedidos pendientes.
- **Prueba en entorno de asistencia:** compilación con JDK 21 y prueba temporal fuera del repositorio de asociación, totales, modificación de cantidad, eliminación lógica, estados terminales y validaciones (resultado correcto).
- **Prueba en computadora del estudiante:** compilación de las cinco entidades confirmada el 11/11/2026.
- **Pendiente de integración:** generación de ID en repositorios, unicidad de mail entre usuarios, stock real de MySQL, confirmación y cancelación transaccionales, menús y CRUD completo.
- **Commit:** `Incorporar usuarios, pedidos y detalles en memoria`.

### DEC-006 — CRUD de Usuario en memoria (11/11/2026)
- **Implementación:** `UsuarioRepository` guarda usuarios en una `ArrayList` y genera IDs; rechaza mails duplicados sin distinguir mayúsculas y minúsculas; conserva las bajas lógicas.
- **Servicio:** `UsuarioService` coordina altas, consultas, cambios de datos y baja. La validación de formato permanece en `Usuario`.
- **Interfaz:** `MenuUsuarios` utiliza un Scanner compartido desde `Main`, con opciones de listar, crear, editar y eliminar; el menú principal identifica los módulos aún pendientes.
- **Prueba realizada en entorno de asistencia:** compilación con JDK 21; alta con ID, listado, correo duplicado rechazado, edición de correo, baja lógica y listado vacío posterior. Resultado satisfactorio. No se ha probado todavía en el equipo del estudiante.
- **Pendiente:** comprobación manual del usuario; CRUD de pedidos, JDBC, transacciones y menús de categorías/productos.
- **Commits:** `Implementar repositorio y servicio de usuarios en memoria`, `Agregar menú CRUD de usuarios y actualizar Main`.


## 11/11/2026 — Preparación del esquema relacional de Programación II

### DEC-007 — Esquema relacional para JDBC
- **Decisión:** utilizar `foodstore_prog2` en MySQL 8 con las tablas `categoria` y `producto` y una relación 1:N mediante `producto.categoria_id`.
- **Motivo:** implementar la persistencia requerida para el CRUD y las transacciones de stock.
- **Archivo:** `db/01_esquema.sql`.
- **Seguridad:** el instalador usa `CREATE DATABASE IF NOT EXISTS` y `CREATE TABLE IF NOT EXISTS`; no ejecuta `DROP` ni `DELETE`.
- **Estado:** **ejecutado y verificado en MySQL Workbench por el estudiante el 11/11/2026**. Se observaron resultados correctos de `CREATE TABLE` y `SHOW CREATE TABLE`, se listaron exactamente `categoria` y `producto`, y se confirmó que ambas tienen 0 registros. No se ha probado aún la conexión desde Java.
- **Reglas de aplicación pendientes:** comprobar categoría activa al asignar productos y prohibir baja lógica de categorías con productos activos mediante DAO/servicios.
- **Evidencia:** capturas de ejecución en MySQL Workbench compartidas durante la revisión.


### DEC-008 — Diagnóstico inicial JDBC (11/11/2026)
- **Decisión:** leer `src/META-INF/persistence.xml` con DOM del JDK en `config` y abrir conexiones con `java.sql` únicamente desde `dao`.
- **Implementado:** `ConfiguracionDB`, `ConexionDB`, `ConexionService` y `PersistenciaException`; al iniciar se comprueba la conexión, y la opción 5 muestra unidad, URL y driver sin mostrar contraseña.
- **Seguridad:** el repositorio publica solo `CAMBIAR_LOCALMENTE`. La credencial real se editará exclusivamente en `bin/META-INF/persistence.xml`, que está ignorado por Git; no debe guardarse en `src` ni compartirse por chat.
- **Estado:** código publicado; instalación de driver indicada por el estudiante, pero ejecución JDBC y diagnóstico en su equipo **pendientes de prueba**. CRUD de categorías/productos y transacciones aún no implementados.
- **Commits:** `Agregar configuración XML y conexión JDBC con manejo de errores`, `Integrar diagnóstico JDBC al inicio y opción de configuración`.


## 11/11/2026 — Implementación de categorías con JDBC

### DEC-009 — CRUD de Categoría
- **Código publicado:** `GenericDAO<T>`, `CategoriaDAO`, `CategoriaService`, `MenuCategorias`, `CategoriaDuplicadaException` e integración de la opción 1 del menú principal.
- **Reglas previstas:** listar categorías activas, crear con ID generado, evitar nombres duplicados mediante consulta SQL, editar conservando campos vacíos, realizar baja lógica e impedirla si existen productos activos asociados.
- **Arquitectura:** consultas SQL y `java.sql` en DAO; servicio separado de consola.
- **Estado:** código publicado, **compilación y prueba JDBC en el equipo del estudiante pendientes**.
- **Prueba anterior:** la opción 5 mostró la configuración XML; para verificar el acceso real aún debe observarse el mensaje de conexión exitosa al arrancar.
- **Commits:** `Implementar CRUD de categorías con JDBC y menú de consola` y `Habilitar gestión de categorías desde el menú principal`.


### Validación de conexión y revisión de persistencia — 11/11/2026
- **JDBC:** el estudiante mostró el mensaje `Conexión a MySQL verificada correctamente.` y el menú Categorías funcionando.
- **CRUD de categorías:** el estudiante informó altas, listados, ediciones y bajas operativas.
- **Persistencia tras reinicio:** confirmada por el estudiante. Una categoría creada y no eliminada permaneció en el listado tras cerrar y volver a abrir el programa.
- **Estado de categorías:** alta, listado, edición, baja y persistencia fueron probados; quedan validaciones de borde, duplicados y bloqueo de baja con productos activos para la auditoría final.


### DEC-010 — Gestión JDBC de Producto (11/11/2026)
- **Implementado y publicado:** `ProductoDAO`, `ProductoService` y `MenuProductos`; opción 2 del menú habilitada.
- **Casos de uso:** listar activos, listar por categoría cargando `Categoria.productos`, crear con `idCategoria`, editar datos propios, reasignar categoría mediante operación separada, baja lógica.
- **Decisión sobre interfaz genérica:** `GenericDAO` conserva listado, búsqueda, actualización y baja; cada DAO define su alta porque Producto exige un ID de categoría adicional. Se retiró `@Override` de `CategoriaDAO.insertar`.
- **Restricciones:** SQL parametrizado solo en DAO, categoría activa obligatoria, precio/stock no negativos mediante validaciones de la entidad. La columna `categoria_id` no aparece en Producto.java.
- **Estado:** código publicado en GitHub. **Compilación y pruebas con JDBC en la computadora del estudiante pendientes**, sin afirmar funcionalidad verificada.
- **Commits:** `Implementar operaciones JDBC de productos y adaptar contrato DAO`, `Agregar servicio y menú de gestión de productos`, `Habilitar el menú de productos desde Main`.

### DEC-011 — Validaciones básicas y preparación de carga limpia (11/11/2026)
- **Problema detectado en consola:** el alta de Producto admitía valores compuestos solo por números en campos textuales.
- **Cambio implementado:** `ValidacionTexto` comprueba campos obligatorios, límite de caracteres, caracteres de control y presencia de letras; se integró a Categoría, Producto y nombres/apellidos de Usuario. El menú vuelve a pedir los campos textuales inválidos.
- **Criterio:** nombres como `7UP` se admiten, pero `2323` o cadenas de símbolos no se aceptan como nombre o descripción. Precio y stock siguen admitiendo números válidos. Imagen es opcional.
- **Estado:** código publicado en GitHub; pendiente la prueba de esta nueva versión en el equipo del estudiante.
- **Preparación de datos:** el estudiante confirmó que limpió `foodstore_prog2` para volver a cargar categorías y productos desde Java. La consulta de recuentos posterior a la limpieza no fue mostrada en esta conversación.
- **Próximas pruebas:** `git pull`, compilar, probar rechazo de campos numéricos, crear categorías reales, registrar productos y verificar persistencia.
- **Commits de código:** `Validar campos de texto en categorías, productos y usuarios` y `Mejorar la validación inmediata de nombres en el menú de usuarios`.

### Verificación local de validaciones y categorías — 11/11/2026
- El estudiante confirmó que la nueva versión rechaza nombres y descripciones numéricos y permite reingresar texto válido.
- El estudiante confirmó el alta y listado correctos de cuatro categorías de ejemplo desde el menú Java.
- **Estado:** pruebas locales positivas de los casos indicados; faltan pruebas de límites, mensajes de error adicionales y confirmación visual de las asociaciones de productos.
- **Siguiente paso:** cargar productos desde el menú JDBC, comprobar asociación por categoría, persistencia, edición, reasignación y baja lógica; no cargar datos desde Workbench.


### Carga del catálogo y corrección pendiente — 11/11/2026
- **Carga informada por el estudiante:** ocho productos ingresados desde la aplicación en las cuatro categorías previamente creadas.
- **Validaciones:** el estudiante informó resultados satisfactorios de las pruebas realizadas sobre la carga de productos. No se considera verificada por ello toda la batería de edición, reasignación, baja lógica y persistencia después de reiniciar.
- **Dato a corregir:** Flan casero se cargó con precio 10, técnicamente válido pero distinto del precio de demostración previsto (4000).
- **Acción indicada:** modificar únicamente el precio a 4000 mediante Productos > Editar; conservar los demás campos y verificar el listado. Corrección aún pendiente de confirmación.
- **Estado:** carga y validaciones informadas como satisfactorias; auditoría final de las historias de Producto y pedidos todavía pendiente.

### DEC-012 — Pedidos en memoria y transacciones JDBC (11/11/2026)
- **Alcance:** HU-PED-01/02/03/04. Nuevo `PedidoRepository` que recorre la asociación Usuario -> Pedido y genera IDs en memoria sin agregar atributos inversos. `PedidoService` coordina creación, edición, confirmación, cancelación y baja lógica.
- **Transacción:** `StockDAO` concentra el SQL. Usa `setAutoCommit(false)`, actualizaciones parametrizadas condicionadas a stock suficiente y disponibilidad, `commit` únicamente cuando se actualizaron todos los productos; ejecuta `rollback` ante errores. Los pedidos se registran en memoria solo después de confirmar la transacción.
- **Reposición:** al cancelar o dar de baja un pedido confirmado se repone stock por JDBC. Un pedido ya cancelado no provoca una segunda reposición. Un pedido terminado se considera cumplido y su baja lógica no repone unidades.
- **Interfaz:** `MenuPedidos` permite listar, filtrar por usuario, crear borrador, agregar/editar/eliminar detalles antes de confirmar, ver resumen, cambiar estado/pago y eliminar. Los usuarios dados de baja conservan acceso a su historial.
- **Pendiente:** código publicado en GitHub; **compilación, prueba de stock y rollback en la computadora del estudiante todavía no verificadas**. Se ejecutarán pruebas positivas y negativas antes de marcar historias como completadas.
- **Commits:** `Implementar repositorio de pedidos y transacciones de stock`, `Agregar menú de pedidos y operaciones de detalles`, `Integrar pedidos en memoria con descuento de stock JDBC`, `Controlar errores de conexión en el menú de pedidos`.


### Pruebas locales de pedidos y stock — 11/11/2026
- **Creación y confirmación:** el estudiante confirmó un pedido en memoria (ID 1) con dos unidades de Hamburguesa clásica y total 17000, mediante la opción de confirmar.
- **Descuento JDBC:** stock del producto pasó de 20 a 18, comprobado por el estudiante desde el listado de productos.
- **Cancelación y reposición:** el estudiante canceló el pedido confirmado y verificó la restitución del stock original a 20 unidades.
- **Estado:** casos de descuento y reposición verificados localmente; pruebas de cancelación repetida, baja lógica e intento de confirmar con stock insuficiente/rollback siguen pendientes.


### Prueba de cancelación repetida — 11/11/2026
- El estudiante confirmó que al intentar cancelar de nuevo el pedido previamente cancelado, el sistema rechazó la modificación y el stock se mantuvo en 20 unidades.
- **Validado:** la operación no repone una segunda vez las existencias.
- **Pendiente:** baja lógica del pedido, consulta de historial y prueba controlada de rollback con más de un detalle.


### Pruebas locales — baja lógica e historial de pedidos (11/11/2026)
- El estudiante confirmó que la baja lógica de un pedido previamente cancelado funciona.
- El pedido dejó de aparecer en el listado de activos y permaneció consultable en el historial del usuario.
- El stock de Hamburguesa clásica continuó en 20 unidades, sin reposición duplicada.
- **Siguiente caso pendiente:** transacción con dos productos, error provocado antes de confirmar, comprobación de ROLLBACK y ausencia de registro en memoria.


### Prueba controlada de rollback de pedido — 11/11/2026
- Borrador con dos líneas: ID 1 Hamburguesa clásica (1 x 8500) e ID 2 Hamburguesa doble cheddar (1 x 12000); total 20500.
- Stock previo comprobado por el estudiante en Workbench: producto ID 1 = 20, producto ID 2 = 15; ambos disponibles.
- Simulación temporal: el estudiante modificó el stock del producto ID 2 a cero.
- Desde PowerShell intentó confirmar el borrador. La aplicación informó stock insuficiente del producto ID 2 y rechazó la confirmación.
- Consulta posterior en Workbench: ID 1 = 20 e ID 2 = 0. **ROLLBACK de descuentos verificado:** no hubo descuento parcial del primer producto.
- **Pendientes de comprobación:** restaurar stock del ID 2 a 15; verificar que el pedido fallido no aparece en la lista de pedidos activos ni en el historial.


### Validación final del caso de rollback y memoria — 11/11/2026
- El estudiante restauró el stock de prueba a sus valores originales: Hamburguesa clásica (ID 1) = 20 y doble cheddar (ID 2) = 15.
- Tras la confirmación fallida por stock insuficiente, en el menú de pedidos se comprobó que **no existen pedidos activos en memoria**.
- En el historial del usuario ID 1 solo aparece el pedido anterior, ID 1, CANCELADO y con BAJA LÓGICA, total $17000, detalle de dos Hamburguesas clásicas señalado [DETALLE ELIMINADO].
- El pedido rechazado de $20500 **no aparece en el historial**. Resultado: rollback completo, sin registro indebido en memoria.
- **Próxima prueba:** reinicio y comprobación de persistencia diferencial: catálogo en MySQL sí, usuarios/pedidos en memoria no.


### Reinicio de la aplicación: colecciones en memoria — 11/11/2026
- Tras cerrar y volver a ejecutar el programa, el estudiante comprobó que **no hay usuarios ni pedidos en memoria**.
- Resultado validado para la parte no persistida del diseño mixto.
- Pendiente de comprobar tras el mismo reinicio: categorías y productos permanecen almacenados en MySQL.


### Persistencia mixta comprobada tras reinicio — 11/11/2026
- Después de salir y ejecutar nuevamente Java, el estudiante verificó que las listas de usuarios y pedidos estaban vacías.
- En la misma ejecución reiniciada, confirmó que permanecían las **4 categorías** y los **8 productos** del catálogo JDBC.
- Stocks verificados tras reinicio: Hamburguesa clásica (ID 1) = **20** y Hamburguesa doble cheddar (ID 2) = **15**.
- **Resultado validado:** entidades Usuario y Pedido en memoria volátil; Categoria y Producto persistidos en MySQL.
- **Siguiente prueba pendiente:** impedir la baja lógica de una categoría que conserva productos activos.


### Integridad de categoría con productos activos — 11/11/2026
- Desde Categorías > Eliminar (baja lógica), el estudiante intentó dar de baja la categoría Hamburguesas con dos productos activos.
- La aplicación rechazó la baja con el control de integridad previsto.
- Confirmó que la categoría seguía apareciendo en el listado de activas.
- **Validado:** bloqueo de baja lógica de categoría con productos vinculados.
- **Próxima prueba:** filtro de productos por categoría y reasignación controlada.


### Consulta de productos por categoría — 11/11/2026
- El estudiante comprobó desde Productos > Listar productos de una categoría que al seleccionar Hamburguesas se muestran exclusivamente Hamburguesa clásica y Hamburguesa doble cheddar, sin productos de otras categorías.
- **Validado:** filtro de productos activos por categoría y asociación de catálogo.
- Pendiente: probar reasignación temporal de un producto entre categorías y volver a su categoría original.


### Reasignación de producto entre categorías
- El estudiante comprobó desde el menú la reasignación de Agua mineral 500 ml (ID 6) desde Bebidas a Postres.
- Verificó que el producto aparecía al consultar la categoría Postres.
- Devolvió el producto a Bebidas y verificó que el catálogo quedó restaurado.
- **Validado:** HU-PROD-04, actualización controlada de la categoría de un producto sin pérdida de datos.


### Verificación de ID inexistente en el menú de pedidos
- Tras actualizar y recompilar, el estudiante consultó el historial usando el ID de usuario inexistente 999.
- La aplicación informó el error mediante un mensaje controlado y regresó al menú, sin cerrarse ni mostrar stack trace.
- **Validado:** captura de `EntidadNoEncontradaException` en `MenuPedidos`.


### Verificación de unicidad de categoría activa
- El estudiante intentó crear otra categoría con el nombre existente **Hamburguesas** y una descripción de prueba.
- El sistema rechazó el duplicado mediante un mensaje controlado.
- El listado continuó mostrando las cuatro categorías originales sin registros duplicados.
- **Validado:** regla de unicidad entre categorías activas.


### Rechazo de stock negativo en edición de producto
- El estudiante intentó editar Hamburguesa clásica (ID 1), dejando los demás campos sin cambios e ingresando stock `-5`.
- La aplicación rechazó el valor inválido y el listado posterior conservó el stock de **20** unidades.
- **Validado:** no se almacena stock negativo ni se altera el registro ante este error.


### Comprobación de modificación y baja lógica de detalles
- Se inició un borrador con dos Hamburguesas clásicas a $8500 cada una.
- El estudiante modificó la cantidad de 2 a 3; la pantalla confirmó un total de **$25500** (captura aportada).
- También confirmó que quitar el detalle funcionó y que el resumen volvió a quedar sin detalles activos (total $0).
- **Validado:** modificación de cantidad, recálculo de total y baja lógica de detalles de un pedido sin confirmar.

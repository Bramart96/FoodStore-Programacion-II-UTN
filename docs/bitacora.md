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

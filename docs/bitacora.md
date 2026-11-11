# Bitácora técnica — Food Store, Programación II

## 11/11/2026 — Inicio del desarrollo

**Contexto:** Bases de Datos I es un trabajo académico independiente. Para mantener coherencia se revisaron `sql/01_esquema.sql` y el informe técnico de ese repositorio, que documentan MySQL 8 y las tablas `categoria` y `producto`.

### DEC-001 — Plataforma y persistencia
- **Decisión:** Java 21 y JDBC puro conforme a consigna; MySQL 8 como motor de trabajo, con una base propia para Programación II.
- **Justificación:** respeta el alcance y conserva coherencia en los nombres y restricciones con el TFI de Bases de Datos I.
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


## 11/11/2026 — Validación local del CRUD de usuarios y revisión del anexo

### Pruebas informadas por el estudiante
- Clonación/actualización, compilación con `javac --release 21` y ejecución de `Main`: correctas en Windows PowerShell.
- Creación de usuario en memoria: se asignó ID 1 y se pudo listar.
- Alta con email repetido: rechazada mediante mensaje controlado, sin cerrar el programa.
- Modificación del email de un usuario existente: informada como correcta.
- Baja lógica confirmada: mensaje de éxito y retorno al menú.
- Nueva ejecución de `Main`: la lista de usuarios apareció vacía, como exige la persistencia en memoria.
- **Límites de la verificación:** no se mostró en la transcripción un listado inmediatamente después de la baja ni un intento de crear un pedido para un usuario eliminado. La consulta de historial de pedidos de usuarios eliminados tampoco está implementada todavía.
- **Datos personales de prueba:** omitidos deliberadamente de esta bitácora.

### Seguimiento del backlog
- Se revisó el anexo oficial completo: 20 historias en 5 épicas (4 de categorías, 5 de productos, 4 de usuarios, 4 de pedidos y 3 de configuración).
- Se creó `docs/matriz_historias.md` para traducir los criterios de aceptación a opciones del menú y registrar estados reales.
- **Estado:** 3 historias de usuario verificadas parcialmente/totalmente según los casos registrados, 4 con implementación parcial y 13 pendientes. HU-USR-04 sigue parcial hasta resolver acceso al historial y verificar la exclusión de usuarios eliminados en la creación de pedidos.
- Próxima prioridad: persistencia JDBC de categorías/productos y posteriormente integración de pedidos y control de stock.

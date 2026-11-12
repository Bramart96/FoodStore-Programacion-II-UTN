# Matriz de historias de usuario — Food Store

Correspondencia entre las historias del TPI, su implementación y la evidencia funcional disponible. **Implementado** no equivale automáticamente a **probado**: los casos de borde no comprobados se identifican por separado.

| Historia | Funcionalidad | Evidencia |
|---|---|---|
| CAT-01 | Listar categorías activas | Listado comprobado |
| CAT-02 | Crear categoría y validar unicidad | Alta, ID, persistencia y rechazo de duplicado comprobados |
| CAT-03 | Editar categoría | Edición comprobada |
| CAT-04 | Baja lógica de categoría | Bloqueo con productos activos comprobado |
| PROD-01 | Listar productos activos | Catálogo comprobado |
| PROD-02 | Consultar por categoría | Filtro Hamburguesas comprobado |
| PROD-03 | Crear producto | Alta y datos del catálogo comprobados |
| PROD-04 | Editar y reasignar categoría | Modificación y reasignación temporal comprobadas; stock negativo rechazado |
| PROD-05 | Baja lógica de producto | Implementada; caso de producto vinculado a pedido histórico pendiente de prueba aislada |
| USR-01 | Listar usuarios | Comprobado |
| USR-02 | Crear usuario y correo único | Alta, ID y rechazo de duplicado comprobados |
| USR-03 | Editar usuario | Edición de correo e ID inexistente comprobados |
| USR-04 | Baja lógica de usuario | Baja e historial de pedidos de un usuario eliminado comprobados |
| PED-01 | Listar y consultar pedidos por usuario | Lista activa e historial comprobados |
| PED-02 | Crear pedido con detalles | Agregar, modificar, quitar, calcular y confirmar comprobados; rollback real comprobado |
| PED-03 | Cambiar estado y forma de pago | Pago y cancelación comprobados; estado TERMINADO pendiente de prueba aislada |
| PED-04 | Baja lógica de pedido | Baja de pedido cancelado y conservación de historial comprobadas |
| CFG-01 | Mostrar unidad y URL JDBC | Funcionalidad implementada y consultada en el entorno de pruebas |
| CFG-02 | Diagnosticar conexión y errores | Inicio con MySQL y error de driver sin cierre inesperado comprobados |
| CFG-03 | Script SQL y ejecución | Esquema de dos tablas instalado y ejecución con javac/java comprobados |

## Criterios transversales

- Modelo y navegación unidireccional conforme al UML.
- `Categoria` y `Producto` operan con JDBC y siguen disponibles tras reiniciar.
- `Usuario`, `Pedido` y `DetallePedido` se gestionan con colecciones en memoria.
- Las operaciones de stock se ejecutan transaccionalmente; el rollback no registra pedidos fallidos.
- Las excepciones previsibles se comunican sin mostrar stack traces.
- El archivo SQL solo crea las tablas `categoria` y `producto`.

Las comprobaciones de borde pendientes no invalidan las pruebas principales ya realizadas; se conservan identificadas para la revisión final.

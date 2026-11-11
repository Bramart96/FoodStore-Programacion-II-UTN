# Matriz de trazabilidad — Historias de usuario Food Store

**Materia:** Programación II (UTN). **Referencia:** `Consigna_TPI_Prog-2_2026_JDBC.pdf`, anexo, páginas 10–16.

El anexo es el backlog mínimo. **Cada historia debe corresponder a una operación concreta de consola, con sus validaciones**, no solo a una clase Java. Estados: **verificada** (se observaron pruebas pertinentes), **parcial** (código del modelo o parte de la operación, faltan criterios) o **pendiente**.

| HU | Opción / caso de uso requerido | Estado al 11/11/2026 | Falta comprobar o implementar |
|---|---|---|---|
| CAT-01 | Categorías > Listar activas [BD] | Parcial | Código JDBC publicado; prueba local pendiente |
| CAT-02 | Categorías > Crear [BD] | Parcial | Probar alta, ID generado, duplicado y persistencia |
| CAT-03 | Categorías > Editar [BD] | Parcial | Probar edición, ID inválido y conservación de campos |
| CAT-04 | Categorías > Eliminar [BD] | Parcial | Probar baja lógica y bloqueo con productos activos |
| PROD-01 | Productos > Listar activos [BD] | Pendiente | Datos completos y consulta JDBC sin categoría inversa |
| PROD-02 | Productos > Listar por categoría [BD] | Pendiente | Lista de categorías, consulta FK y agregación Categoria → Productos |
| PROD-03 | Productos > Crear [BD] | Pendiente | Campos obligatorios, categoría activa, validaciones, INSERT e ID |
| PROD-04 | Productos > Editar/reasignar categoría [BD] | Pendiente | UPDATE de atributos propios separado del cambio de categoria_id |
| PROD-05 | Productos > Eliminar [BD] | Pendiente | Baja lógica, no afectar detalles históricos |
| USR-01 | Usuarios > Listar [MEMORIA] | Verificada | Se observó listado con ID y datos; volver a probar si cambia UI |
| USR-02 | Usuarios > Crear [MEMORIA] | Verificada | ID generado, controles de formato y duplicado (se observó rechazo de duplicado) |
| USR-03 | Usuarios > Editar [MEMORIA] | Verificada para email | Se observó modificación de email; otros campos siguen sin prueba manual específica |
| USR-04 | Usuarios > Eliminar [MEMORIA] | Parcial | Baja lógica ejecutada; falta comprobar ocultamiento inmediato, impedir nuevos pedidos y consulta histórica de pedidos de eliminados |
| PED-01 | Pedidos > Listar y filtrar por usuario [MEMORIA] | Pendiente | Recorrer Usuario → Pedido; líneas, fecha, total, estado y filtros |
| PED-02 | Pedidos > Crear con detalles [MEMORIA + BD] | Parcial | Métodos del modelo presentes; falta menú, usuario activo, consulta de productos, resumen y transacción de stock |
| PED-03 | Pedidos > Editar estado/pago [MEMORIA + BD] | Parcial | Métodos de entidad presentes; falta menú, servicios, validación de estados y reposición transaccional |
| PED-04 | Pedidos > Eliminar [MEMORIA + BD] | Parcial | Bajas lógicas del modelo presentes; falta servicio, menú y reposición de stock en BD |
| CFG-01 | Configuración > Consultar unidad y URL | Pendiente | Parseo DOM de persistence.xml sin URL hardcodeada |
| CFG-02 | Diagnóstico de conexión al iniciar | Pendiente | Driver, conexión y mensajes sin stack trace |
| CFG-03 | Instalar desde script SQL y README | Pendiente | `db/` con solamente categoria y producto, manual de reproducción |

**Resumen a 11/11/2026:** 20 historias: 3 verificadas en flujos concretos (USR-01/02/03), 8 parciales (CAT-01/02/03/04, USR-04 y PED-02/03/04) y 9 pendientes. Las 4 historias de categorías requieren pruebas locales de JDBC. **No significa que 3 historias estén auditadas al 100 % de todos los criterios:** la edición de los distintos campos y los errores de entrada deben repetirse en una batería final.

## Pruebas realizadas desde PowerShell — 11/11/2026

Se informó y mostró: alta con ID, listado, rechazo de un correo repetido, modificación de correo, baja lógica y reinicio de la aplicación con lista vacía. Los mensajes se mostraron sin cerrar inesperadamente el programa.

No se guardan emails, teléfonos ni contraseñas usadas en la prueba. Los pedidos y el acceso a historial no fueron comprobados.

## Control de aceptación para el cierre del TPI

- Todas las opciones del anexo deben estar disponibles y operativas desde consola.
- El menú identifica `[BASE DE DATOS]` y `[MEMORIA]`.
- CRUD de Categoria y Producto mediante JDBC: persisten después de cerrar/reabrir.
- Usuario/Pedido/DetallePedido solo en memoria: se pierden después de cerrar/reabrir.
- Pedidos y stock se coordinan mediante una única transacción JDBC; rollback ante fallo.
- Ninguna entidad incorpora relaciones inversas contrarias al UML.
- El repositorio público, script SQL, README, informe PDF, video demostrativo y ZIP son excluyentes para la entrega.

**Nota:** La documentación del uso de IA se incorporará en la etapa de cierre; esta matriz registra únicamente alcance funcional y pruebas.

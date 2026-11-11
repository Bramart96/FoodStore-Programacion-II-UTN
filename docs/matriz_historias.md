# Matriz de trazabilidad — Historias de usuario Food Store

**Materia:** Programación II (UTN). **Referencia:** `Consigna_TPI_Prog-2_2026_JDBC.pdf`, anexo, páginas 10–16.

El anexo es el backlog mínimo. **Cada historia debe corresponder a una operación concreta de consola, con sus validaciones**, no solo a una clase Java. Estados: **verificada** (se observaron pruebas pertinentes), **parcial** (código del modelo o parte de la operación, faltan criterios) o **pendiente**.

| HU | Opción / caso de uso requerido | Estado al 11/11/2026 | Falta comprobar o implementar |
|---|---|---|---|
| CAT-01 | Categorías > Listar activas [BD] | Parcial | Código JDBC publicado; prueba local pendiente |
| CAT-02 | Categorías > Crear [BD] | Parcial | Probar alta, ID generado, duplicado y persistencia |
| CAT-03 | Categorías > Editar [BD] | Parcial | Probar edición, ID inválido y conservación de campos |
| CAT-04 | Categorías > Eliminar [BD] | Parcial | Probar baja lógica y bloqueo con productos activos |
| PROD-01 | Productos > Listar activos [BD] | Parcial | Código publicado; falta prueba local del listado |
| PROD-02 | Productos > Listar por categoría [BD] | Parcial | Código publicado; falta probar selección y agregación unidireccional |
| PROD-03 | Productos > Crear [BD] | Parcial | Código publicado; probar creación, categoría activa, errores e ID |
| PROD-04 | Productos > Editar/reasignar categoría [BD] | Parcial | Código publicado; probar edición y reasignación por separado |
| PROD-05 | Productos > Eliminar [BD] | Parcial | Código publicado; probar baja e historial de detalles cuando haya pedidos |
| USR-01 | Usuarios > Listar [MEMORIA] | Verificada | Se observó listado con ID y datos; volver a probar si cambia UI |
| USR-02 | Usuarios > Crear [MEMORIA] | Verificada | ID generado, controles de formato y duplicado (se observó rechazo de duplicado) |
| USR-03 | Usuarios > Editar [MEMORIA] | Verificada para email | Se observó modificación de email; otros campos siguen sin prueba manual específica |
| USR-04 | Usuarios > Eliminar [MEMORIA] | Parcial | Baja lógica ejecutada; falta comprobar ocultamiento inmediato, impedir nuevos pedidos y consulta histórica de pedidos de eliminados |
| PED-01 | Pedidos > Listar y filtrar por usuario [MEMORIA] | Parcial | Menú y recorrido de usuarios implementados; falta prueba local con pedidos activos y usuarios dados de baja |
| PED-02 | Pedidos > Crear con detalles [MEMORIA + BD] | Parcial | Menú y StockDAO transaccional implementados; falta prueba de confirmación, stock y rollback |
| PED-03 | Pedidos > Editar estado/pago [MEMORIA + BD] | Parcial | Servicio y menú implementados; falta comprobar estados terminales y reposición al cancelar |
| PED-04 | Pedidos > Eliminar [MEMORIA + BD] | Parcial | Servicio y menú implementados; falta probar baja, detalles históricos y reposición una sola vez |
| CFG-01 | Configuración > Consultar unidad y URL | Pendiente | Parseo DOM de persistence.xml sin URL hardcodeada |
| CFG-02 | Diagnóstico de conexión al iniciar | Pendiente | Driver, conexión y mensajes sin stack trace |
| CFG-03 | Instalar desde script SQL y README | Pendiente | `db/` con solamente categoria y producto, manual de reproducción |

**Resumen (11/11/2026):** 20 historias controladas por la matriz. Código de HU-PED-01/02/03/04 publicado pero pendiente de prueba local; las historias no se declaran terminadas hasta verificar sus casos de aceptación.

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

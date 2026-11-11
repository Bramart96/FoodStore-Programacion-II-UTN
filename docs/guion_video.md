# Guion del video final — Food Store (Programación II)

**Duración exigida por la consigna:** 10 a 15 minutos. Mostrar flujo completo de consola y demostrar expresamente la diferencia entre persistencia JDBC y colecciones en memoria. Se registran resultados informados por el estudiante, no pruebas hipotéticas.

## Pruebas confirmadas al 11/11/2026

- [x] Compilación Java y arranque por PowerShell; conexión JDBC y opción de configuración.
- [x] Esquema MySQL: solamente tablas `categoria` y `producto`.
- [x] Categorías: alta, listado, edición, baja lógica, validaciones y persistencia tras reiniciar.
- [x] Usuarios en memoria: alta, listado, edición, duplicados, baja y lista vacía tras reiniciar.
- [x] Ocho productos: alta y listado; rechazo de textos numéricos; precio del Flan casero corregido a 4000.
- [x] Pedido con dos hamburguesas clásicas: total 17000; confirmación y descuento real de stock 20 -> 18.
- [x] Cancelación y reposición 18 -> 20; segunda cancelación rechazada sin modificar stock.
- [x] Baja lógica del pedido cancelado, ausencia del listado activo, historial conservado y stock intacto.
- [x] Borrador preparado para rollback: hamburguesa clásica ID 1 (1 unidad) + doble cheddar ID 2 (1 unidad), total 20500; stock previo verificado en Workbench: ID 1 = 20, ID 2 = 15, ambos disponibles.

## Pruebas todavía pendientes

- [ ] Forzar stock insuficiente en segundo producto, demostrar rollback íntegro y restaurar stock.
- [ ] Verificar reasignación entre categorías y bloqueo de baja con productos activos.
- [ ] Mostrar pérdida de usuario/pedido tras reinicio en el flujo de demostración final.
- [ ] Auditoría de entradas inválidas, stock, errores y estados de pedidos.

## Secuencia sugerida para grabar (ajustar tiempos)

1. Presentación del proyecto, tecnologías y arquitectura por capas.
2. Arranque y diagnóstico JDBC; base con las dos tablas permitidas.
3. CRUD de categorías y productos; asociación categoría-productos.
4. CRUD de usuarios en memoria.
5. Alta de pedido, detalle, subtotal y cálculo de total con `Calculable`.
6. Confirmación y descuento de stock, cancelación y reposición; historial y bajas lógicas.
7. Rollback comprobado en un caso controlado (una vez verificado).
8. Reiniciar: categorías y productos permanecen; usuarios y pedidos desaparecen.
9. Cierre, repositorio y documentación.

**Precauciones:** no mostrar contraseñas, emails o teléfonos reales. No crear tablas de usuario/pedido/detalle. No confundir pruebas ya realizadas con pendientes. Mostrar capturas claras del menú, de Workbench y de los resultados.


## Evidencia nueva para la demostración — ROLLBACK (11/11/2026)
- Borrador: hamburguesa clásica ID 1 y hamburguesa doble cheddar ID 2, una unidad de cada una, total $20500.
- Stock real al preparar el pedido: 20 y 15.
- Simulación de venta concurrente: ID 2 pasó temporalmente a stock 0 desde Workbench.
- La confirmación rechazó el pedido por stock insuficiente del ID 2.
- La consulta posterior confirmó ID 1 = 20, ID 2 = 0: transacción sin descuento parcial, rollback verificado.
- Antes de grabar, mostrar el caso con orden y sin revelar configuración de credenciales. **Pendiente**: restaurar ID 2 = 15 y comprobar que el pedido fallido no quedó en memoria.


### Caso de demostración documentado — rechazo de pedido sin registro
- Después de provocar stock insuficiente en la segunda línea, se verificó rollback en Workbench (ID 1 = 20, ID 2 = 0) y luego se restauró ID 2 = 15.
- En consola, lista de pedidos activos vacía; historial del usuario ID 1 conserva solo pedido ID 1 cancelado/de baja, total 17000; no contiene el pedido rechazado de 20500.
- Para el video explicar que la transacción de stock es atómica y que solo se incorpora un pedido al repositorio en memoria si el descuento se confirma con éxito.


### Prueba de reinicio — memoria volátil (11/11/2026)
- El estudiante cerró y reabrió la aplicación; verificó que los listados de usuarios y pedidos quedaron vacíos.
- Para la demostración final, contrastar en el mismo reinicio con categorías y productos persistentes. La segunda mitad de esta prueba está pendiente.


### Persistencia diferencial confirmada — 11/11/2026
- Tras reiniciar Java, usuarios y pedidos quedaron vacíos, mientras que el estudiante verificó el catálogo de 4 categorías y 8 productos, con stock 20 (Hamburguesa clásica) y 15 (doble cheddar).
- Para la grabación, mostrar antes/después del reinicio como evidencia del almacenamiento mixto. La comprobación funcional ya se hizo; aún hay que filmarla.


### Prueba de integridad del catálogo — 11/11/2026
- Se intentó dar de baja la categoría Hamburguesas con dos productos activos; la operación fue rechazada y la categoría siguió activa.
- Incluir como demostración breve de una validación de negocio del catálogo.


### Filtro de productos por categoría validado — 11/11/2026
- La consulta por categoría Hamburguesas mostró solamente los dos productos correspondientes. Preparar para el video una demostración breve del filtro y la asociación unidireccional Categoria -> Productos.


## PLAN DEFINITIVO DE GRABACIÓN — actualización docente del 11/11/2026

**Prioridad:** video público de **10 a 15 minutos**. Objetivo de edición/guion: ~12-13 minutos, dejando margen dentro del rango. **No se filma toda la batería de pruebas**: esa batería se usa para validar el desarrollo. La consigna original pide recorrer el flujo completo y demostrar de forma explícita persistencia: crear una categoría y un producto, cerrar Java, volverlo a abrir, comprobar que persisten y que usuarios/pedidos ya no están.

### Secuencia reducida para el video (tiempos orientativos)

| Tramo | Tiempo | Mostrar |
|---|---|---|
| 1. Introducción | 0:00–1:00 | Food Store, Java 21, POO, MySQL/JDBC y almacenamiento mixto |
| 2. Organización y conexión | 1:00–2:00 | Carpetas por capas, solo tablas categoria/producto, conexión |
| 3. Catálogo persistente | 2:00–4:30 | Crear UNA categoría y UN producto de demostración, listarlos, validación relevante |
| 4. Usuario y pedido | 4:30–8:30 | Crear usuario ficticio, agregar detalles, calcular total, confirmar, mostrar descuento de stock |
| 5. Estados e integridad | 8:30–10:00 | Cancelar/reponer stock; baja lógica o excepción controlada como muestra |
| 6. Reinicio obligatorio | 10:00–12:30 | Salir y volver a iniciar; categorías/productos presentes, usuario/pedidos ausentes |
| 7. Cierre | 12:30–13:00 | Qué se demostró; documentación del código en repositorio |

**Casos que deben estar validados en desarrollo pero no hace falta filmar completos:** rollback forzado desde Workbench, cancelación repetida, consultas detalladas de historial, filtro y reasignación de categoría, entradas inválidas repetidas. La reasignación es criterio de HU-PROD-04 y se probará brevemente fuera de cámara; no necesita espacio en el video si se presenta otro ejemplo del CRUD.

### Cambio de entregables anunciado por correo docente — 11/11/2026

El correo de ARIEL ENFERREL (vía TUP), aportado en captura por el estudiante, especifica **únicamente**:
1. Un archivo **.zip** con el código fuente completo para adjuntar en la plataforma.
2. **Enlace público al video** en los comentarios de la entrega.

El docente aclara: **no se debe entregar informe escrito**, y **no incluir el video dentro del .zip**. Este correo es posterior a la consigna y rúbrica originales, que sí exigían PDF y hasta lo mencionaban como criterio excluyente. Para el envío en la plataforma se seguirá la comunicación más reciente; conservar captura del correo por la discrepancia. El script SQL sigue siendo parte necesaria del código completo y el repositorio debe mantenerse accesible según la rúbrica.

No añadir credenciales reales al .zip ni en pantallas del video. Preparar el video cuando el programa y el catálogo de demostración estén listos.

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

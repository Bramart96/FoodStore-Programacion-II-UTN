# Pruebas manuales de pedidos y stock

Escenarios reproducibles desde la aplicación de consola. Utilizar usuarios ficticios y anotar el stock inicial antes de cada prueba.

## Preparación

1. Confirmar la conexión MySQL al iniciar Java.
2. Verificar en Productos las existencias de los artículos elegidos.
3. Crear un usuario ficticio, ya que los usuarios no se guardan al reiniciar.
4. Acceder a Pedidos y detalles desde el menú principal.

## Escenarios

| Caso | Operación | Resultado esperado |
|---|---|---|
| PED-01 | Intentar confirmar un borrador sin detalles | Se solicita agregar al menos un detalle |
| PED-02 | Agregar un producto, modificar su cantidad y consultar el resumen | Subtotal y total recalculados |
| PED-03 | Quitar un detalle del borrador | El detalle deja de contar en el total |
| PED-04 | Confirmar un pedido válido | Se crea el pedido en memoria y MySQL descuenta exactamente las cantidades |
| PED-05 | Cambiar el pago de un pedido confirmado | Se conserva estado, cantidad y total |
| PED-06 | Cancelar un pedido confirmado | MySQL repone el stock |
| PED-07 | Intentar cancelar nuevamente un pedido cancelado | Se rechaza el cambio sin duplicar el stock |
| PED-08 | Dar de baja lógicamente un pedido cancelado | No aparece entre activos, pero sí en el historial |
| PED-09 | Forzar stock insuficiente en el segundo producto durante la confirmación | Rollback completo y ausencia de pedido registrado |
| PED-10 | Reiniciar la aplicación | Usuarios y pedidos desaparecen; productos y categorías persisten |

Los casos PED-01 a PED-10 tienen evidencia manual de sus operaciones principales en el desarrollo.

## Comprobación de rollback con dos productos

1. Preparar un borrador con dos detalles y **no confirmarlo** todavía.
2. Consultar y anotar las existencias de ambos productos en MySQL Workbench.
3. Desde una sesión SQL, cambiar temporalmente el stock del **segundo** producto a cero.
4. Intentar confirmar desde Java; la operación debe informar stock insuficiente.
5. Consultar nuevamente MySQL: el stock del primer producto debe seguir en su valor original.
6. Verificar en Pedidos que el pedido rechazado no se haya registrado en memoria.
7. Restaurar el stock original del segundo producto y verificar el resultado.

No ejecutar `DELETE`, no crear tablas adicionales y no modificar simultáneamente otros datos del catálogo. El programa usa `UPDATE` transaccionales para descontar y reponer existencias.

## Otros escenarios de borde

Pendientes de comprobación aislada: pedido marcado TERMINADO y baja lógica de producto con historial de pedidos. No es necesario mostrarlos todos en el video, pero el comportamiento debe respetar la consigna.

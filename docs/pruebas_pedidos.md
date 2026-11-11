# Pruebas manuales — Pedidos y stock (Food Store)

Este plan comprueba las historias HU-PED-01 a HU-PED-04. Registrar los resultados reales; no incluir contraseñas ni datos personales en capturas.

## Preparación
1. Ejecutar `git pull origin main` desde PowerShell y compilar con `javac --release 21`.
2. Mantener la contraseña local en `bin/META-INF/persistence.xml` (no sobrescribirla).
3. Ejecutar `java -cp "bin;lib/*" integrado.prog2.Main`.
4. Comprobar conexión con MySQL. Verificar precios y stock desde el menú Productos.
5. Crear un **usuario de prueba ficticio** desde Usuarios (los usuarios se pierden al reiniciar).

## Casos

| ID | Acción | Resultado esperado |
|---|---|---|
| PED-T01 | Entrar en Pedidos > Listar antes de crear alguno | Mensaje de listado vacío |
| PED-T02 | Crear pedido con usuario válido; agregar 2 unidades de un producto | El borrador informa subtotal y total; MySQL no cambia antes de confirmar |
| PED-T03 | Agregar un segundo producto; confirmar | Pedido ID generado, estado CONFIRMADO, productos descontados exactamente |
| PED-T04 | Reiniciar el programa tras confirmar | Productos conservan el stock actualizado; usuario y pedido ya no existen |
| PED-T05 | Crear otro pedido y cancelar por estado | Estado CANCELADO; stock se repone una sola vez |
| PED-T06 | Intentar modificar pedido CANCELADO | Se rechaza con mensaje controlado |
| PED-T07 | Crear otro pedido, luego eliminarlo lógicamente | No se lista entre activos; stock se repone y consulta de historial muestra la baja |
| PED-T08 | Eliminar un usuario con pedidos existentes | No aparece entre usuarios activos; sus pedidos se consultan por ID en historial |
| PED-T09 | Añadir cantidad 0 o mayor al stock disponible | Rechazo sin guardar pedido ni modificar stock |
| PED-T10 | Provocar falta de stock entre borrador y confirmación con dos productos | Transacción revierte todas las actualizaciones y no registra pedido (prueba controlada, después de las básicas) |

## Consultas de control en Workbench

Solo consultar; no modificar el catálogo durante las pruebas normales:

```sql
SELECT id, nombre, precio, stock, disponible, eliminado
FROM foodstore_prog2.producto
ORDER BY id;
```

En una prueba de rollback se pueden simular cambios de stock desde una segunda sesión, siempre anotando valores previos y restaurándolos después. No se debe ejecutar DELETE ni crear tablas de usuarios o pedidos.

## Estado
Código publicado; resultados funcionales del módulo de pedidos **aún pendientes de confirmar por el estudiante**.

## ROLLBACK — preparación real comprobada (11/11/2026)

El estudiante dejó preparado en consola un pedido **sin confirmar** con los productos ID 1 (Hamburguesa clásica, 1 unidad) e ID 2 (Hamburguesa doble cheddar, 1 unidad); total de borrador: **20500**. Se verificó en MySQL Workbench, antes de forzar el error:

- ID 1: stock **20**, disponible **1**.
- ID 2: stock **15**, disponible **1**.

**Aún no se ejecutó la prueba de rollback ni se modificó el stock para provocarla.** Pasos posteriores: cambiar temporalmente el stock del ID 2 a cero, confirmar el borrador, comprobar que el stock del ID 1 no disminuye y que no se guarda pedido en memoria, y restaurar ID 2 a 15. Usar UPDATE restringido por ID y valor anterior, verificar filas afectadas. No repetir confirmación antes de restaurar los valores.


## Resultado manual de PED-T10 — 11/11/2026
- Pedido de prueba: una unidad del producto 1 ($8500) y una del producto 2 ($12000), total $20500.
- Stocks de partida en MySQL: ID 1 = 20; ID 2 = 15.
- Se cambió temporalmente el stock de ID 2 a 0 en Workbench.
- El estudiante intentó confirmar; el programa comunicó stock insuficiente del ID 2 y rechazó la confirmación.
- La consulta de Workbench posterior mostró ID 1 = 20 e ID 2 = 0; el primer descuento fue revertido.
- **Comprobado:** rollback sin descuentos parciales.
- **Aún pendiente:** restauración del ID 2 a 15 y ausencia del pedido fallido en memoria.


### Cierre de PED-T10 — comprobación en menú
- Se restauró el stock temporal del producto ID 2 a 15 (ID 1 se mantuvo en 20).
- Captura de PowerShell aportada por el estudiante: mensaje "No hay pedidos activos en memoria".
- En consulta por usuario ID 1 aparece únicamente el pedido anterior ID 1 [BAJA LÓGICA], estado CANCELADO, total 17000, con detalle histórico.
- No aparece el pedido fallido de 20500.
- **PED-T10 validado en JDBC y en memoria.**

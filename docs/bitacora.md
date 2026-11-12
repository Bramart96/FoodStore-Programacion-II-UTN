# Decisiones técnicas y pruebas — Food Store

Este documento resume decisiones de diseño y verificaciones del proyecto, sin registrar datos privados de las pruebas.

## Arquitectura

- **Modelo:** cinco entidades que heredan de `Base` (`id`, `eliminado`, `createdAt`), enumeraciones `Rol`, `Estado` y `FormaPago`, e interfaz `Calculable`.
- **Relaciones:** `Categoria → Productos`, `Usuario → Pedidos`, `Pedido → Detalles`, `DetallePedido → Producto`. No se añaden referencias inversas.
- **Persistencia:** categorías y productos se conservan en MySQL; usuarios, pedidos y detalles se gestionan exclusivamente en memoria.
- **Capas:** el SQL se concentra en `dao`; `service` implementa reglas de negocio; `repository` administra las colecciones; `ui` interactúa por consola.
- **Configuración:** `persistence.xml` se lee con DOM del JDK; su uso como formato de propiedades no implica utilizar JPA.
- **Bajas:** se usa `eliminado` en lugar de borrado físico para conservar los registros y el historial.

## Integridad y transacciones

- El nombre de una categoría debe ser único entre las activas, validado por SQL.
- Una categoría con productos activos no puede darse de baja.
- Los productos necesitan una categoría activa, precio válido y stock no negativo.
- Los pedidos requieren un usuario activo; los detalles necesitan productos disponibles y cantidades positivas que no excedan el stock.
- La confirmación ejecuta los descuentos en una sola transacción JDBC con `setAutoCommit(false)`, `commit` y `rollback` ante error.
- Un pedido no se registra en memoria si la confirmación falla; una cancelación repone stock solamente una vez.
- Los pedidos cancelados o terminados no admiten nuevas modificaciones.

## Verificaciones funcionales realizadas

| Área | Verificación |
|---|---|
| Categorías | Alta, listado, edición, persistencia, rechazo de duplicados y bloqueo de baja con productos activos |
| Productos | Carga del catálogo, edición, validación de stock negativo, filtro por categoría y reasignación con restauración |
| Usuarios | Alta, listado, edición, unicidad del correo, baja lógica, historial de pedidos de un usuario eliminado, manejo de ID inexistente y vaciado tras reinicio |
| Detalles | Alta, cambio de cantidad, recálculo del total y baja lógica en borrador |
| Pedidos | Confirmación, descuento de stock, actualización de forma de pago, cancelación, baja lógica e historial |
| Transacciones | Fallo controlado en el segundo producto, reversión del primer descuento y ausencia de pedido fallido en memoria |
| Reinicio | Catálogo persistente en MySQL; usuarios y pedidos ausentes después de reiniciar Java |
| Errores | Opciones fuera de rango, entradas no numéricas, IDs inexistentes y ausencia de driver con mensajes controlados |

## Comprobaciones de cierre

Los recorridos principales se probaron en el equipo de desarrollo y el código se recompiló con `javac --release 21`. Quedan como controles de borde específicos, sin considerarlos previamente demostrados: la finalización de un pedido y la baja de un producto vinculado a un pedido histórico.

La preparación del video y del archivo ZIP corresponde a la etapa de entrega; no modifica el diseño de la aplicación.

## Documentación del código

- Las clases y métodos que explican reglas de negocio importantes se documentan con JavaDoc en español.
- Los comentarios internos se reservan para decisiones que no son evidentes a simple vista, como la transacción JDBC o la dirección de relaciones del UML.
- Los mensajes de consola describen el resultado de cada operación sin mostrar excepciones técnicas innecesarias ni contraseñas.
- La documentación no cambia la lógica ni sustituye las pruebas funcionales.

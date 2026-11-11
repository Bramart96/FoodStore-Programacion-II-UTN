# Food Store — Programación II (UTN)

Trabajo Práctico Integrador de Programación II: aplicación de consola con Java 21, POO y JDBC puro.

## Alcance

- `Categoria` y `Producto`: persistencia en base de datos relacional mediante JDBC.
- `Usuario`, `Pedido` y `DetallePedido`: colecciones en memoria.
- Bajas lógicas, excepciones propias, cálculo de pedidos y transacciones para stock.
- Sin Maven, Gradle, JPA ni Hibernate.

Este repositorio es **exclusivo de Programación II**. Bases de Datos I utiliza el mismo dominio en otro repositorio académico, sin compartir código ni base de datos de ejecución.

## Estado del desarrollo — 11/11/2026

**Implementado y compilado en entorno de asistencia:** `Base`, enums `Rol`, `Estado`, `FormaPago`, interfaz `Calculable`, las cinco entidades (`Categoria`, `Producto`, `Usuario`, `Pedido`, `DetallePedido`), excepciones de precio y stock y Main provisional. Las entidades se probaron en el entorno de asistencia; falta prueba local.

**Pendiente:** DAO, conexión JDBC, repositorios para generar IDs y controlar unicidad, servicios y transacciones de stock, menús y pruebas integradas. Las tres entidades de memoria ya están codificadas, pero aún no constituyen un CRUD funcional. No se afirma que el sistema ya gestione pedidos.

## Ejecutar el bloque inicial

Requiere JDK 21. Desde la raíz, en terminal Bash o Git Bash:

```bash
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name '*.java')
java -cp bin integrado.prog2.Main
```

En etapas siguientes agregaremos el archivo `src/META-INF/persistence.xml`, el driver JDBC `.jar` en `lib/`, el script SQL propio en `db/` y las instrucciones definitivas para Windows/NetBeans.

## Documentación y decisiones

Ver [`docs/bitacora.md`](docs/bitacora.md) para distinguir decisiones, archivos implementados, pruebas ejecutadas y tareas pendientes. El informe PDF y el video demostrativo se prepararán con evidencias reales, antes de la entrega.

**Seguridad:** nunca subir credenciales reales ni archivos locales sensibles.

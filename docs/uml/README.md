# Secuencias actualizadas

Estas secuencias describen el código actual. Complementan los originales de las páginas físicas 13 y 14 de la Entrega 1, que permanecen intactos en el PDF.

- [Crear reserva](CrearReserva.mmd).
- [Registrar pagos](RegistrarPagos.mmd).

Los archivos son texto editable en Mermaid: participantes, mensajes, retornos y fragmentos combinados `alt`, `opt`, `loop` y `break`. Un `break` indica que, si se cumple su condición, se interrumpe el resto de la interacción. Las excepciones se muestran propagadas hasta el menú, omitiendo los retornos intermedios.

Se omiten getters, detalles del lector de consola y llamadas internas de archivos para que el flujo se pueda leer. Las notas identifican las operaciones resumidas. Los participantes Reserva, Pasajero y Pago representan instancias creadas durante la operación; los mensajes `new` señalan su construcción. No representan objetos recuperados previamente, salvo la reserva buscada en la secuencia de pagos.

No hace falta instalar Mermaid para compilar ni ejecutar Java. Para visualizar o exportar SVG/PNG, abrir el contenido en un visor compatible con diagramas de secuencia Mermaid. No se generaron imágenes en esta revisión porque no se encontró un renderizador local disponible; las fuentes se contrastaron con Java, pero su representación gráfica queda por revisar al exportarlas.

## Cambios respecto de las secuencias originales

Revisión del 04/11/2026: se inspeccionaron participantes, mensajes y bloques de ambas fuentes. Se sustituyó un punto y coma en un mensaje para evitar que se interprete como separador. No hay Mermaid ni su CLI disponibles localmente: **la validación mediante parser y el renderizado de ambas secuencias siguen pendientes**. No se certifica validez sintáctica solamente por la inspección del texto.

- `MenuPrincipal` reúne las pantallas y `ControladorVentas` coordina las operaciones.
- El menú construye la reserva y sus pasajeros. Ventas valida antes de guardar y delega el precio a `CalculadoraPrecioReserva` (SRP).
- Las promociones se consultan mediante `EstrategiaPromocion` y se elige un único descuento (OCP).
- `Reserva.registrarPago` crea el pago (Creador); `Pago` utiliza `MedioPago` y la tarjeta resuelve el recargo (DIP).
- La confirmación toma el capital abonado. El recargo no reduce el saldo.
- Los cambios se persisten mediante los repositorios; salidas y reservas se escriben juntas (Fabricación pura).
- Los errores ordinarios vuelven al menú; un error de archivo interrumpe la sesión. No se dibuja una reversión de cambios que el código no implementa.

Las diferencias de diseño y los puntos pendientes se detallan en [Correspondencia técnica](../Correspondencia_Tecnica.md).

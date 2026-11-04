# Plan de pruebas — Agencia de Turismo Rumbo Sur

## Objetivo y alcance

Se verificaron las operaciones principales de la Propuesta B de la Etapa 2: catálogo, clientes, reservas, cobros, cupos, modificaciones, cancelaciones y operación de salidas. También se comprobó que los datos se conservaran después de cerrar el programa.

La revisión tomó como referencia el enunciado, el diseño de la Entrega 1 y las clases actuales. Las pruebas unitarias comprueban cálculos y reglas del dominio; las funcionales recorren `Presentacion.MenuPrincipal` y sus controladores y repositorios.

## Entorno y resultados

**Reverificación del 04/11/2026:** compilación completa con destino Java 17, 55 comprobaciones unitarias aprobadas y nueva ejecución de F01–F24: 24 aprobadas, 0 fallidas. Se ejecutó `Presentacion.MenuPrincipal` en carpetas temporales, incluidos reinicios para recuperar datos. F25 continúa PENDIENTE. Las fechas de trabajo de los escenarios se mantienen para reproducir los mismos cálculos.

- Fecha de ejecución: **03/11/2026**.
- Rama: `revision-codex-tpo`.
- Compilación de todo `src` con Temurin 24 y `javac --release 17 -encoding UTF-8`.
- Pruebas unitarias: **55 comprobaciones aprobadas, 0 fallidas**, distribuidas en dos clases.
- Pruebas funcionales: **24 casos APROBADOS, 0 FALLIDOS y 1 PENDIENTE**.

Los casos F01 a F24 se ejecutaron automáticamente enviando las entradas al programa de consola y comparando las salidas con los resultados esperados. Los reinicios se realizaron con procesos Java nuevos. No son verificaciones manuales de IntelliJ.

Cada escenario usó una carpeta temporal independiente. No se modificaron ni eliminaron los archivos de `datos` del proyecto. El programa auxiliar y los registros temporales no forman parte de la entrega. Los pasos siguientes permiten repetir los recorridos manualmente.

La compilación finalizó correctamente. Se mantiene la advertencia de conversión de tipos en `ArchivoLista`, al recuperar una lista mediante `ObjectInputStream`.

## Pruebas unitarias

Se conservó el estilo existente: métodos de prueba, comparaciones, `try/catch` para los rechazos y un método `verificar`. Cada comprobación correcta imprime `[OK]`; una diferencia provoca una excepción y detiene la ejecución. No se agregaron bibliotecas ni frameworks.

| Archivo | Cobertura | Resultado |
|---|---|---|
| `src/Test/CalculadoraPrecioReservaTest.java` | Precios por categoría; promociones a 60 y 61 días; seis límites de devolución; edad y seguro de aventura; seña, vencimiento y cupos; itinerario, vigencias, fechas e importes. | 33 aprobadas, 0 fallidas. Se ejecutó antes y después de incorporar las nuevas pruebas. |
| `src/Test/ReservaSalidaTest.java` | Pagos negativos, excesivos y fuera de fecha; recargo y saldo; devolución y cancelación repetida; bebés y sobreventa; guía, vehículo, reemplazo, lista vigente, inicio, cierre y honorarios. | 22 aprobadas, 0 fallidas. Archivo incorporado en esta revisión. |

Son 55 comprobaciones dentro de métodos que agrupan escenarios; no se cuentan los reinicios ni las líneas impresas por el menú como pruebas unitarias.

### Reglas contrastadas

| Regla del enunciado | Comprobación |
|---|---|
| Adulto 100 %, menor de 12 años 60 %, menor de 3 años sin cargo y jubilado 90 %. | Base de $1.000: resultados $1.000, $600, $0 y $900. El bebé ocupa un lugar. |
| Anticipación mayor a 60 días: 15 %. Grupo de seis o más: 10 %. No se acumulan. | Seis pasajeros y subtotal $6.000: descuento $900 a 61 días y $600 a 60 días. |
| Seña del 30 % y saldo hasta diez días antes. | 29 % mantiene pendiente; 30 % confirma. A diez días se admite pago; a nueve se rechaza y se anula el saldo vencido al controlar plazos. |
| Tarjeta en cuotas: 7 % de recargo. | Capital $30: cobro $32,10 y saldo $70 sobre una reserva de $100. |
| Devolución del cliente: 90 %, 50 %, 20 % o 0 %, según anticipación; agencia 100 %. | Sobre $100 se verificaron 30, 29, 15, 14, 7 y 6 días. También devolución de recargos y liberación de lugares. |
| Cancelar la salida siete días antes si no alcanza el mínimo. | Salida cancelada, ocupación cero y devolución completa al pasajero que había pagado. |
| Aventura exige edad mínima y seguro. | Rechazo sin seguro y antes del cumpleaños; aceptación el día que alcanza la edad con seguro. |
| Reemplazo hasta tres días antes, conservando precio y requisitos. | Aceptación a tres días con adicional conservado; rechazo a dos días; nueva lista requerida. |
| Guía habilitado, sin superposición, y vehículo con capacidad suficiente. | Validaciones del dominio y rechazos desde el menú en F20 y F21. |
| Honorario diario más 5 % de adicionales vendidos. | Después del cierre: $500 + 5 % de $100 = $505. El recorrido precargado liquida $18.100. |

### Ejecución

Desde PowerShell, en la raíz del proyecto, con `java` y `javac` en el PATH:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac --release 17 -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out Test.CalculadoraPrecioReservaTest
java -cp out Test.ReservaSalidaTest
```

También se puede ejecutar el `main` de cada clase desde IntelliJ. Las fechas ingresadas por el usuario son **DD/MM/AAAA**. En las pruebas que llaman directamente a constructores se usa el formato interno de `Fecha`, que no cambia el formato del menú.

## Preparación de los recorridos funcionales

Para realizar una prueba manual sin afectar los datos del proyecto, compilar y abrir el programa desde una carpeta nueva. El siguiente ejemplo crea la carpeta en el directorio temporal de Windows:

```powershell
$clasesRumbo = Join-Path (Get-Location).Path 'out'
$pruebaRumbo = Join-Path $env:TEMP 'rumbo-prueba-manual-01'
New-Item -ItemType Directory -Path $pruebaRumbo -ErrorAction Stop | Out-Null
Push-Location $pruebaRumbo
try { java -cp $clasesRumbo Presentacion.MenuPrincipal } finally { Pop-Location }
```

Si la carpeta ya existe, elegir otro nombre. Para reiniciar un mismo escenario, usar `Push-Location $pruebaRumbo` y la última línea, sin volver a crear la carpeta. No borrar datos para comenzar otra prueba: usar otra carpeta.

Los recorridos usan estos grupos de datos:

- **D — Venta de demostración:** ejecutar las entradas de [demo.txt](demo.txt) en una carpeta nueva. Fecha 01/02/2028, cliente C1, tour cultural T, actividad con proveedor Hotel, tarifa baja $1.000, salida `T_01/03/2028` con dos lugares. Reserva 2: pasajera Eva, documento P1, nacimiento 01/01/1990, adulta, contacto 4455, sin restricciones. Vendedor D1, Luis, legajo V1, comisión 5 %. Adicional Traslado de $100 y pago $330 con tarjeta en tres cuotas. La reserva 1 pertenece a la precarga. Ejecutar F01 a F11 en orden sobre esta carpeta; F01 a F04 son comprobaciones del mismo recorrido de demostración.
- **S — Salta:** carpeta nueva, fecha 03/11/2026. La precarga genera la reserva 1 en `RS01_15/01/2027`, con Ana y Leo, un traslado de $2.000, guía Julia Ríos y combi de 12 lugares. Total $36.000, seña $10.800 y saldo $25.200. Ejecutar F12 a F17 en orden.
- **C — Cancelaciones:** dos carpetas nuevas con la misma fecha y datos de S. Usar una para F18 y otra para F19, para que ambas partan de la reserva vigente.
- **R — Recursos:** otra carpeta nueva con los datos de S. Ejecutar F20 y F21 en orden.
- **P — Reemplazos:** otra carpeta nueva con los datos de S. Pagar el saldo de $25.200 en efectivo y una cuota el 03/11/2026, salir y ejecutar F22 y F23 en orden.
- **V — Vencimiento:** otra carpeta nueva con los datos de S. Salir sin pagar el saldo y ejecutar F24.

En las tablas, `8 → 3` significa opción 8 del menú principal y luego opción 3 del submenú. Cada operación vuelve al menú principal. Para finalizar una sesión se usa 0. Los importes se ingresan sin separador de miles y con coma decimal.

## Casos funcionales y resultados

### Venta y consulta

| ID | Caso de uso o funcionalidad | Datos de entrada | Resultado esperado | Resultado obtenido | Estado | Observaciones |
|---|---|---|---|---|---|---|
| F01 | Registrar cliente | D: opción 2, C1, Ana Vega, ana@example.com, 111, Argentina. | Confirmar el registro. | Mensaje «Cliente registrado exitosamente». El cliente se utilizó en F03. | APROBADO | Parte de demo.txt. |
| F02 | Crear catálogo y programar salida | D: tour T, cultural, un día, mínimo 1, máximo 2; tarifa baja $1.000 desde 01/01/2028; actividad 1.1 a las 09:00; opción 3, inicio y fin 01/03/2028, cupo 2. | Registrar tour y salida con tarifa e itinerario. | Tour registrado y salida `T_01/03/2028` programada. | APROBADO | Entradas completas en demo.txt. |
| F03 | Crear reserva y calcular precio | D: opción 4, C1, salida T, baja, vendedor D1/V1, una pasajera adulta P1. | Crear reserva 2 por $1.000, sin descuento por grupo ni anticipación. | Reserva 2 creada; total $1.000. | APROBADO | A 29 días de la salida. |
| F04 | Contratar adicional, pagar y consultar | D: 7 → 1, reserva 2, P1, Traslado, 100; opción 5, reserva 2, 330, tarjeta, 3; opción 6, reserva 2. | Total $1.100, seña del 30 %, cobro $353,10 y saldo $770. | Reserva CONFIRMADA con total $1.100 y saldo $770; cobro $353,10. | APROBADO | Recargo separado del capital. |
| F05 | Consultar catálogo | D: 1 → 4. | Mostrar T, itinerario, proveedor y tarifa con fecha local. | Museos, proveedor Hotel y tarifa baja $1.000 desde 01/01/2028. | APROBADO | Nueva sesión sobre D. |
| F06 | Rechazar cliente duplicado | D: repetir los datos de C1 en opción 2. | Informar rechazo. | Operación rechazada. | APROBADO | Documento ya registrado. |
| F07 | Verificar cupo disponible | D: opción 4, C1, salida T, baja, vendedor D1/V1, cantidad 2; luego opción 9. | Rechazar porque queda un lugar y mantener un pasajero reservado. | Mensaje de falta de cupo; reporte con máximo 2 y reservados 1. | APROBADO | Rechazo antes de pedir pasajeros. |
| F08 | Rechazar sobrepago | D: opción 5, reserva 2, 800, efectivo, 1; luego 6, reserva 2. | Rechazar y mantener saldo $770. | Operación rechazada; total $1.100 y saldo $770. | APROBADO | Se verifica que no cambie la deuda. |
| F09 | Registrar ajuste | D: 7 → 4, reserva 2, motivo «Cambio de servicio», diferencia 10; luego consultar. | Total $1.110, saldo $780 y nota fechada. | Coincidieron ambos importes; nota del 01/02/2028. | APROBADO | Conserva el pago previo. |
| F10 | Recuperar archivos | D: salir, abrir con 01/02/2028 y consultar reserva 2. | Recuperar total, saldo y nota de F09. | Total $1.110, saldo $780 y motivo conservados. | APROBADO | Proceso Java nuevo. |
| F11 | Validar fecha | D: opción 3, tour T, inicio 31/02/2028. | Rechazar la fecha imposible. | Operación rechazada. | APROBADO | No es necesario ingresar fecha de fin. |

### Precarga, operación y reportes

| ID | Caso de uso o funcionalidad | Datos de entrada | Resultado esperado | Resultado obtenido | Estado | Observaciones |
|---|---|---|---|---|---|---|
| F12 | Consultar precarga y promoción | S: 1 → 4; opción 6, reserva 1. | Catálogo de ejemplo y reserva con 15 % de descuento en pasajes: $40.000 − $6.000 + $2.000. | Se observaron RS01 y RS05 en catálogo; reserva CONFIRMADA, total $36.000 y saldo $25.200. | APROBADO | La reserva se realizó con más de 60 días. |
| F13 | Emitir reportes | S: opción 9. | Ocupación 2/12 = 16,67 %, ventas V-SALTA $36.000 e ingresos RS01 $10.800. | Coincidieron los tres valores. | APROBADO | Antes de completar el saldo. |
| F14 | Completar pago y emitir lista | S: opción 5, reserva 1, 25200, efectivo, 1; 8 → 3, salida Salta; consultar reserva 1. | Saldo cero y lista con Ana y Leo. | Saldo $0; lista con documentos 30000001 y 31000002. | APROBADO | Lista guardada antes del reinicio. |
| F15 | Rechazar inicio anticipado | S: el 03/11/2026, 8 → 4, salida Salta. | Rechazar porque no llegó la fecha del viaje. | Mensaje «Aún no llegó la fecha de inicio». | APROBADO | Se mantienen los datos para F16. |
| F16 | Iniciar, cerrar y liquidar | S: reiniciar el 15/01/2027; 8 → 4, 8 → 5 y 8 → 6, indicando salida Salta en cada operación; consultar reserva 1. | Finalizar y liquidar $18.000 + 5 % de $2.000 = $18.100. | Reserva FINALIZADA, saldo cero y honorarios $18.100. | APROBADO | Utiliza guía y vehículo precargados. |
| F17 | Recuperar salida finalizada | S: reiniciar el 15/01/2027; consultar reserva 1 y liquidar nuevamente con 8 → 6. | Conservar estado final y cálculo de honorarios. | FINALIZADA, saldo cero y honorarios $18.100. | APROBADO | La liquidación consulta el importe; no registra otro pago. |

### Cancelaciones y validaciones

| ID | Caso de uso o funcionalidad | Datos de entrada | Resultado esperado | Resultado obtenido | Estado | Observaciones |
|---|---|---|---|---|---|---|
| F18 | Cancelación del cliente | C: 7 → 3, reserva 1, motivo «No viaja», origen cliente; consultar y emitir reportes; repetir cancelación. | Devolver 90 % de $10.800 = $9.720, liberar dos lugares y rechazar repetición. | Devolución $9.720; CANCELADA, reservados 0; segundo intento rechazado por reserva cerrada. | APROBADO | Más de 30 días de anticipación. |
| F19 | Cancelación de la agencia | Otra carpeta C: mismo recorrido de F18, origen agencia. | Devolver $10.800 completos, liberar lugares y rechazar repetición. | Devolución $10.800; CANCELADA, reservados 0; segundo intento rechazado. | APROBADO | No reutilizar la reserva cancelada de F18. |
| F20 | Validar vehículo y guía | R: 8 → 2, Salta, patente CHICA, capacidad 1, Auto. Luego 8 → 1, Salta, G2, Guia, email vacío, 111, matrícula M2, tarifa 100, categoría naturaleza, n, Castellano, n. | Rechazar capacidad insuficiente y categoría incompatible. | Se observaron ambos mensajes de rechazo. | APROBADO | Salta es cultural y su capacidad es 12. |
| F21 | Evitar recursos superpuestos | R: programar RS02 el 15/01/2027, un día, cupo 10. Asignarle guía documento G-SALTA, Julia, email vacío, 111, GS01, 18000, naturaleza, n, Castellano, n. Asignarle vehículo RS001AA, 12, Combi. | Rechazar porque ambos recursos están asignados a Salta ese día. | Mensajes «Guía ocupado» y «Vehículo ocupado». | APROBADO | La categoría cargada es válida para RS02; se comprueba el cruce. |
| F22 | Reemplazar pasajero | P: reiniciar el 12/01/2027; 7 → 2, reserva 1, documento anterior 30000001; nuevo P3, Rosa, 01/01/1990, adulto, 9876, Ninguna. Emitir lista y consultar. | Admitir a tres días, conservar total y reflejar el reemplazo. | Lista con Rosa/P3/9876, total $36.000, saldo cero y nota del reemplazo. | APROBADO | El traslado conservado se comprueba además en las pruebas unitarias. |
| F23 | Rechazar reemplazo fuera de plazo | P: reiniciar el 13/01/2027; reemplazar P3 por P4, Otra, 01/01/1990, adulto, 111, Ninguna; emitir lista. | Rechazar a dos días y conservar a Rosa. | Mensaje de plazo vencido; lista conserva Rosa/P3/9876. | APROBADO | Sigue a F22. |
| F24 | Anular por saldo vencido | V: reiniciar el 06/01/2027; consultar reserva 1 y reportes. | Anular la reserva incompleta a nueve días y liberar los dos lugares. | ANULADA, reporte con reservados 0 y motivo «Saldo vencido». | APROBADO | El control se ejecuta al ingresar al programa. |
| F25 | Ejecución manual desde IntelliJ | Configurar el JDK y una carpeta de trabajo nueva; ejecutar `Presentacion.MenuPrincipal`, ingresar 03/11/2026, consultar reserva 1, salir y volver a abrir. Ejecutar también los dos `main` de pruebas. | Menú operable, reserva recuperada y 55 comprobaciones correctas. | PENDIENTE | PENDIENTE | Requiere realizar el recorrido en la instalación de IntelliJ del grupo. |

## Alcance de la verificación y pendientes

La revisión técnica posterior volvió a ejecutar las dos clases unitarias: 55 comprobaciones aprobadas, sin modificar Java. No se repitieron los casos funcionales. F25 sigue pendiente; sus pasos manuales están detallados en [Correspondencia_Tecnica.md](Correspondencia_Tecnica.md#prueba-funcional-pendiente-f25-intellij). Las diferencias de alcance del enunciado allí señaladas no quedan resueltas por estos resultados de pruebas.

No se detectaron fallas en las comprobaciones ejecutadas y no se modificaron reglas de negocio en esta revisión. Los casos de error aprobados indican que el programa rechazó la operación según lo esperado.

La cobertura es representativa: no prueba todas las combinaciones de entradas. Los límites de descuentos, devoluciones, edad y vencimientos se verificaron directamente en las pruebas unitarias; no se repitió cada combinación desde el menú. La cancelación automática por mínimo insuficiente y la renovación de la lista después del reemplazo también tienen comprobaciones unitarias.

Queda la comprobación manual F25. Tampoco se ejecutó con una instalación independiente de JDK 17 ni se ensayó el uso simultáneo de varias consolas sobre los mismos archivos. La compilación con destino Java 17 sí fue verificada.

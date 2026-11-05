# TPO POO - Agencia de Turismo Rumbo Sur

## Integrantes

- Santiago Lopez Monea — Legajo 1213045.
- Carlos Thomas Rivas — Legajo 1208227.
- Bryan Zornada Alvarez Vega — Legajo 1210869.

Docente: María Angela León.

## Descripción del proyecto

Este proyecto corresponde al Trabajo Práctico Obligatorio de la materia Programación Orientada a Objetos.

La propuesta elegida es:

**Propuesta B: Agencia de Turismo Rumbo Sur.**

El sistema permite organizar tours culturales, de naturaleza y de aventura. Se pueden registrar clientes, programar salidas, realizar reservas con uno o más pasajeros, contratar servicios adicionales y registrar pagos.

También permite asignar guías y vehículos, emitir listas de pasajeros, cancelar o modificar reservas y consultar reportes de la agencia.

La idea principal del trabajo es aplicar los conceptos de Programación Orientada a Objetos vistos en la cursada, junto con los principios SOLID y patrones GRASP presentados en la Etapa 1.

## Repositorio

[TPO-POO en GitHub](https://github.com/santilm2017-sketch/TPO-POO)

## Organización del proyecto

El código está separado en paquetes para que cada parte tenga una responsabilidad clara. La siguiente estructura muestra los archivos principales.

```text
src/
 ├── Presentacion/
 │    ├── MenuPrincipal.java
 │    └── DatosIniciales.java
 │
 ├── Dominio/
 │    ├── Tour.java
 │    ├── TourCultural.java
 │    ├── TourNaturaleza.java
 │    ├── TourAventura.java
 │    ├── Actividad.java
 │    ├── Tarifa.java
 │    ├── Salida.java
 │    ├── Reserva.java
 │    ├── Cliente.java
 │    ├── Pasajero.java
 │    ├── Pago.java
 │    ├── Personal.java
 │    ├── PersonalAdministrativo.java
 │    ├── Guia.java
 │    ├── Vendedor.java
 │    ├── Vehiculo.java
 │    ├── Proveedor.java
 │    ├── ServicioAdicional.java
 │    ├── ContratacionServicio.java
 │    ├── CalculadoraPrecioReserva.java
 │    └── Excepciones/
 │
 ├── Controladores/
 │    ├── ControladorVentas.java
 │    ├── ControladorOperaciones.java
 │    └── ControladorAdministrativo.java
 │
 ├── Persistencia/
 │    ├── Repositorio.java
 │    ├── RepositorioClientes.java
 │    ├── RepositorioTours.java
 │    ├── RepositorioSalidas.java
 │    ├── RepositorioReservas.java
 │    ├── RepositorioPersonal.java
 │    ├── ArchivoLista.java
 │    └── ErrorArchivo.java
 │
 └── Test/
      ├── CalculadoraPrecioReservaTest.java
      ├── ReservaSalidaTest.java
      ├── PersonalAdministrativoTest.java
      └── RepositorioPersonalTest.java
```

La carpeta `docs` contiene la Entrega 1, los documentos de revisión, el plan de pruebas, una demostración por consola y las secuencias actualizadas.

## Funcionalidades principales

El sistema permite:

- Registrar y consultar tours, actividades, proveedores y tarifas.
- Registrar clientes y programar salidas.
- Crear reservas y controlar los lugares disponibles.
- Calcular precios según el pasajero, la temporada y las promociones.
- Contratar adicionales por pasajero, incluido el seguro obligatorio de aventura.
- Registrar señas y pagos con efectivo, transferencia o tarjeta.
- Listar todas las reservas con su estado, total, saldo y notas de ajuste.
- Reemplazar pasajeros, cancelar reservas y registrar notas de ajuste.
- Asignar guía y transporte, emitir la lista de pasajeros e iniciar y cerrar salidas.
- Calcular honorarios de guías y consultar reportes.
- Registrar y consultar coordinadores y administradores.
- Guardar y recuperar los datos mediante archivos.

## Conceptos utilizados

| Concepto | Uso dentro del sistema |
|---|---|
| Herencia y clases abstractas | Tour es la base de los tres tipos de viaje. Personal es la base de Guia, Vendedor y PersonalAdministrativo. |
| Encapsulamiento | Los datos se administran mediante constructores y métodos que validan las operaciones. |
| Interfaces y polimorfismo | Se aplican en medios de pago, promociones, políticas de cancelación y repositorios. |
| Colecciones | Las listas reúnen pasajeros, reservas, actividades y pagos. Los mapas agrupan los reportes económicos. |
| Comparable y Comparator | Ordenan las actividades por día y orden, y la lista de pasajeros por nombre. |
| Excepciones | Informan errores de datos, cupos, requisitos de pasajeros y archivos. |
| Lectura y escritura de archivos | Conservan clientes, tours, salidas con sus reservas y personal administrativo. |

## Justificación de las decisiones de diseño

### Separación de responsabilidades

El menú recibe los datos del usuario. Los controladores coordinan las operaciones y las clases del dominio aplican las reglas de la agencia. Los repositorios se encargan de guardar y recuperar la información.

Esta organización permite ubicar cada operación sin concentrar todo el programa en una sola clase.

### Principios SOLID

- **SRP:** CalculadoraPrecioReserva calcula los precios y las políticas de cancelación calculan las devoluciones. Reserva conserva sus pasajeros, pagos y estado.
- **OCP:** las promociones implementan EstrategiaPromocion. La calculadora las compara y aplica únicamente el descuento más favorable.
- **DIP:** Pago utiliza la interfaz MedioPago. Efectivo, Transferencia y TarjetaCredito resuelven el cálculo correspondiente.

### Patrones GRASP

- **Experto en Información:** Salida conoce los adicionales vendidos y Guia calcula sus honorarios con la información de la salida y su tarifa diaria.
- **Creador:** Reserva crea sus pagos y Pasajero crea sus contrataciones de servicios.
- **Controlador:** las solicitudes se coordinan mediante ControladorVentas, ControladorOperaciones y ControladorAdministrativo.
- **Fabricación Pura:** los repositorios resuelven la persistencia fuera de las entidades de la agencia.

### Guardado de datos

Las reservas se guardan junto con sus salidas para conservar relacionados los pasajeros, pagos y cupos. Los coordinadores y administradores se guardan en un archivo separado.

La carpeta `datos` se genera durante la ejecución. Para recuperar lo guardado, se debe volver a ejecutar desde el mismo directorio de trabajo. Sus archivos son locales y están excluidos del repositorio mediante `.gitignore`.

## Clases principales

| Clase | Responsabilidad |
|---|---|
| MenuPrincipal.java | Muestra el menú, recibe datos y presenta los resultados. |
| DatosIniciales.java | Carga los ejemplos iniciales cuando corresponde. |
| Tour.java y sus subclases | Representan los tours y sus requisitos. |
| Actividad.java y Tarifa.java | Representan el itinerario y los precios por temporada y vigencia. |
| Salida.java | Administra fechas, cupos, reservas, recursos y estado del viaje. |
| Reserva.java | Reúne titular, vendedor, pasajeros, pagos, modificaciones y cancelación. |
| Cliente.java y Pasajero.java | Representan al titular y a quienes participan del viaje. |
| CalculadoraPrecioReserva.java | Calcula pasajes, adicionales y promociones. |
| Pago.java | Conserva el capital abonado, la fecha, las cuotas y el medio de pago. |
| Guia.java y Vendedor.java | Representan al personal de operación y venta. |
| PersonalAdministrativo.java | Registra los datos del coordinador y administrador, incluido su legajo. |
| ServicioAdicional.java y ContratacionServicio.java | Representan el servicio y su contratación individual. |
| ControladorVentas.java | Coordina clientes, reservas, pagos y modificaciones. |
| ControladorOperaciones.java | Coordina salidas, recursos, vencimientos, listas, inicio y cierre. |
| ControladorAdministrativo.java | Administra catálogo y personal, calcula honorarios y genera reportes. |
| Repositorios y ArchivoLista.java | Buscan, guardan y recuperan los datos. |
| Fecha.java e Importes.java | Reúnen el manejo de fechas y de importes. |

## Cómo ejecutar el proyecto

Se necesita **JDK 17 o superior**. El proyecto fue compilado con Temurin 24 utilizando `--release 17`. No requiere bibliotecas externas.

### Desde IntelliJ IDEA

1. Abrir la carpeta del proyecto.
2. Seleccionar el JDK instalado en **File → Project Structure → Project**.
3. Verificar que `src` esté marcada como **Sources Root**.
4. Abrir `src/Presentacion/MenuPrincipal.java` y ejecutar su método `main`.
5. Usar la carpeta del proyecto como **Working directory** para conservar los datos entre ejecuciones.

### Desde consola

Abrir PowerShell en la raíz del proyecto, con `java` y `javac` disponibles en el PATH:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac --release 17 -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out Presentacion.MenuPrincipal
```

## Cómo probar el sistema

Durante una carga, **0 cancela y vuelve al menú principal**. En el menú principal, 0 sale del programa. Para ingresar un importe cero, escribir **0,00**; los ajustes negativos siguen permitidos.

Si una fecha, número u opción de texto es inválida, se vuelve a pedir ese campo. Un error que depende de varios datos permite elegir **1 para repetir la misma operación** o **0 para cancelarla**. Al repetir una operación se vuelven a ingresar sus datos. Los errores de archivos interrumpen la sesión para evitar continuar después de un fallo de guardado. No se impone un máximo arbitrario de días: una duración positiva admitida por el dominio sigue siendo válida.

### 1. Datos iniciales

Al ejecutar MenuPrincipal, se ingresa la fecha de trabajo en formato **DD/MM/AAAA**, por ejemplo `05/11/2026`. Los importes se ingresan sin separador de miles y con coma decimal, por ejemplo `15900,00`.

En una primera ejecución con fecha de trabajo del año 2026, se cargan cinco clientes y cinco tours con salidas en enero de 2027:

| Documento | Cliente |
|---|---|
| 30000001 | Ana Díaz |
| 30000002 | Luis Paz |
| 30000003 | Eva Ríos |
| 30000004 | Juan Vera |
| 30000005 | Sara Cruz |

| Salida | Tour | Categoría | Tarifa baja |
|---|---|---|---|
| RS01_15/01/2027 | Paseo por Salta | Cultural | $20.000,00 |
| RS02_16/01/2027 | Lagos de Bariloche | Naturaleza | $22.500,00 |
| RS03_17/01/2027 | Bodegas de Mendoza | Cultural | $25.000,00 |
| RS04_18/01/2027 | Cataratas del Iguazú | Naturaleza | $27.500,00 |
| RS05_19/01/2027 | Trekking en El Chaltén | Aventura | $30.000,00 |

El año de las salidas se determina en la primera carga. Cada tour tiene una actividad y tarifa; las salidas duran un día. El trekking exige edad mínima de 16 años y seguro de viaje.

La precarga incluye una reserva para Salta con Ana y Leo, traslado al hotel para Ana, guía y vehículo. Con fecha de trabajo 03/11/2026, su total es $36.000, la seña $10.800 y el saldo $25.200.

En los datos locales de la demostración se agregaron además estas dos reservas desde el menú:

| Reserva | Titular | Tour | Estado | Total | Saldo |
|---|---|---|---|---|---|
| 2 | Luis Paz | Lagos de Bariloche | PENDIENTE | $38.250,00 | $38.250,00 |
| 3 | Eva Ríos | Trekking en El Chaltén | CONFIRMADA | $53.000,00 | $37.100,00 |

Ambas tienen dos pasajeros; la de aventura incluye seguros y una seña por transferencia. Son datos guardados localmente, no una ampliación de la precarga: al descargar el repositorio en otra computadora no aparecen automáticamente. Los estados y saldos pueden cambiar al operar o avanzar la fecha de trabajo.

### 2. Menú interactivo

Después de cargar o recuperar los datos, se muestra el menú:

```text
=== RUMBO SUR - MENÚ PRINCIPAL ===

1. Catálogo de tours y personal
2. Registrar cliente
3. Programar salida
4. Crear reserva
5. Registrar pago
6. Listar reservas
7. Modificar o cancelar reserva
8. Operar salida
9. Reportes
0. Salir
```

La opción 1 muestra el catálogo automáticamente y permite registrar tours, tarifas y actividades. Desde **1 → 4** se muestran los coordinadores y administradores guardados. Allí se puede elegir **1** para registrar otro o **0** para volver.

El menú tiene nueve opciones principales y 0 para salir. La opción 7 reúne adicionales, reemplazos, cancelaciones y ajustes. La opción 8 tiene tres recorridos: **1. Asignar guía y vehículo**, **2. Emitir lista e iniciar salida** y **3. Cerrar salida y consultar honorarios**. En el segundo, responder **n** permite ver la lista sin iniciar. El tercero muestra los honorarios al cerrar y permite volver a consultarlos para una salida ya finalizada.

Ejemplo de prueba manual para consultar las reservas:

1. Ingresar `05/11/2026` como fecha de trabajo.
2. Seleccionar la opción **6** para ver todas las reservas, con titular, salida, fecha, estado, total y saldo.
3. Revisar el estado, saldo y las notas de ajuste en ese mismo listado.
4. Salir con **0** y volver a abrir desde el mismo directorio para comprobar que los datos se recuperan.

El menú no ejecuta las pruebas unitarias automáticamente. Se realizan por separado, como se indica a continuación.

### 3. Pruebas automatizadas

Después de compilar, ejecutar:

```powershell
java -cp out Test.CalculadoraPrecioReservaTest
java -cp out Test.ReservaSalidaTest
java -cp out Test.PersonalAdministrativoTest
```

Estas clases completaron **65 comprobaciones aprobadas y 0 fallidas**: 33 de precios y reglas, 22 de reservas y salidas y 10 de personal. Cada comprobación correcta muestra `[OK]`; una diferencia provoca una excepción.

La prueba de archivos de personal debe ejecutarse en una carpeta nueva para no usar los datos de trabajo. Desde la raíz del proyecto, después de compilar:

```powershell
$clasesPersonal = Join-Path (Get-Location).Path 'out'
$pruebaPersonal = Join-Path $env:TEMP 'rumbo-personal-prueba-01'
New-Item -ItemType Directory -Path $pruebaPersonal -ErrorAction Stop | Out-Null
Push-Location $pruebaPersonal
try { java -cp $clasesPersonal Test.RepositorioPersonalTest } finally { Pop-Location }
```

Si la carpeta ya existe, elegir otro nombre. Esta prueba completó **6 comprobaciones aprobadas y 0 fallidas** de guardado, recuperación, actualización y duplicados.

El archivo [demo.txt](docs/demo.txt) contiene otro recorrido con entradas para registrar cliente, tour, salida, reserva, adicional y pago. Debe ejecutarse en una carpeta nueva. Sus pasos y los demás escenarios están explicados en el plan de pruebas.

## Casos de prueba

Los siguientes resultados fueron verificados. Se resumen algunos casos; el detalle se encuentra en el [Plan de pruebas funcionales](docs/Plan_Pruebas_Funcionales.md).

| Funcionalidad evaluada | Datos de entrada | Resultado esperado | Resultado obtenido | Observación |
|---|---|---|---|---|
| Calcular precio por pasajero | Tarifa $1.000; adulto, menor de 12, menor de 3 y jubilado | $1.000, $600, $0 y $900 | Coincidieron los cuatro importes | Prueba unitaria |
| Aplicar promociones | Seis pasajeros, subtotal $6.000, reserva a 61 y a 60 días | Descuento $900 y $600 | Coincidieron ambos descuentos | No se acumulan |
| Confirmar reserva | Pagos del 29 % y del 30 % | PENDIENTE y CONFIRMADA | Se obtuvieron ambos estados | Se controla el capital pagado |
| Calcular devoluciones | $100 abonados; 30, 29, 15, 14, 7 y 6 días de anticipación | $90, $50, $50, $20, $20 y $0 | Coincidieron los seis límites | Cancelación del cliente |
| Validar aventura | Pasajero sin seguro o con edad insuficiente | Rechazar participación | Se rechazaron ambos casos | Se admite con edad y seguro válidos |
| Validar fecha | 29/02/2028 y 31/02/2028 | Aceptar la primera y rechazar la segunda | Ambas validaciones correctas | Formato DD/MM/AAAA |
| Registrar personal | Coordinador y administrador con sus datos | Guardar y recuperar ambos roles | Ambos recuperados tras reiniciar | Se rechazan duplicados |
| Liquidar honorarios | Salida finalizada, tarifa diaria $500 y adicionales $100 | $505 | Se obtuvo $505 | Incluye 5 % de adicionales |
| Listar reservas | Opción 6 con reservas guardadas | Mostrar titular, viaje, estado, total y saldo | Se mostraron los datos y las reservas canceladas | Los archivos no cambiaron al repetir la consulta |
| Listado vacío | Repositorio sin reservas | Informar que no hay reservas | Se mostró el mensaje correspondiente | Verificación aislada de la precarga |

La última ejecución sobre el menú simplificado completó **47 casos funcionales aprobados, 0 fallidos y F25 pendiente**: 24 recorridos principales, tres de personal, uno de compatibilidad, 13 de validación de entradas y seis de operaciones agrupadas. La revisión posterior del listado agregó cuatro verificaciones específicas aprobadas: datos visibles, acceso al personal reorganizado, conservación de archivos y casos de reservas canceladas y listado vacío, agrupados en cuatro comprobaciones.

## Estado de la entrega

El proyecto incluye:

- Código Java organizado en paquetes.
- Herencia, interfaces, colecciones, excepciones y archivos.
- Aplicación de los principios SOLID y patrones GRASP de la Etapa 1.
- Menú interactivo y datos iniciales.
- Pruebas automatizadas y plan de pruebas funcionales con resultados.
- README con instrucciones de compilación y ejecución.
- Documentación de diseño y correspondencia con la implementación.
- Enlace al repositorio de GitHub.

La compilación y las pruebas se verificaron por consola. Queda completar **F25 desde IntelliJ**, validar con un parser y renderizar las secuencias Mermaid y aclarar con la cátedra el alcance de las liquidaciones a proveedores. El programa registra proveedores asociados a actividades y calcula honorarios de guías; no liquida pagos a proveedores.

El diseño original está en la [Entrega 1](docs/TPO%20POO%20(1).pdf). Las diferencias y aclaraciones se encuentran en [Correspondencia técnica](docs/Correspondencia_Tecnica.md) y las versiones editables de las secuencias en [docs/uml](docs/uml/README.md). El informe final del TPO será redactado por el grupo por separado.

## Conclusión

El trabajo permite aplicar los conceptos de Programación Orientada a Objetos dentro de un sistema de turismo.

Rumbo Sur relaciona tours, salidas, clientes, pasajeros y pagos, separando las operaciones del menú, las reglas de negocio y el guardado de datos.

El sistema se ejecuta desde MenuPrincipal.java, donde se cargan o recuperan los datos y se accede a las funcionalidades por consola. Las pruebas permiten comprobar los cálculos y las operaciones principales de la agencia.

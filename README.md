# Agencia de Turismo Rumbo Sur
Trabajo Práctico Obligatorio de Programación Orientada a Objetos — Propuesta B.

## Integrantes

| Integrante | Legajo |
|---|---|
| Santiago Lopez Monea | 1213045 |
| Carlos Thomas Rivas | 1208227 |
| Bryan Zornada Alvarez Vega | 1210869 |

Docente: María Angela León.

## Descripción del proyecto

Rumbo Sur es un sistema de consola para una agencia que organiza tours culturales, de naturaleza y de aventura. Permite administrar el catálogo, programar salidas y registrar reservas con sus pasajeros, adicionales y pagos.

El programa reúne la venta y la operación de cada salida. Controla los lugares disponibles, calcula los precios y las devoluciones, y permite consultar los datos necesarios para realizar el viaje y liquidar los honorarios del guía.

## Repositorio

El proyecto se encuentra en [TPO-POO en GitHub](https://github.com/santilm2017-sketch/TPO-POO). La revisión de esta entrega se está realizando en la rama `revision-tpo`.

## Organización del proyecto

La estructura principal es la siguiente. En `Dominio` se muestran algunas de las clases más importantes.

```text
TPO-POO/
├── src/
│   ├── Presentacion/
│   │   ├── MenuPrincipal.java
│   │   └── DatosIniciales.java
│   ├── Controladores/
│   │   ├── ControladorVentas.java
│   │   ├── ControladorOperaciones.java
│   │   └── ControladorAdministrativo.java
│   ├── Dominio/
│   │   ├── Tour.java
│   │   ├── TourAventura.java
│   │   ├── TourCultural.java
│   │   ├── TourNaturaleza.java
│   │   ├── Salida.java
│   │   ├── Reserva.java
│   │   ├── Cliente.java
│   │   ├── Pasajero.java
│   │   ├── Pago.java
│   │   ├── CalculadoraPrecioReserva.java
│   │   └── Excepciones/
│   │       ├── CupoSuperadoException.java
│   │       └── RequisitoPasajeroException.java
│   ├── Persistencia/
│   │   ├── Repositorio.java
│   │   ├── RepositorioClientes.java
│   │   ├── RepositorioTours.java
│   │   ├── RepositorioSalidas.java
│   │   ├── RepositorioReservas.java
│   │   ├── ArchivoLista.java
│   │   └── ErrorArchivo.java
│   └── Test/
│       ├── CalculadoraPrecioReservaTest.java
│       └── ReservaSalidaTest.java
├── docs/
│   ├── TPO POO (1).pdf
│   ├── Revision-Etapa-2.md
│   ├── Plan_Pruebas_Funcionales.md
│   └── demo.txt
└── README.md
```

La carpeta `datos` se crea al guardar información. Contiene los archivos de clientes, tours y salidas con sus reservas. Para recuperar los datos entre ejecuciones, se debe abrir el programa desde el mismo directorio de trabajo.

## Funcionalidades principales

- Registrar tours, sus actividades, proveedores asociados y tarifas por temporada y vigencia.
- Programar salidas y controlar sus cupos y estados.
- Registrar clientes y crear reservas con uno o más pasajeros.
- Calcular precios según el tipo de pasajero y aplicar la promoción más favorable.
- Contratar servicios por pasajero y exigir seguro de viaje en los tours de aventura.
- Registrar señas y pagos en efectivo, transferencia o tarjeta, incluyendo el recargo por cuotas.
- Reemplazar pasajeros, cancelar reservas y registrar notas de ajuste.
- Asignar guía y vehículo, emitir la lista de pasajeros, iniciar y cerrar salidas.
- Liquidar honorarios del guía y consultar ocupación, ventas por vendedor, ingresos por tour y cancelaciones.

El control de vencimientos se realiza al abrir el sistema y antes de cada operación, utilizando la fecha de trabajo ingresada.

## Conceptos y herramientas utilizados

El proyecto utiliza Java y las herramientas de la cursada.

| Concepto | Aplicación |
|---|---|
| Herencia y clases abstractas | `Tour` tiene las subclases `TourCultural`, `TourNaturaleza` y `TourAventura`. `Personal` es la base de `Guia` y `Vendedor`. |
| Interfaces y polimorfismo | Los medios de pago, las promociones y las políticas de cancelación resuelven sus cálculos mediante implementaciones de una interfaz común. |
| Encapsulamiento | Las entidades conservan sus datos en atributos privados o protegidos y validan las operaciones mediante sus métodos. Las listas se devuelven como copias. |
| Colecciones | Se usan listas para reservas, pasajeros y actividades, y mapas para agrupar los reportes económicos. |
| Ordenamiento | `Actividad` implementa `Comparable` por día y orden. La lista de pasajeros se ordena por nombre con `Comparator`. |
| Excepciones | Se rechazan operaciones inválidas y se usan excepciones propias para cupos y requisitos del pasajero. El menú maneja errores y cierra el lector con `finally`. |
| Archivos | Los repositorios leen y escriben objetos con `Serializable`, `ObjectInputStream` y `ObjectOutputStream`. |

Se utiliza IntelliJ IDEA para trabajar con el proyecto y Git para el control de versiones. La compilación requiere únicamente el JDK.

## Justificación de las decisiones de diseño

Se mantuvo la separación entre presentación, controladores, dominio y persistencia planteada en la Etapa 1. Esto permite ubicar cada regla y modificarla sin tener que recorrer todo el menú.

Los tres principios SOLID y los cuatro patrones GRASP elegidos se aplican de la siguiente manera.

| Principio o patrón | Decisión |
|---|---|
| SRP — Responsabilidad única | `Reserva` administra sus datos y estado. `CalculadoraPrecioReserva` calcula el precio y las políticas calculan las devoluciones. |
| OCP — Abierto/cerrado | La calculadora recorre las implementaciones de `EstrategiaPromocion`. Se puede incorporar otra promoción mediante `agregarPromocion`. |
| DIP — Inversión de dependencias | `Pago` utiliza la interfaz `MedioPago`, que implementan `Efectivo`, `Transferencia` y `TarjetaCredito`. |
| Experto | `Salida` suma los adicionales de sus reservas y `Guia` calcula los honorarios con su tarifa diaria. |
| Creador | `Reserva` crea los pagos y `Pasajero` crea sus contrataciones de servicios. |
| Controlador | Los tres controladores reciben las solicitudes del menú y coordinan las operaciones. |
| Fabricación pura | Los repositorios se encargan de los archivos, una tarea separada de las entidades de la agencia. |

Las reservas se guardan junto con su salida para recuperar una sola versión de los cupos, pagos y pasajeros. Los nombres de los repositorios se pasaron a castellano. Las diferencias con el modelo y su justificación están desarrolladas en el [informe de la Etapa 2](docs/Revision-Etapa-2.md). El diseño original se encuentra en la [entrega de la Etapa 1](docs/TPO%20POO%20(1).pdf).

## Clases principales y responsabilidades

| Clase o grupo | Responsabilidad |
|---|---|
| `MenuPrincipal` y `DatosIniciales` | Recibir los datos del usuario y cargar los ejemplos iniciales. |
| `ControladorVentas` | Coordinar clientes, reservas, pagos, adicionales, reemplazos, ajustes y cancelaciones. |
| `ControladorOperaciones` | Programar salidas, asignar recursos, controlar vencimientos y coordinar el inicio y cierre. |
| `ControladorAdministrativo` | Administrar el catálogo, emitir reportes y consultar honorarios. |
| `Tour`, sus subclases, `Actividad` y `Tarifa` | Representar el recorrido, sus requisitos, el itinerario y los precios vigentes. |
| `Salida` | Reunir fecha, cupo, reservas, guía, vehículo y estado de un viaje concreto. |
| `Reserva`, `Cliente` y `Pasajero` | Relacionar al titular con el viaje y sus participantes, conservando pagos y modificaciones. |
| `CalculadoraPrecioReserva`, `PromocionAnticipada` y `PromocionGrupo` | Calcular pasajes, adicionales y el descuento que corresponde. |
| `Pago` y las implementaciones de `MedioPago` | Registrar el capital abonado y calcular el importe cobrado según el medio y las cuotas. |
| `CancelacionReserva` y las políticas de cancelación | Conservar el motivo y las devoluciones calculadas para el cliente o la agencia. |
| `Guia`, `Vendedor` y `Vehiculo` | Representar al personal involucrado y al transporte asignado. |
| `Proveedor`, `ServicioAdicional` y `ContratacionServicio` | Registrar proveedores de actividades y servicios contratados por cada pasajero. |
| `Repositorio`, sus implementaciones y `ArchivoLista` | Buscar, guardar y recuperar la información. |
| `Fecha` e `Importes` | Centralizar las operaciones de calendario y el manejo de importes. |

## Cómo ejecutar el proyecto

Se necesita un **JDK 17 o superior**. La versión se verificó con Temurin 24, compilando para Java 17 mediante `--release 17`. La configuración de IntelliJ incluida en el proyecto apunta a JDK 24.

### Desde IntelliJ IDEA

1. Abrir la carpeta `TPO-POO`.
2. En **File → Project Structure → Project**, seleccionar el JDK instalado. Si se usa JDK 17, seleccionar también el nivel de lenguaje 17.
3. Verificar que `src` esté marcada como **Sources Root**.
4. Abrir `src/Presentacion/MenuPrincipal.java` y ejecutar el método `main` con el botón verde.
5. En la configuración de ejecución, usar `Presentacion.MenuPrincipal` como clase principal y la carpeta del proyecto como **Working directory**.

Al iniciar, ingresar la fecha de trabajo en formato **DD/MM/AAAA**, por ejemplo `03/11/2026`. Para probar otro día, salir y volver a ejecutar con la nueva fecha. Los importes se ingresan sin separador de miles y con coma decimal, por ejemplo `25200,50`.

### Desde consola

Abrir PowerShell en la raíz del proyecto. Los comandos `java` y `javac` deben estar disponibles en el PATH.

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac --release 17 -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out Presentacion.MenuPrincipal
```

## Cómo probar el sistema

La opción **10 → 1** registra coordinador o administrador con documento, nombre, email, teléfono, legajo y rol. **10 → 2** consulta ambos roles. Los cuatro datos obligatorios se validan y se rechazan legajos o documentos repetidos. Se recuperan al reiniciar desde el mismo directorio, mediante `datos/personal.dat`.

`PersonalAdministrativo` hereda de `Personal`. Conserva su propio legajo; Vendedor mantiene el suyo y Guia utiliza matrícula/legajo. La estructura serializada de esas clases existentes no se modificó.

Para ejecutar la prueba de archivos de personal, después de compilar, usar una carpeta nueva (si existe, elegir otro nombre):

```powershell
$clasesPersonal = Join-Path (Get-Location).Path 'out'
$pruebaPersonal = Join-Path $env:TEMP 'rumbo-personal-prueba-01'
New-Item -ItemType Directory -Path $pruebaPersonal -ErrorAction Stop | Out-Null
Push-Location $pruebaPersonal
try { java -cp $clasesPersonal Test.RepositorioPersonalTest } finally { Pop-Location }
```

### Opciones del menú

| Opción | Uso |
|---|---|
| 1. Catálogo de tours | Registrar tours, agregar tarifas y actividades, o consultar el catálogo. |
| 2. Registrar cliente | Cargar los datos del titular de una reserva. |
| 3. Programar salida | Elegir un tour existente, las fechas y el cupo. |
| 4. Crear reserva | Elegir cliente y salida, registrar vendedor y pasajeros y calcular el precio. |
| 5. Registrar pago | Indicar reserva, importe, medio de pago y cuotas. |
| 6. Consultar reserva | Ver estado, total, saldo y notas de ajuste. |
| 7. Modificar o cancelar reserva | Contratar adicionales, reemplazar pasajeros, cancelar o registrar un ajuste. |
| 8. Operar salida | Asignar guía y vehículo, emitir la lista, iniciar, cerrar o liquidar honorarios. |
| 9. Reportes | Consultar ocupación, ventas por vendedor, ingresos por tour y cancelaciones. |
| 10. Personal administrativo | Registrar y consultar coordinador y administrador. |
| 0. Salir | Terminar la sesión. |

### Datos precargados

Los siguientes ejemplos corresponden a una primera ejecución en una carpeta sin datos, con fecha de trabajo **03/11/2026**. Al volver a abrir, se recupera la información guardada. El año de las salidas de ejemplo se determina en la primera carga.

| Documento | Cliente |
|---|---|
| 30000001 | Ana Díaz |
| 30000002 | Luis Paz |
| 30000003 | Eva Ríos |
| 30000004 | Juan Vera |
| 30000005 | Sara Cruz |

Todos los viajes de ejemplo duran un día, tienen cupo mínimo de dos pasajeros, una actividad a las 09:00 y una tarifa de temporada baja.

| Código de salida | Viaje | Cupo máximo | Tarifa por pasajero |
|---|---|---|---|
| RS01_15/01/2027 | Paseo por Salta | 12 | $20.000,00 |
| RS02_16/01/2027 | Lagos de Bariloche | 10 | $22.500,00 |
| RS03_17/01/2027 | Bodegas de Mendoza | 12 | $25.000,00 |
| RS04_18/01/2027 | Cataratas del Iguazú | 10 | $27.500,00 |
| RS05_19/01/2027 | Trekking en El Chaltén | 8 | $30.000,00 |

El trekking exige una edad mínima de 16 años, tiene dificultad media y requiere seguro por pasajero.

La **reserva 1** corresponde a Salta. Incluye a Ana Díaz y Leo Díaz, con un traslado al hotel de $2.000,00 para Ana. La vendedora es Laura Paz, legajo `V-SALTA`. La salida tiene asignados a la guía Julia Ríos y una combi de 12 lugares, patente `RS001AA`.

Con la fecha indicada se aplica el 15 % de descuento anticipado sobre los dos pasajes. El total es **$36.000,00**, la seña por transferencia es **$10.800,00** y el saldo es **$25.200,00**.

### Recorrido sugerido

1. Consultar la reserva 1 con la opción **6** y observar el saldo.
2. Entrar en **8 → 3** y emitir la lista de `RS01_15/01/2027`.
3. Consultar los reportes con **9**. Salta debe mostrar dos pasajeros sobre doce lugares.
4. En **5**, pagar `25200,00` para la reserva 1, con medio `efectivo` y una cuota.
5. Salir y volver a abrir con fecha `15/01/2027`. En **8**, iniciar la salida (4), cerrarla (5) y liquidar honorarios (6), indicando el mismo código en cada operación. El honorario es **$18.100,00**.

Para cargar una venta propia se puede usar otro cliente precargado y una de las salidas disponibles. Para crear un recorrido nuevo, primero se registra el tour con una tarifa y una actividad, y después se programa su salida.

### Pruebas unitarias

La clase `Test.CalculadoraPrecioReservaTest` comprueba precios, promociones, cancelaciones, requisitos de aventura, cupos, vencimientos, tarifas, fechas e importes. `Test.ReservaSalidaTest` agrega rechazos de pagos, control de cupos, reemplazos, recursos, inicio, cierre y honorarios. Se ejecuta el `main` de cada una desde IntelliJ o, después de compilar, con:

```powershell
java -cp out Test.CalculadoraPrecioReservaTest
java -cp out Test.ReservaSalidaTest
java -cp out Test.PersonalAdministrativoTest
```

La ejecución verificada completó **65 comprobaciones unitarias aprobadas y ninguna fallida**: 33 en la primera clase, 22 en la segunda y 10 en `Test.PersonalAdministrativoTest`. También aprobaron las 6 comprobaciones de archivos de `Test.RepositorioPersonalTest`. Cada caso correcto muestra `[OK]`; un resultado incorrecto interrumpe la prueba con una excepción.

### Demostración desde un archivo

El archivo [demo.txt](docs/demo.txt) contiene las entradas para cargar un cliente, un tour cultural, una salida, una reserva, un adicional y un pago. Para ejecutarlo en una carpeta nueva, desde la raíz del proyecto y después de compilar:

```powershell
New-Item -ItemType Directory demo-rumbo -ErrorAction Stop
Push-Location demo-rumbo
try { Get-Content ../docs/demo.txt | java -cp ../out Presentacion.MenuPrincipal } finally { Pop-Location }
```

Si `demo-rumbo` ya existe, elegir otro nombre de carpeta en las dos primeras líneas. La demostración usa la fecha `01/02/2028`. Crea la reserva 2, porque la precarga ocupa el primer número, y termina con total de $1.100,00, cobro con tarjeta de $353,10 y saldo de $770,00.

## Casos de prueba

Estos casos fueron ejecutados con el código actual. Los importes del ejemplo precargado corresponden a la primera apertura con fecha 03/11/2026.

| Caso | Datos de entrada | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| Precio por pasajero | Tarifa 1.000 y tipos adulto, menor de 12, menor de 3 y jubilado | 1.000 / 600 / 0 / 900 | Coincidieron los cuatro importes. |
| Promociones | Seis pasajeros, subtotal 6.000, reserva a 61 y a 60 días | Descuento de 900 y de 600, respectivamente | Coincidieron ambos descuentos. |
| Cancelación del cliente | 100 abonados, con 30, 29, 15, 14, 7 y 6 días de anticipación | Devolver 90 / 50 / 50 / 20 / 20 / 0 | Coincidieron los seis límites. |
| Fechas | 29/02/2028 y 31/02/2028 | Aceptar la primera y rechazar la segunda | Ambas validaciones fueron correctas. |
| Reserva precargada | Consultar reserva 1 y reportes al abrir | Total 36.000, saldo 25.200, ocupación 16,67 % e ingresos 10.800 | Se observaron los cuatro resultados. |
| Cobro y liquidación | Pagar 25.200, reiniciar el 15/01/2027, iniciar y cerrar Salta | Saldo cero y honorarios 18.100 | Se conservaron los datos y se liquidó el importe esperado. |
| Venta desde demo.txt | Pasaje 1.000, traslado 100 y seña 330 con tarjeta en tres cuotas | Total 1.100, cobro 353,10 y saldo 770 | Reserva confirmada con esos importes. |

El [plan de pruebas funcionales](docs/Plan_Pruebas_Funcionales.md) detalla las entradas, los resultados y cómo repetir los recorridos en carpetas separadas. Incluye 27 casos aprobados desde consola, ninguno fallido y una verificación manual pendiente en IntelliJ.

## Estado de la entrega

Las [aclaraciones de correspondencia técnica](docs/Correspondencia_Tecnica.md) clasifican las diferencias con la Etapa 1. Las [secuencias actualizadas y editables](docs/uml/README.md) complementan los diagramas originales. El registro y recuperación del coordinador y administrador está implementado. Queda confirmar el alcance de las liquidaciones a proveedores.

El código compila para Java 17 y se verificaron las pruebas unitarias y los recorridos de consola descriptos en este README. Las pruebas se ejecutaron con JDK 24. Las indicaciones de IntelliJ corresponden a la estructura y configuración del proyecto; la verificación de ejecución se realizó desde PowerShell.

La documentación incluye el diseño de Etapa 1, el informe de revisión y los datos de demostración. Queda realizar la verificación manual en IntelliJ y confirmar entre los integrantes la distribución de tareas y la reflexión del informe. Tampoco se encuentran creados localmente los tags de las etapas.

## Conclusión

El desarrollo permitió aplicar los contenidos de Programación Orientada a Objetos a una agencia de turismo. La separación de responsabilidades ayudó a organizar las reglas de reservas, pagos y salidas. Las pruebas permitieron comprobar tanto los cálculos como la conservación de los datos al volver a abrir el programa. El proyecto reúne el diseño de la primera etapa con una aplicación que permite recorrer las operaciones principales de Rumbo Sur.

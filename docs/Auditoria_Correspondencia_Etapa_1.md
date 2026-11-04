# Correspondencia entre la Etapa 1 y Java

**Actualización técnica posterior:** consultar [Correspondencia_Tecnica.md](Correspondencia_Tecnica.md) para la clasificación de diferencias y [las secuencias actualizadas](uml/README.md). En particular, se comprobó que ServicioAdicional no permite modificar su precio mediante su API actual: la ausencia de precioContratado separado no implica pérdida del precio guardado. Los datos de coordinador y administrador continúan pendientes bajo lectura literal del enunciado. Se conserva esta auditoría como antecedente, sin modificar el PDF original.

Auditoría del 03/11/2026, rama `revision-codex-tpo`. No se modificó código ni el PDF entregado.

## Fuentes y alcance

Se consultaron el enunciado original `TPO_VMAÑANA_POO.pdf`, páginas físicas 6–8, y `TPO POO (1).pdf`: requisitos y descripción de los 32 casos de uso, justificaciones SOLID/GRASP y diagrama final. Se inspeccionaron visualmente las imágenes originales de las dos secuencias (páginas físicas 13 y 14) y del UML final (página física 19, numerada 18). No se utilizó el diagrama inicial como referencia del diseño definitivo.

La correspondencia de los casos de uso se basa en sus descripciones textuales. No se certifica aquí la orientación de todas las flechas include/extend del diagrama general de casos de uso. La auditoría es estática; los resultados de ejecución anteriores están en [Plan_Pruebas_Funcionales.md](Plan_Pruebas_Funcionales.md).

## Resultado general

La estructura principal coincide: entidades, herencias, interfaces, tres controladores y separación de persistencia. Los siete principios y patrones elegidos tienen aplicaciones concretas. No hay equivalencia literal de todas las firmas y atributos. Las secuencias no incorporan todos los cambios del diseño final, y existen simplificaciones que deben explicarse en la documentación de Etapa 2.

## Clases, datos y relaciones

| Elemento del UML | Implementación actual | Evaluación y recomendación |
|---|---|---|
| Tour abstracto y tres especializaciones | `Tour`, `TourAventura`, `TourCultural`, `TourNaturaleza`; código, nombre, descripción, duración y cupos. Aventura conserva edad mínima y dificultad. | Coincide. Mantener. La validación recibe además la fecha de salida para calcular edad correctamente. |
| Composición de Tour con actividades y tarifas, 1..* | Listas de `Actividad` y `Tarifa`; día/orden y temporada/vigencia. | Coincide al programar una salida: se exige itinerario y tarifa. Un tour recién creado puede tener listas vacías. Documentar la etapa de carga, no afirmar que el constructor garantiza 1..*. |
| Actividad con proveedor opcional | Referencia `Proveedor`, que puede ser nula; conserva nombre, CUIT, contacto y servicio. | Coincide con 0..1 proveedor por actividad. No hay registro independiente de proveedores. |
| Salida asociada a un Tour y varias reservas | Referencia al tour y lista de reservas; cada reserva referencia su salida. | Coincide con asociación y agregación. El mínimo se obtiene del Tour, no se duplica como atributo de Salida. Diferencia menor. |
| Guía y vehículo opcionales durante preparación | Referencias inicialmente nulas; se exigen al iniciar. | Coherente con 0..1 y con la operación posterior. Superposiciones se comprueban en `ControladorOperaciones`, no en `Guia.estaDisponible`, método del UML que no existe en Java. Actualizar ubicación de la responsabilidad. |
| Personal abstracto, Guia y Vendedor | Se conserva la herencia. `Personal` tiene documento, nombre, email y teléfono. El legajo está en Vendedor y la matrícula en Guia. | Diferencia media: el UML ubica legajo en Personal. Mantener si se documenta matrícula como identificador laboral del guía. |
| Cliente con nombre y apellido separados | `Cliente.nombre` contiene el nombre completo; conserva documento, email, teléfono y residencia. | Diferencia menor, documentar el campo combinado. |
| Reserva con titular, vendedor, salida y 1..* pasajeros | Referencias obligatorias en constructor; pasajeros cargados antes del alta; controlador valida cantidad y requisitos. | Coincide para reservas registradas. Durante construcción la lista puede estar vacía. |
| Composición Reserva–Pasajero, Reserva–Pago y Reserva–CancelacionReserva | Listas de pasajeros y pagos, cancelación opcional. Pagos y cancelaciones se conservan dentro de la reserva. | Correspondencia de estructura y uso; Java no impide por sí solo compartir una referencia entre objetos. Las copias de listas no equivalen a copias profundas ni prueban propiedad exclusiva. |
| Composición Pasajero–ContratacionServicio y asociación a ServicioAdicional | Cada pasajero crea sus contrataciones; cada contratación conserva pasajero y servicio. | Coincide la responsabilidad. Diferencia relevante en los datos económicos, detallada debajo. |
| Fecha, estados y temporada tipados en UML | Fechas internas como texto validado con `Fecha`; estados y temporada como String. Pantalla DD/MM/AAAA. | Diferencia media de representación. Actualizar documentación; no requiere introducir enumeraciones para forzar igualdad. |
| Pago y MedioPago | Pago conserva fecha, capital, cuotas y referencia a la interfaz; efectivo, transferencia y tarjeta la implementan. | Coincide. `getMontoCobrado()` equivale al cálculo final del UML. |
| Políticas y estrategias | Interfaces `PoliticaCancelacion`, `EstrategiaPromocion` y sus implementaciones. | Coincide con el UML final. El texto OCP abrevia la firma; Java y UML incluyen subtotal como segundo parámetro. |

Las asociaciones inversas que no se almacenan, como una lista de reservas en Cliente, no son obligatorias para materializar una asociación UML sin navegabilidad explícita. No se recomienda agregarlas solo para duplicar información.

## Diferencias que requieren explicación o decisión

| Diferencia real | Importancia | Recomendación |
|---|---|---|
| `ContratacionServicio` no conserva `cantidad` ni `precioContratado` del UML; `calcularImporte()` consulta el precio del servicio. | Relevante: desaparece el precio histórico explícito y la posibilidad de cantidad distinta de uno. | Decidir antes de entregar si se mantiene el supuesto de una unidad por pasajero y servicio sin modificación posterior. Documentarlo; si se exige precio histórico independiente, evaluar corregir código en otro paso. |
| `IRepository` y cuatro repositorios pasan a `Repositorio` y nombres castellanos. `listar` pasa a `obtenerTodos` y se agrega `actualizar`. | Menor, firmas y nombres. | Mantener y actualizar la correspondencia de nombres. |
| `RepositorioReservas` recorre las reservas de las salidas y delega su escritura. No mantiene archivo independiente. | Relevante para persistencia y consistencia. | Mantener. Documentar que salida y reservas se recuperan como un mismo conjunto de objetos. `ArchivoLista` centraliza lectura/escritura. |
| Las notas de ajuste están en `Reserva.notas`, coordinadas por Ventas; el UML coloca registrarNotaAjuste en Administración. | Media: cambia el responsable del caso de uso. | Mantener y actualizar documentación. La operación conserva pagos y registra motivo, fecha y diferencia, no elimina la reserva. |
| El texto GRASP asigna cancelaciones a Administración, pero el UML final las ubica en Ventas. | Inconsistencia interna de Etapa 1. | Aclarar que el código sigue el UML final: `ControladorVentas.cancelarReserva`. No mover código para seguir ambos documentos. |
| Pantallas y gestores de las secuencias no existen como clases independientes. | Media, organización de interacción. | Documentar equivalencia con `MenuPrincipal` y `ControladorVentas`; actualizar secuencias en un anexo de Etapa 2. |
| Selección de tarifa vigente pasa a `Tour.getTarifaVigente`, que conoce todas las tarifas. | Media, responsabilidad trasladada desde Tarifa. | Mantener: permite comparar temporada y vigencias entre varias tarifas. |
| Nuevas clases `Fecha`, `Importes`, `DatosIniciales`, `ArchivoLista`, `ErrorArchivo` y pruebas. Nuevos campos de control, como `listaEmitida` y `registrada`. | Ampliación técnica justificada. | Documentar en el anexo; no representan nuevos requisitos comerciales. |
| Honorarios se calculan y muestran, sin entidad de liquidación pagada. | Alcance que conviene explicitar. | Mantener como cálculo requerido por el caso de uso; no afirmar que registra el pago al guía. |
| Proveedores se cargan dentro de actividades; no hay liquidación a proveedores. | Ambigüedad del enunciado: aparece en contexto, pero no tiene fórmula ni caso mínimo específico. | Confirmar alcance con la profesora o declarar el supuesto. No afirmar cumplimiento de esa operación. |
| Coordinador y administrador son roles del menú, sin entidades de personal ni identificación propia. | Posible brecha respecto del párrafo del enunciado que pide datos de todo el personal; el UML final tampoco incorpora esas subclases. | Resolver el alcance antes de entregar. No añadir autenticación ni gestión de personal sin acordarlo. |

## SOLID y GRASP

| Principio o patrón | Evidencia concreta | Correspondencia |
|---|---|---|
| SRP | `CalculadoraPrecioReserva.calcularTotalReserva`, `calcularPrecioPasajero`, `obtenerMejorDescuento`; `PoliticaCancelacionCliente.calcularDevolucion` y variante Agencia. | Coincide: precios y porcentajes están separados. Reserva conserva coordinación de cancelación, ajustes y estado; no debe afirmarse que carece de toda lógica económica. |
| OCP | Lista de `EstrategiaPromocion`, `obtenerMejorDescuento` y `agregarPromocion`. | Coincide: el recorrido selecciona el mayor descuento sin depender del tipo concreto. Registrar una estrategia nueva sigue siendo necesario. |
| DIP | `Pago` recibe `MedioPago`; `getMontoCobrado` delega a `calcularMontoFinal(importe, cuotas)`. | Coincide. La elección de la implementación está en el menú; el cálculo del recargo está en TarjetaCredito. |
| Experto | `Salida.calcularTotalAdicionalesVendidos` y `Guia.calcularHonorarios`. | Coincide exactamente con la justificación; solo liquida al guía asignado después del cierre. |
| Creador | `Reserva.registrarPago` construye Pago; `Pasajero.contratarServicio` construye ContratacionServicio. | Coincide con el diseño final, aunque la secuencia de pagos anterior muestra otro creador. |
| Controlador | Ventas: `crearReserva`, `cobrar`, `reemplazar`, `cancelarReserva`; Operaciones: `programarSalida`, `asignarGuia`, `iniciarSalida`, `cerrarSalida`; Administración: catálogo, reportes y `liquidarHonorariosGuia`. | Coincide en la separación general, con el traslado de ajustes señalado. Los reportes imprimen desde Administración: separación de presentación parcial, no MVC estricto. |
| Fabricación pura | `Repositorio<T>`, cuatro implementaciones y `ArchivoLista`. | Coincide: el dominio no abre archivos. Ventas crea concretamente RepositorioReservas; no toda construcción está abstraída, sin invalidar este ejemplo de GRASP. |

## Los dos diagramas de secuencia

### Crear reserva — CU2

El diagrama muestra PantallaReserva → GestorReserva → Salida.verificarCupos, alternativa por cupo, creación de Reserva, ciclo de pasajeros y Reserva.calcularTotal.

Java conserva el flujo funcional: `MenuPrincipal.crearReserva` localiza titular y salida, comprueba cupo, crea la reserva y recorre los pasajeros. `ControladorVentas.crearReserva` vuelve a comprobar registro, plazo, cupos y requisitos, delega en la calculadora y guarda. `RepositorioReservas.guardar` llama a `confirmarAlta`, incorporando la reserva a la salida solo después de validar.

La alternativa por falta de cupo está implementada con `CupoSuperadoException`. Se añaden rechazos por edad, seguro, fecha, cliente o salida inexistentes. La diferencia principal es que el menú construye los objetos y la calculadora obtiene el precio, en lugar del gestor y Reserva del dibujo. Recomendación: mantener el código y actualizar la secuencia; volver a `Reserva.calcularTotal` contradiría el SRP final.

### Registrar pagos — CU7

El diagrama muestra un gestor que busca la reserva, calcula directamente el recargo, crea Pago con importe final y solicita confirmar al alcanzar 30 %.

Java hace `MenuPrincipal.registrarPago` → `ControladorVentas.cobrar` → `Reserva.registrarPago` → creación de Pago → `agregarPago` → `verificarEstadoPago`, y luego actualiza el repositorio. `Pago.getMontoCobrado` delega al medio de pago. Se mantiene la alternativa de tarjeta con cuotas y la confirmación automática.

Diferencia relevante: se conserva el capital separado del recargo. Un pago de capital $30 con tarjeta cobra $32,10, pero reduce la deuda en $30. La secuencia antigua no distingue esos importes. También busca la reserva en la entidad en vez de mostrar el repositorio. Recomendación: actualizar el diagrama para reflejar DIP, Creador y persistencia. El menú informa el cobro y permite consultar saldo; no genera un comprobante documental como podría sugerir el texto del dibujo.

## Correspondencia de los casos de uso

| Casos de Etapa 1 | Implementación y alternativas |
|---|---|
| CU1–5: cliente, reserva, pasajeros, cupos y precio | Opciones 2 y 4; Ventas, Salida, Reserva y Calculadora. Se rechazan cupos insuficientes y requisitos incumplidos; se aplica una promoción. |
| CU6–8: adicionales, pagos y vencimientos | Opciones 7 → 1, 5 y 6; seguro requerido en aventura. `controlarPlazos` se ejecuta al abrir y entre operaciones, no mediante un proceso permanente. |
| CU9–12: modificar, reemplazar, cancelar y devolver | Opción 7, Ventas y políticas. Reemplazo hasta tres días; cancelación por agencia o cliente. Fecha tomada de la sesión. |
| CU13–18: programar y controlar salida, guía y transporte | Opciones 3 y 8; Operaciones valida superposición y Salida habilitación/capacidad. Guías y vehículos se ingresan al asignar, no se eligen de un catálogo independiente. |
| CU19–22: lista, requisitos, inicio y cierre | Opción 8; lista con documentos y contactos. Salida controla lista vigente, guía, vehículo, mínimo y saldo; Operaciones controla fecha. |
| CU23–26: catálogo, itinerarios, tarifas y proveedores | Opción 1. Alta de actividades con proveedor opcional. El registro de proveedor existe integrado; no hay pantalla independiente de gestión. |
| CU27–28: honorarios y notas | Opciones 8 → 6 y 7 → 4. Honorarios solo tras cierre; notas sin eliminar pagos. Cambio de controlador para notas. |
| CU29–32: cuatro reportes | Opción 9; `emitirReporteOcupacion` y `emitirReportesEconomicos` reúnen las cuatro consultas. Diferencia menor: salida conjunta en vez de cuatro operaciones separadas. |

## Qué resolver antes de entregar

1. Documentar la evolución de las secuencias, la persistencia conjunta y el responsable de ajustes; conservar el PDF original como antecedente.
2. Acordar y dejar explícito el supuesto de contratación por unidad y el tratamiento del precio contratado.
3. Aclarar el alcance de proveedores y los datos del coordinador/administrador frente al enunciado. Son puntos abiertos, no funcionalidades verificadas.
4. Actualizar firmas y representaciones del UML en un anexo, sin reescribir código correcto solo para coincidir literalmente.

No se propone una reestructuración general. Las decisiones pendientes deben evaluarse antes de autorizar cambios de código. Esta auditoría no equivale a una certificación de todas las combinaciones de reglas de negocio.

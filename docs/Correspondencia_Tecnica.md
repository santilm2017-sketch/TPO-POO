# Correspondencia técnica de la implementación

**Operaciones agrupadas:** el catálogo se muestra al entrar, sin una opción de consulta separada; sus cuatro opciones son tour, tarifa, actividad y personal. Operar salida pasa de seis opciones a tres: asignar guía y vehículo, emitir lista e iniciar (con confirmación s/n), cerrar y mostrar honorarios. Para salidas ya finalizadas, la tercera permite consultar nuevamente el importe. ControladorOperaciones.asignarRecursos reemplaza las dos asignaciones del controlador, valida ambos recursos antes de guardar y conserva las validaciones del dominio. Cancelar durante la entrada o rechazar transporte no deja el guía parcialmente cambiado. Persistencia y objetos serializados no se modifican. Las cuatro modificaciones de reservas siguen separadas porque representan requisitos distintos. Las referencias anteriores al menú deben interpretarse con estas equivalencias.

**Validación de entradas:** MenuPrincipal incorpora ciclos sencillos para repetir fechas, enteros, importes, opciones y valores de texto permitidos. Las operaciones comparten un punto de ejecución con reintento o cancelación. La excepción propia de presentación CargaCancelada permite abandonar una carga con 0 desde cualquier profundidad. Los errores de archivos no se reintentan. No se modificaron reglas ni clases serializadas del dominio. Para importe cero se usa 0,00, ya que 0 cancela. Las notas previas sobre reducción de líneas corresponden a la simplificación anterior; este cambio agrega el manejo interactivo solicitado. Aprobaron 65 comprobaciones unitarias, 6 de archivos y 41 escenarios de consola.

**Simplificación del menú:** se trabaja sobre main actualizado. Se unificaron las consultas en 6, Listar reservas, mostrando también notas de ajuste. El personal se consulta automáticamente al entrar en 1 → 4. Se eliminó el constructor auxiliar de Administración que no tenía llamadas. Persistencia y clases serializadas se conservan para mantener la lectura de los datos existentes; algunos métodos sin llamadas se mantienen por compatibilidad y por representar datos del enunciado. Esta nota reemplaza las referencias anteriores al menú con opción 10.

Se verificaron 65 comprobaciones unitarias, 6 de archivos y 28 escenarios de consola, todos aprobados. Frente a la última versión de revision-tpo, los dos archivos Java modificados pasan de 486 a 473 líneas: reducción neta de 13. El main publicado tenía un menú anterior sin listado: por eso su comparación con el código resultante muestra un aumento, al recuperar esa funcionalidad. No se borraron clases ni se modificaron los objetos serializados para aumentar artificialmente la reducción. La actualización local de main fue un avance al commit ya existente del PR #2, sin crear un commit ni subir cambios.

## Actualización: registro de personal incorporado

Esta actualización reemplaza los pendientes de personal que se conservan debajo como antecedente de la auditoría. Se revisaron los cinco archivos de Correccion_Personal_Rumbo_Sur.zip y se incorporaron PersonalAdministrativo, RepositorioPersonal, PersonalAdministrativoTest y las modificaciones de ControladorAdministrativo y MenuPrincipal. Se corrigió además la validación de documento duplicado al actualizar y se agregó RepositorioPersonalTest.

Coordinador y administrador se representan mediante PersonalAdministrativo con rol validado. Hereda nombre, documento, teléfono y email de Personal y agrega legajo. Se registran y consultan desde la opción 10; se guardan en personal.dat mediante ArchivoLista. Se conserva el legajo de Vendedor y la matrícula/legajo de Guia. No se trasladó el atributo a Personal: evita cambiar la estructura serializada de las clases existentes. Es una diferencia de ubicación respecto del UML, no una ausencia de los datos requeridos.

Verificación: compilación con `--release 17`; 65 comprobaciones unitarias, 6 de persistencia y 27 casos de consola aprobados, sin fallas. Los 27 comprenden F01–F24 y los tres casos de personal del plan. Se usaron carpetas temporales. El registro de ambos roles, recuperación tras reinicio y rechazo de duplicados quedaron comprobados. F25 de IntelliJ y la validación gráfica/sintáctica de Mermaid siguen pendientes, al igual que la aclaración del alcance de liquidaciones a proveedores. No se implementaron liquidaciones ni se redactó el informe final.

**Reverificación del 04/11/2026:** el código vuelve a compilar y aprueba 55 comprobaciones unitarias y F01–F24 (24 casos funcionales). Los resultados de recuperación de archivos y fechas DD/MM/AAAA se reprodujeron en carpetas temporales. F25 continúa pendiente. El resto de las menciones a ejecuciones de este documento describe la revisión anterior. El hash remoto de main consultado sin cambiar referencias coincide con main local y HEAD: `40c7ea614aa45798d96cefb0c1dbcf3146435ff4`. No se detectaron avances adicionales en main remoto en esa consulta. Continúan pendientes los datos del personal y la validación de las secuencias con un parser Mermaid, además de confirmar el alcance de proveedores.

Revisión del 03/11/2026. Este documento complementa la auditoría y registra el estado técnico observado. No es el informe final del TPO ni atribuye decisiones al grupo. El PDF de la Etapa 1 no se modifica.

## Clasificación de diferencias

| Diferencia | Clasificación | Resolución técnica o acción pendiente |
|---|---|---|
| Cantidad en ContratacionServicio | Diseño válido que necesita documentarse. | El enunciado exige contratación por pasajero, no cantidades variables. Cada objeto representa una unidad y `calcularImporte()` devuelve el precio del servicio. No es obligatorio agregar cantidad solo porque figure en el UML. |
| Precio contratado | Diseño válido en el alcance actual. | `ServicioAdicional.precio` es privado, se fija en el constructor y no tiene setter. El servicio se guarda junto con la contratación. Por ello el importe no se pierde al reiniciar ni existe una operación para cambiarlo. No hace falta duplicarlo hoy; si se incorpora edición de precios de servicios, habrá que preservar el precio histórico por contratación. Se precisa así la observación preliminar de la auditoría. |
| Personal: legajo en la superclase | Diferencia de diseño. | Vendedor tiene legajo y Guia matrícula; Personal conserva nombre, documento y teléfono, además de email. Se propone documentar matrícula como identificador laboral, sujeto a confirmación del grupo. |
| Coordinador y administrador | Requisito de datos no cubierto bajo lectura literal del enunciado. | La página 8 pide legajo, nombre, documento y teléfono de todo el personal. Actualmente son roles de uso, sin objetos ni registro de sus datos. No puede darse por cumplido solo por existir controladores. Ver propuesta mínima debajo. |
| Gestión de proveedores | Diseño válido con alcance acotado. | Se registran nombre, CUIT, contacto y servicio al cargar una actividad y se guardan con el tour. Esto cubre los datos y la asociación solicitados. No existe catálogo independiente ni edición general de proveedores; no se debe describir esa funcionalidad en el README. |
| Nombres y firmas UML/Java | Diferencias menores, salvo cambios de responsabilidad ya identificados. | Repositorios en castellano; `obtenerTodos` en lugar de `listar`; fechas internas validadas por Fecha, estados String, nombre completo del cliente en un campo. Se conservan las interfaces, herencias y responsabilidades principales. |
| Tarifa vigente y disponibilidad del guía | Diseño válido. | Tour selecciona entre sus tarifas; Operaciones compara las salidas para detectar superposición. Son las clases que cuentan con los datos necesarios. No agregar métodos duplicados para imitar firmas del dibujo. |
| Persistencia conjunta | Diseño válido. | RepositorioReservas busca dentro de las salidas y delega su actualización a RepositorioSalidas. `salidas-reservas.dat` conserva el conjunto; clientes y tours tienen sus archivos. Se evita mantener dos versiones independientes de una reserva. |
| Notas de ajuste | Diseño válido. | Ventas localiza y actualiza la reserva; `Reserva.ajustar` conserva pagos y agrega fecha, motivo, total anterior y diferencia. La ubicación difiere de Administración en el UML. No requiere una clase nueva ni mover código. |
| Honorarios del guía | Requisito de cálculo implementado. | `Guia.calcularHonorarios` exige salida finalizada y guía asignado: tarifa diaria por duración más 5 % de adicionales vigentes. El menú muestra el importe; no registra un desembolso ni un comprobante de liquidación. |
| Liquidaciones a proveedores | Alcance ambiguo que requiere confirmación. | Aparecen en el contexto de la página 6, pero los casos mínimos y reglas económicas definen solamente honorarios de guías. No hay cálculo ni registro de liquidaciones a proveedores. No inventar importes o reglas: confirmar con la cátedra si se exige esa operación. |
| Secuencias originales | Documentación desactualizada. | Las versiones de [docs/uml](uml/README.md) reflejan cálculo separado, estrategias, creación del pago por Reserva y persistencia. Los originales se conservan. |

## Cambio mínimo propuesto para los datos del personal

**Cierre de sesión del 03/11/2026:** no se inició esta implementación. Cambiar los campos de Personal, Guia o Vendedor exige comprobar la compatibilidad de los objetos ya serializados; no se debe probar una migración sobre los datos originales. Para retomarlo: definir el legajo común, incorporar Coordinador y Administrador, acordar una carga simple, reutilizar ArchivoLista para su recuperación y verificar tanto un reinicio como la lectura de copias de los datos anteriores. Si cambia el formato serializado, resolver su compatibilidad antes de reemplazar las clases. No hay código parcial de esta funcionalidad.

Si se mantiene la interpretación literal, habría que incorporar representaciones sencillas de Coordinador y Administrador que hereden de Personal, conservar sus cuatro datos e incluir su registro y recuperación. También habría que unificar el legajo común o documentar claramente el identificador equivalente del guía. Solo crear dos clases vacías no resuelve el registro de esos datos.

Antes de implementar, el grupo debe confirmar si la cátedra admite tratarlos exclusivamente como actores. Si no lo admite, corresponde acordar una carga simple de esos datos y su persistencia. No hacen falta contraseñas, permisos, autenticación ni una nueva aplicación de gestión de empleados. No se implementó este cambio en esta revisión, porque afecta modelo, carga y archivos existentes.

## Revisión y pruebas

No se modificaron fuentes Java ni dependencias. Se recompiló todo `src` con Temurin 24 y destino Java 17 y se ejecutaron nuevamente:

- `Test.CalculadoraPrecioReservaTest`: 33 comprobaciones aprobadas.
- `Test.ReservaSalidaTest`: 22 comprobaciones aprobadas.

Total: **55 aprobadas y 0 fallidas**. Se mantiene la advertencia de conversión de tipos de `ArchivoLista`. No se volvieron a ejecutar los 24 casos funcionales: sus resultados anteriores siguen identificados en el plan, sobre el mismo código de negocio. No se presentan como una nueva ejecución.

El menú convierte las entradas con `Fecha.desdePantalla` y presenta fechas mediante `Fecha.mostrar`; las fechas visibles y los ejemplos usan DD/MM/AAAA. El formato interno no se cambia. README y plan conservan las mismas clases de prueba, comandos, cantidades y datos de ejemplo.

## Prueba funcional pendiente F25: IntelliJ

1. Abrir el proyecto en IntelliJ y seleccionar el JDK instalado en Project Structure; marcar `src` como Sources Root si fuera necesario.
2. Crear una carpeta nueva y vacía fuera de los datos del proyecto, por ejemplo en el directorio temporal de Windows. Configurar esa carpeta como Working directory de `Presentacion.MenuPrincipal`.
3. Ejecutar su `main` e ingresar `03/11/2026`.
4. Elegir 6 y localizar reserva 1 en el listado. Debe indicar CONFIRMADA, total $36.000,00 y saldo $25.200,00.
5. Salir con 0 y volver a ejecutar sobre la misma carpeta y con la misma fecha. Consultar otra vez la reserva y comprobar los mismos valores.
6. Ejecutar los `main` de las dos clases de Test. Deben completar 33 y 22 mensajes `[OK]` sin excepción.
7. Registrar fecha, JDK utilizado y resultados reales en F25 del plan. Marcar APROBADO solo después de completar el recorrido; si difiere, registrar FALLIDO y el error observado.

F25 sigue pendiente. La exportación visual de los diagramas también necesita revisión, pero no es otra prueba funcional del programa.

## Estado para la última revisión

El código existente conserva sus funcionalidades y las pruebas unitarias aprueban. La documentación técnica explica las diferencias sin forzar cambios innecesarios. Está preparado para una última revisión, pero no se declara cumplimiento total ni integración lista hasta resolver los datos del personal, confirmar el alcance de proveedores y completar F25. El informe final queda a cargo del grupo.

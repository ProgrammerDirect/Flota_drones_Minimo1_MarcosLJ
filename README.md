# Mínimo 1 DSA — Gestión de una flota de drones

Actualmente este proyecto implementa API REST (Jersey, Grizzly y Swagger) para gestionar una flota de drones junto con sus pilotos, sus planes de vuelo y la administración de su mantenimento. 

## Estado actual

- Actualmente este proyecto resuelve las siguientes operaciones de gestión:
  - Se pueden añadir drones.
  - Se pueden añadir pilotos.
  - Se listan los drones ordenados descendentemente por horas de vuelo.
  - Se listan los pilotos ordenados descendentemente por horas de vuelo.
  - Se guardan los drones en un almacén en caso de que deban hacer tareas de mantenimiento con él.
  - Se repara o realizan las tareas de mantenimiento en un dron en el almacén.
  - Se pueden crear planes de vuelo.
  - Se listan los planes de vuelo que han sido asignados a un piloto.
  - Se listan los planes de vuelo que han sido asignados a un dron.

- Funcionamiento interno
  - Las estructuras de datos utilizadas son:
    - HashMap de drones y otro de pilotos con el id como clave.
    - Un Linkedlist usado como pila para la gestión del almacén.
    - LinkedList para guardar los planes de vuelo en las clases Dron y Piloto.
  - La fachada 'GestorDronesImpl' implementa la interfaz 'GestorDrones' y es un Singleton.
  - Se añaden funciones de excepciones propias a las comunes en el lenguaje Java.
  - Se añaden test a las operaciones de gestión para verificar su correcto funcionamiento.
  - Los métodos incluyen una TRAZA de LOG4J.
  - La gestión de dependencias se realiza mediante Maven, donde se incluyen JUnit y log4j.

- Servicio de REST
  - /drones
    - 'POST /drones' para añadir drones.
    - 'POST /drones/almacen/reparar' para reparar el dron más cercano a la puerta.
    - 'POST /drones/almacen/{id}' para guardar un dron en el almacén.
    - 'GET /drones/porHorasVuelo' para obtener planes de vuelo de un dron ordenado descendientemente por horas de vuelo.
    - 'GET /drones/{id}/planesVuelo' para obtener los planes de vuelo de un dron.

  - /planesVuelo
    - 'POST /planesVuelo' para crear planes de vuelo.

  - /pilotos
    - 'POST /pilotos' para añadir pilotos.
    - 'GET /pilotos/porHorasVuelo' para obtener planes de vuelo de un piloto ordenado descendientemente por horas de vuelo.
    - 'GET /pilotos/{id}/planesVuelo' para obtener los planes de vuelo de un piloto.
  
  - Códigos de respuesta: 201 (correcto), 404 (no existe o almacén vacío), 409 (conflicto: dron ya en el almacén, en mantenimiento o plan solapado) y 500 (error de validación).


## Comprobación
- Tests: Ejecutar 'GestorDronesTest'.
- API: Ejecutar 'Main' y abrir Swagger en 'http://localhost:8080/swagger/index.html'
- Página web principal: 'http://localhost:8080' 




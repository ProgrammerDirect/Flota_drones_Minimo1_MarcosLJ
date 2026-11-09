const API_URL = "http://localhost:8080/dsaApp";

// Pinta tarjetas a partir de un endpoint. "pintar" decide qué mostrar de cada objeto.
async function cargarTarjetas(ruta, idContenedor, pintar) {
    const contenedor = document.getElementById(idContenedor);
    try {
        const respuesta = await fetch(API_URL + ruta);

        if (!respuesta.ok) {
            throw new Error(`Error HTTP: ${respuesta.status}`);
        }

        const datos = await respuesta.json();
        contenedor.innerHTML = "";

        datos.forEach(item => {
            const tarjeta = document.createElement('div');
            tarjeta.classList.add('track-card');
            tarjeta.innerHTML = pintar(item);
            contenedor.appendChild(tarjeta);
        });

    } catch (error) {
        console.error("Error al cargar " + ruta + ":", error);
        contenedor.innerHTML = `<p style="color: red; text-align: center;">No se pudo conectar con la API en ${API_URL}${ruta}</p>`;
    }
}

const pintarDron = d => `
    <p><span class="label">Nombre:</span> ${d.nombre}</p>
    <p><span class="label">Fabricante:</span> ${d.fabricante}</p>
    <p><span class="label">Modelo:</span> ${d.modelo}</p>
    <p><span class="label">Horas:</span> ${d.horasVuelo}</p>
    <p><span class="label">Estado:</span> ${d.enMantenimiento ? 'En almacén' : 'Operativo'}</p>
    <small>Ref: ${d.id}</small>
`;

const pintarPiloto = p => `
    <p><span class="label">Nombre:</span> ${p.nombre} ${p.apellidos}</p>
    <p><span class="label">Horas:</span> ${p.horasVuelo}</p>
    <small>Ref: ${p.id}</small>
`;

cargarTarjetas("/drones/porHorasVuelo", "drones", pintarDron);
cargarTarjetas("/pilotos/porHorasVuelo", "pilotos", pintarPiloto);

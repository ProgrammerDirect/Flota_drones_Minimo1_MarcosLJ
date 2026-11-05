package edu.upc.dsa.services;


import edu.upc.dsa.domain.models.Dron;
import edu.upc.dsa.domain.models.PlanVuelo;

import edu.upc.dsa.domain.repository.Drones.GestorDrones;
import edu.upc.dsa.domain.repository.Drones.GestorDronesImpl;

import edu.upc.dsa.exceptions.*;
import edu.upc.dsa.util.RandomUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "/drones", description = "Endpoint del servicio de Drones")
@Path("/drones")

public class DronesService {

    private GestorDrones gd;

    public DronesService()
    {
        this.gd = GestorDronesImpl.getInstance();
    }


    @GET
    @ApiOperation(value = "obtener todos los drones", notes = "Retorna todos los drones")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Dron.class, responseContainer = "List"),
    })
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDrones() {
        List<Dron> drones = this.gd.getTodosDrones();
        GenericEntity<List<Dron>> entity = new GenericEntity<List<Dron>>(drones) {};
        return Response.status(201).entity(entity).build();
    }

    @GET
    @ApiOperation(value = "drones por horas de vuelo", notes = "Drones ordenados por horas de vuelo (descendente)")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Dron.class, responseContainer = "List"),
    })
    @Path("/porHorasVuelo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDronesPorHorasVuelo() {
        List<Dron> drones = this.gd.getDronesPorHorasVuelo();
        GenericEntity<List<Dron>> entity = new GenericEntity<List<Dron>>(drones) {};
        return Response.status(201).entity(entity).build();
    }

    @GET
    @ApiOperation(value = "obtener un dron", notes = "Devuelve un dron")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Dron.class),
            @ApiResponse(code = 404, message = "Dron no encontrado")
    })
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDron(@PathParam("id") String id) {
        Dron d = this.gd.getDron(id);
        if (d == null) return Response.status(404).build();
        else return Response.status(201).entity(d).build();
    }

    @POST
    @ApiOperation(value = "añadir un dron", notes = "Añadir un nuevo dron")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Dron.class),
            @ApiResponse(code = 500, message = "Error de validación")
    })
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response nuevoDron(Dron dron) {

        if (dron == null || dron.getNombre() == null || dron.getFabricante() == null || dron.getModelo() == null)
            return Response.status(500).entity(dron).build();

        if (dron.getId() == null || dron.getId().trim().isEmpty())
            dron.setId(RandomUtils.getId());

        if (this.gd.getDron(dron.getId()) != null)
            return Response.status(500).entity(dron).build();

        this.gd.anadirDron(dron.getId(), dron.getNombre(), dron.getFabricante(), dron.getModelo());
        return Response.status(201).entity(this.gd.getDron(dron.getId())).build();
    }

    @PUT
    @ApiOperation(value = "actualizar un dron", notes = "Actualiza nombre, fabricante y modelo")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 404, message = "Dron no encontrado")
    })
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarDron(Dron dron) {
        Dron d = this.gd.actualizarDron(dron);
        if (d == null) return Response.status(404).build();
        return Response.status(201).build();
    }

    @DELETE
    @ApiOperation(value = "eliminar un dron", notes = "Eliminar un dron")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful"),
            @ApiResponse(code = 404, message = "Dron no encontrado")
    })
    @Path("/{id}")
    public Response eliminarDron(@PathParam("id") String id) {
        if (this.gd.getDron(id) == null) return Response.status(404).build();
        this.gd.eliminarDron(id);
        return Response.status(201).build();
    }


    @POST
    @ApiOperation(value = "guardar un dron en el almacén", notes = "Guarda un dron en el almacén para mantenimiento (pila)")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Dron.class),
            @ApiResponse(code = 404, message = "Dron no encontrado"),
            @ApiResponse(code = 409, message = "El dron ya está en el almacén")
    })
    @Path("/almacen/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response guardarDronEnAlmacen(@PathParam("id") String id) {
        try {
            this.gd.guardarDronEnAlmacen(id);
            return Response.status(201).entity(this.gd.getDron(id)).build();
        } catch (DronNoEncontradoException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (DronEnMantenimientoException e) {
            return Response.status(409).entity(e.getMessage()).build();
        }
    }

    @POST
    @ApiOperation(value = "reparar un dron", notes = "Repara el dron más cercano a la puerta y lo saca del almacén")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Dron.class),
            @ApiResponse(code = 404, message = "Almacén vacío")
    })
    @Path("/almacen/reparar")
    @Produces(MediaType.APPLICATION_JSON)
    public Response repararDron() {
        try {
            Dron d = this.gd.repararDron();
            return Response.status(201).entity(d).build();
        } catch (AlmacenVacioException e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
    }


    @POST
    @ApiOperation(value = "crear un plan de vuelo", notes = "Crea un plan de vuelo. Fecha en formato yyyy-MM-ddTHH:mm")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = PlanVuelo.class),
            @ApiResponse(code = 404, message = "Dron o piloto no encontrado"),
            @ApiResponse(code = 409, message = "Dron en mantenimiento o conflicto de horario"),
            @ApiResponse(code = 500, message = "Error de validación")
    })
    @Path("/planesVuelo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearPlanVuelo(PlanVuelo plan) {

        if (plan == null || plan.getIdDron() == null || plan.getIdPiloto() == null || plan.getFecha() == null
                || plan.getHorasEstimadas() <= 0 || plan.getPosiciones() == null || plan.getPosiciones().isEmpty())
            return Response.status(500).entity(plan).build();

        if (plan.getId() == null || plan.getId().trim().isEmpty())
            plan.setId(RandomUtils.getId());

        try {
            PlanVuelo creado = this.gd.crearPlanVuelo(plan);
            return Response.status(201).entity(creado).build();
        } catch (DronNoEncontradoException | PilotoNoEncontradoException e) {
            return Response.status(404).entity(e.getMessage()).build();
        } catch (DronEnMantenimientoException | ConflictoPlanVueloException e) {
            return Response.status(409).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {     // fecha mal formada, horas <= 0
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @ApiOperation(value = "planes de vuelo de un dron", notes = "Planes de vuelo asignados a un dron")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = PlanVuelo.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Dron no encontrado")
    })
    @Path("/{id}/planesVuelo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlanesVueloDron(@PathParam("id") String id) {
        try {
            List<PlanVuelo> planes = this.gd.getPlanesVueloDron(id);
            GenericEntity<List<PlanVuelo>> entity = new GenericEntity<List<PlanVuelo>>(planes) {};
            return Response.status(201).entity(entity).build();
        } catch (DronNoEncontradoException e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
    }
}

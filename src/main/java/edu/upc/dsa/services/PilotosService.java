package edu.upc.dsa.services;


import edu.upc.dsa.domain.models.Piloto;
import edu.upc.dsa.domain.models.PlanVuelo;

import edu.upc.dsa.domain.repository.Drones.GestorDrones;
import edu.upc.dsa.domain.repository.Drones.GestorDronesImpl;

import edu.upc.dsa.exceptions.PilotoNoEncontradoException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Api(value = "/pilotos", description = "Endpoint del servicio de Pilotos")
@Path("/pilotos")

public class PilotosService {

    private GestorDrones gd;

    public PilotosService()
    {
        this.gd = GestorDronesImpl.getInstance();
    }

    @GET
    @ApiOperation(value = "obtener todos los pilotos", notes = "Retorna todos los pilotos")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Piloto.class, responseContainer = "List"),
    })
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPilotos() {
        List<Piloto> pilotos = this.gd.getTodosPilotos();
        GenericEntity<List<Piloto>> entity = new GenericEntity<List<Piloto>>(pilotos) {};
        return Response.status(201).entity(entity).build();
    }

    @GET
    @ApiOperation(value = "pilotos por horas de vuelo", notes = "Pilotos ordenados por horas de vuelo (descendente)")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Piloto.class, responseContainer = "List"),
    })
    @Path("/porHorasVuelo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPilotosPorHorasVuelo() {
        List<Piloto> pilotos = this.gd.getPilotosPorHorasVuelo();
        GenericEntity<List<Piloto>> entity = new GenericEntity<List<Piloto>>(pilotos) {};
        return Response.status(201).entity(entity).build();
    }

    @GET
    @ApiOperation(value = "obtener un piloto", notes = "Devuelve un piloto")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Piloto.class),
            @ApiResponse(code = 404, message = "Piloto no encontrado")
    })
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPiloto(@PathParam("id") String id) {
        Piloto p = this.gd.getPiloto(id);
        if (p == null) return Response.status(404).build();
        else return Response.status(201).entity(p).build();
    }

    @POST
    @ApiOperation(value = "añadir un piloto", notes = "Añadir un nuevo piloto")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = Piloto.class),
            @ApiResponse(code = 500, message = "Error de validación")
    })
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response nuevoPiloto(Piloto piloto) {

        if (piloto == null || piloto.getId() == null || piloto.getId().trim().isEmpty()
                || piloto.getNombre() == null || piloto.getApellidos() == null)
            return Response.status(500).entity(piloto).build();

        if (this.gd.getPiloto(piloto.getId()) != null)
            return Response.status(500).entity(piloto).build();

        this.gd.anadirPiloto(piloto.getId(), piloto.getNombre(), piloto.getApellidos());
        return Response.status(201).entity(this.gd.getPiloto(piloto.getId())).build();
    }

    @GET
    @ApiOperation(value = "planes de vuelo de un piloto", notes = "Planes de vuelo asignados a un piloto")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = PlanVuelo.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Piloto no encontrado")
    })
    @Path("/{id}/planesVuelo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlanesVueloPiloto(@PathParam("id") String id) {
        try {
            List<PlanVuelo> planes = this.gd.getPlanesVueloPiloto(id);
            GenericEntity<List<PlanVuelo>> entity = new GenericEntity<List<PlanVuelo>>(planes) {};
            return Response.status(201).entity(entity).build();
        } catch (PilotoNoEncontradoException e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
    }
}

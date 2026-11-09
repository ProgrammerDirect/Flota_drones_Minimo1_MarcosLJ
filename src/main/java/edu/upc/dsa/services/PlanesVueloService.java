package edu.upc.dsa.services;


import edu.upc.dsa.domain.models.Piloto;
import edu.upc.dsa.domain.models.PlanVuelo;

import edu.upc.dsa.domain.repository.Drones.GestorDrones;
import edu.upc.dsa.domain.repository.Drones.GestorDronesImpl;

import edu.upc.dsa.exceptions.ConflictoPlanVueloException;
import edu.upc.dsa.exceptions.DronEnMantenimientoException;
import edu.upc.dsa.exceptions.DronNoEncontradoException;
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

@Api(value = "/planesVuelo", description = "Endpoint del servicio de Planes de Vuelo")
@Path("/planesVuelo")

public class PlanesVueloService {

    private GestorDrones gd;

    public PlanesVueloService()
    {
        this.gd = GestorDronesImpl.getInstance();
    }


    @POST
    @ApiOperation(value = "crear un plan de vuelo", notes = "Creación de un plan de vuelo. Es importante recordar que la fecha sigue el formato yyyy-MM-ddTHH:mm")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successful", response = PlanVuelo.class),
            @ApiResponse(code = 404, message = "Dron o piloto no encontrado"),
            @ApiResponse(code = 409, message = "Dron en mantenimiento o conflicto de horario"),
            @ApiResponse(code = 500, message = "Error de validación")
    })
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearPlanVuelo(PlanVuelo plan) {

        if (plan == null || plan.getIdDron() == null || plan.getIdPiloto() == null || plan.getFecha() == null
                         || plan.getHorasEstimadas() <= 0 || plan.getPosiciones() == null || plan.getPosiciones().isEmpty())
            return Response.status(500).entity(plan).build();

        try {
            PlanVuelo creado = this.gd.crearPlanVuelo(plan);
            return Response.status(201).entity(creado).build();
        }
        catch (DronNoEncontradoException | PilotoNoEncontradoException e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
        catch (DronEnMantenimientoException | ConflictoPlanVueloException e) {
            return Response.status(409).entity(e.getMessage()).build();
        }

        catch (IllegalArgumentException e) {     // En caso de no introducir el formato de hora correcto, horas <= 0
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

}
